package androidx.camera.core;

import android.os.Handler;
import android.os.Looper;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CaptureProcessor;
import androidx.camera.core.impl.CaptureStage;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.SingleImageProxyBundle;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

final class ProcessingSurface extends DeferrableSurface {
    private static final int MAX_IMAGES = 2;
    private static final String TAG = "ProcessingSurfaceTextur";
    private final CameraCaptureCallback mCameraCaptureCallback;
    final CaptureProcessor mCaptureProcessor;
    final CaptureStage mCaptureStage;
    private final Handler mImageReaderHandler;
    final MetadataImageReader mInputImageReader;
    final Surface mInputSurface;
    final Object mLock = new Object();
    private final DeferrableSurface mOutputDeferrableSurface;
    boolean mReleased;
    private final Size mResolution;
    private String mTagBundleKey;
    private final ImageReaderProxy.OnImageAvailableListener mTransformedListener;

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-core-ProcessingSurface  reason: not valid java name */
    public /* synthetic */ void m157lambda$new$0$androidxcameracoreProcessingSurface(ImageReaderProxy reader) {
        synchronized (this.mLock) {
            imageIncoming(reader);
        }
    }

    ProcessingSurface(int width, int height, int format, Handler handler, CaptureStage captureStage, CaptureProcessor captureProcessor, DeferrableSurface outputSurface, String tagBundleKey) {
        super(new Size(width, height), format);
        ProcessingSurface$$ExternalSyntheticLambda0 processingSurface$$ExternalSyntheticLambda0 = new ProcessingSurface$$ExternalSyntheticLambda0(this);
        this.mTransformedListener = processingSurface$$ExternalSyntheticLambda0;
        this.mReleased = false;
        Size size = new Size(width, height);
        this.mResolution = size;
        if (handler != null) {
            this.mImageReaderHandler = handler;
        } else {
            Looper looper = Looper.myLooper();
            if (looper != null) {
                this.mImageReaderHandler = new Handler(looper);
            } else {
                throw new IllegalStateException("Creating a ProcessingSurface requires a non-null Handler, or be created  on a thread with a Looper.");
            }
        }
        Executor executor = CameraXExecutors.newHandlerExecutor(this.mImageReaderHandler);
        MetadataImageReader metadataImageReader = new MetadataImageReader(width, height, format, 2);
        this.mInputImageReader = metadataImageReader;
        metadataImageReader.setOnImageAvailableListener(processingSurface$$ExternalSyntheticLambda0, executor);
        this.mInputSurface = metadataImageReader.getSurface();
        this.mCameraCaptureCallback = metadataImageReader.getCameraCaptureCallback();
        this.mCaptureProcessor = captureProcessor;
        captureProcessor.onResolutionUpdate(size);
        this.mCaptureStage = captureStage;
        this.mOutputDeferrableSurface = outputSurface;
        this.mTagBundleKey = tagBundleKey;
        Futures.addCallback(outputSurface.getSurface(), new FutureCallback<Surface>() {
            public void onSuccess(Surface surface) {
                synchronized (ProcessingSurface.this.mLock) {
                    ProcessingSurface.this.mCaptureProcessor.onOutputSurface(surface, 1);
                }
            }

            public void onFailure(Throwable t) {
                Logger.e(ProcessingSurface.TAG, "Failed to extract Listenable<Surface>.", t);
            }
        }, CameraXExecutors.directExecutor());
        getTerminationFuture().addListener(new ProcessingSurface$$ExternalSyntheticLambda1(this), CameraXExecutors.directExecutor());
    }

    public ListenableFuture<Surface> provideSurface() {
        ListenableFuture<Surface> immediateFuture;
        synchronized (this.mLock) {
            immediateFuture = Futures.immediateFuture(this.mInputSurface);
        }
        return immediateFuture;
    }

    /* access modifiers changed from: package-private */
    public CameraCaptureCallback getCameraCaptureCallback() {
        CameraCaptureCallback cameraCaptureCallback;
        synchronized (this.mLock) {
            if (!this.mReleased) {
                cameraCaptureCallback = this.mCameraCaptureCallback;
            } else {
                throw new IllegalStateException("ProcessingSurface already released!");
            }
        }
        return cameraCaptureCallback;
    }

    /* access modifiers changed from: private */
    public void release() {
        synchronized (this.mLock) {
            if (!this.mReleased) {
                this.mInputImageReader.close();
                this.mInputSurface.release();
                this.mOutputDeferrableSurface.close();
                this.mReleased = true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void imageIncoming(ImageReaderProxy imageReader) {
        if (!this.mReleased) {
            ImageProxy image = null;
            try {
                image = imageReader.acquireNextImage();
            } catch (IllegalStateException e) {
                Logger.e(TAG, "Failed to acquire next image.", e);
            }
            if (image != null) {
                ImageInfo imageInfo = image.getImageInfo();
                if (imageInfo == null) {
                    image.close();
                    return;
                }
                Integer tagValue = (Integer) imageInfo.getTagBundle().getTag(this.mTagBundleKey);
                if (tagValue == null) {
                    image.close();
                } else if (this.mCaptureStage.getId() != tagValue.intValue()) {
                    Logger.w(TAG, "ImageProxyBundle does not contain this id: " + tagValue);
                    image.close();
                } else {
                    SingleImageProxyBundle imageProxyBundle = new SingleImageProxyBundle(image, this.mTagBundleKey);
                    try {
                        incrementUseCount();
                        this.mCaptureProcessor.process(imageProxyBundle);
                        imageProxyBundle.close();
                        decrementUseCount();
                    } catch (DeferrableSurface.SurfaceClosedException e2) {
                        Logger.d(TAG, "The ProcessingSurface has been closed. Don't process the incoming image.");
                        imageProxyBundle.close();
                    }
                }
            }
        }
    }
}
