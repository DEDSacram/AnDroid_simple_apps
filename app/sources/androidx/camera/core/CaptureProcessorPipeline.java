package androidx.camera.core;

import android.media.ImageReader;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.impl.CaptureProcessor;
import androidx.camera.core.impl.ImageProxyBundle;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

class CaptureProcessorPipeline implements CaptureProcessor {
    private static final String TAG = "CaptureProcessorPipeline";
    CallbackToFutureAdapter.Completer<Void> mCloseCompleter;
    private ListenableFuture<Void> mCloseFuture;
    private boolean mClosed = false;
    final Executor mExecutor;
    private ImageReaderProxy mIntermediateImageReader = null;
    private final Object mLock = new Object();
    private final int mMaxImages;
    private final CaptureProcessor mPostCaptureProcessor;
    private final CaptureProcessor mPreCaptureProcessor;
    private boolean mProcessing = false;
    private ImageInfo mSourceImageInfo = null;
    private final ListenableFuture<List<Void>> mUnderlyingCaptureProcessorsCloseFuture;

    CaptureProcessorPipeline(CaptureProcessor preCaptureProcessor, int maxImages, CaptureProcessor postCaptureProcessor, Executor executor) {
        this.mPreCaptureProcessor = preCaptureProcessor;
        this.mPostCaptureProcessor = postCaptureProcessor;
        List<ListenableFuture<Void>> closeFutureList = new ArrayList<>();
        closeFutureList.add(preCaptureProcessor.getCloseFuture());
        closeFutureList.add(postCaptureProcessor.getCloseFuture());
        this.mUnderlyingCaptureProcessorsCloseFuture = Futures.allAsList(closeFutureList);
        this.mExecutor = executor;
        this.mMaxImages = maxImages;
    }

    public void onOutputSurface(Surface surface, int imageFormat) {
        this.mPostCaptureProcessor.onOutputSurface(surface, imageFormat);
    }

    public void process(ImageProxyBundle bundle) {
        synchronized (this.mLock) {
            if (!this.mClosed) {
                this.mProcessing = true;
                ListenableFuture<ImageProxy> imageProxyListenableFuture = bundle.getImageProxy(bundle.getCaptureIds().get(0).intValue());
                Preconditions.checkArgument(imageProxyListenableFuture.isDone());
                try {
                    this.mSourceImageInfo = ((ImageProxy) imageProxyListenableFuture.get()).getImageInfo();
                    this.mPreCaptureProcessor.process(bundle);
                } catch (InterruptedException | ExecutionException e) {
                    throw new IllegalArgumentException("Can not successfully extract ImageProxy from the ImageProxyBundle.");
                }
            }
        }
    }

    public void onResolutionUpdate(Size size) {
        AndroidImageReaderProxy androidImageReaderProxy = new AndroidImageReaderProxy(ImageReader.newInstance(size.getWidth(), size.getHeight(), 35, this.mMaxImages));
        this.mIntermediateImageReader = androidImageReaderProxy;
        this.mPreCaptureProcessor.onOutputSurface(androidImageReaderProxy.getSurface(), 35);
        this.mPreCaptureProcessor.onResolutionUpdate(size);
        this.mPostCaptureProcessor.onResolutionUpdate(size);
        this.mIntermediateImageReader.setOnImageAvailableListener(new CaptureProcessorPipeline$$ExternalSyntheticLambda1(this), CameraXExecutors.directExecutor());
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onResolutionUpdate$1$androidx-camera-core-CaptureProcessorPipeline  reason: not valid java name */
    public /* synthetic */ void m136lambda$onResolutionUpdate$1$androidxcameracoreCaptureProcessorPipeline(ImageReaderProxy imageReader) {
        ImageProxy image = imageReader.acquireNextImage();
        try {
            this.mExecutor.execute(new CaptureProcessorPipeline$$ExternalSyntheticLambda3(this, image));
        } catch (RejectedExecutionException e) {
            Logger.e(TAG, "The executor for post-processing might have been shutting down or terminated!");
            image.close();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: postProcess */
    public void m135lambda$onResolutionUpdate$0$androidxcameracoreCaptureProcessorPipeline(ImageProxy imageProxy) {
        boolean closed;
        synchronized (this.mLock) {
            closed = this.mClosed;
        }
        if (!closed) {
            Size resolution = new Size(imageProxy.getWidth(), imageProxy.getHeight());
            Preconditions.checkNotNull(this.mSourceImageInfo);
            String tagBundleKey = this.mSourceImageInfo.getTagBundle().listKeys().iterator().next();
            int captureId = ((Integer) this.mSourceImageInfo.getTagBundle().getTag(tagBundleKey)).intValue();
            SettableImageProxy settableImageProxy = new SettableImageProxy(imageProxy, resolution, this.mSourceImageInfo);
            this.mSourceImageInfo = null;
            SettableImageProxyBundle settableImageProxyBundle = new SettableImageProxyBundle(Collections.singletonList(Integer.valueOf(captureId)), tagBundleKey);
            settableImageProxyBundle.addImageProxy(settableImageProxy);
            try {
                this.mPostCaptureProcessor.process(settableImageProxyBundle);
            } catch (Exception e) {
                Logger.e(TAG, "Post processing image failed! " + e.getMessage());
            }
        }
        synchronized (this.mLock) {
            this.mProcessing = false;
        }
        closeAndCompleteFutureIfNecessary();
    }

    public void close() {
        synchronized (this.mLock) {
            if (!this.mClosed) {
                this.mClosed = true;
                this.mPreCaptureProcessor.close();
                this.mPostCaptureProcessor.close();
                closeAndCompleteFutureIfNecessary();
            }
        }
    }

    private void closeAndCompleteFutureIfNecessary() {
        boolean closed;
        boolean processing;
        CallbackToFutureAdapter.Completer<Void> closeCompleter;
        synchronized (this.mLock) {
            closed = this.mClosed;
            processing = this.mProcessing;
            closeCompleter = this.mCloseCompleter;
            if (closed && !processing) {
                this.mIntermediateImageReader.close();
            }
        }
        if (closed && !processing && closeCompleter != null) {
            this.mUnderlyingCaptureProcessorsCloseFuture.addListener(new CaptureProcessorPipeline$$ExternalSyntheticLambda4(closeCompleter), CameraXExecutors.directExecutor());
        }
    }

    public ListenableFuture<Void> getCloseFuture() {
        ListenableFuture<Void> closeFuture;
        synchronized (this.mLock) {
            if (!this.mClosed || this.mProcessing) {
                if (this.mCloseFuture == null) {
                    this.mCloseFuture = CallbackToFutureAdapter.getFuture(new CaptureProcessorPipeline$$ExternalSyntheticLambda2(this));
                }
                closeFuture = Futures.nonCancellationPropagating(this.mCloseFuture);
            } else {
                closeFuture = Futures.transform(this.mUnderlyingCaptureProcessorsCloseFuture, CaptureProcessorPipeline$$ExternalSyntheticLambda0.INSTANCE, CameraXExecutors.directExecutor());
            }
        }
        return closeFuture;
    }

    static /* synthetic */ Void lambda$getCloseFuture$3(List nullVoid) {
        return null;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getCloseFuture$4$androidx-camera-core-CaptureProcessorPipeline  reason: not valid java name */
    public /* synthetic */ Object m134lambda$getCloseFuture$4$androidxcameracoreCaptureProcessorPipeline(CallbackToFutureAdapter.Completer completer) throws Exception {
        synchronized (this.mLock) {
            this.mCloseCompleter = completer;
        }
        return "CaptureProcessorPipeline-close";
    }
}
