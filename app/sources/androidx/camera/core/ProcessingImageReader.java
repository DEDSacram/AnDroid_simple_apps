package androidx.camera.core;

import android.media.ImageReader;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CaptureBundle;
import androidx.camera.core.impl.CaptureProcessor;
import androidx.camera.core.impl.CaptureStage;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ProcessingImageReader implements ImageReaderProxy {
    private static final int EXIF_MAX_SIZE_BYTES = 64000;
    private static final String TAG = "ProcessingImageReader";
    private final List<Integer> mCaptureIdList = new ArrayList();
    final CaptureProcessor mCaptureProcessor;
    private FutureCallback<List<ImageProxy>> mCaptureStageReadyCallback = new FutureCallback<List<ImageProxy>>() {
        /* JADX WARNING: Code restructure failed: missing block: B:10:?, code lost:
            r6.this$0.mCaptureProcessor.process(r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0027, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0028, code lost:
            r4 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
            monitor-enter(r6.this$0.mLock);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:?, code lost:
            r6.this$0.mSettableImageProxyBundle.reset();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0039, code lost:
            r3.execute(new androidx.camera.core.ProcessingImageReader$3$$ExternalSyntheticLambda0(r2, r4));
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSuccess(java.util.List<androidx.camera.core.ImageProxy> r7) {
            /*
                r6 = this;
                androidx.camera.core.ProcessingImageReader r0 = androidx.camera.core.ProcessingImageReader.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                androidx.camera.core.ProcessingImageReader r1 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ all -> 0x0059 }
                boolean r1 = r1.mClosed     // Catch:{ all -> 0x0059 }
                if (r1 == 0) goto L_0x000d
                monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                return
            L_0x000d:
                androidx.camera.core.ProcessingImageReader r1 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ all -> 0x0059 }
                r2 = 1
                r1.mProcessing = r2     // Catch:{ all -> 0x0059 }
                androidx.camera.core.ProcessingImageReader r1 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ all -> 0x0059 }
                androidx.camera.core.SettableImageProxyBundle r1 = r1.mSettableImageProxyBundle     // Catch:{ all -> 0x0059 }
                androidx.camera.core.ProcessingImageReader r2 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ all -> 0x0059 }
                androidx.camera.core.ProcessingImageReader$OnProcessingErrorCallback r2 = r2.mOnProcessingErrorCallback     // Catch:{ all -> 0x0059 }
                androidx.camera.core.ProcessingImageReader r3 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ all -> 0x0059 }
                java.util.concurrent.Executor r3 = r3.mErrorCallbackExecutor     // Catch:{ all -> 0x0059 }
                monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                androidx.camera.core.ProcessingImageReader r0 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ Exception -> 0x0027 }
                androidx.camera.core.impl.CaptureProcessor r0 = r0.mCaptureProcessor     // Catch:{ Exception -> 0x0027 }
                r0.process(r1)     // Catch:{ Exception -> 0x0027 }
                goto L_0x0042
            L_0x0027:
                r0 = move-exception
                r4 = r0
                androidx.camera.core.ProcessingImageReader r0 = androidx.camera.core.ProcessingImageReader.this
                java.lang.Object r5 = r0.mLock
                monitor-enter(r5)
                androidx.camera.core.ProcessingImageReader r0 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ all -> 0x0056 }
                androidx.camera.core.SettableImageProxyBundle r0 = r0.mSettableImageProxyBundle     // Catch:{ all -> 0x0056 }
                r0.reset()     // Catch:{ all -> 0x0056 }
                if (r2 == 0) goto L_0x0041
                if (r3 == 0) goto L_0x0041
                androidx.camera.core.ProcessingImageReader$3$$ExternalSyntheticLambda0 r0 = new androidx.camera.core.ProcessingImageReader$3$$ExternalSyntheticLambda0     // Catch:{ all -> 0x0056 }
                r0.<init>(r2, r4)     // Catch:{ all -> 0x0056 }
                r3.execute(r0)     // Catch:{ all -> 0x0056 }
            L_0x0041:
                monitor-exit(r5)     // Catch:{ all -> 0x0056 }
            L_0x0042:
                androidx.camera.core.ProcessingImageReader r0 = androidx.camera.core.ProcessingImageReader.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                androidx.camera.core.ProcessingImageReader r4 = androidx.camera.core.ProcessingImageReader.this     // Catch:{ all -> 0x0053 }
                r5 = 0
                r4.mProcessing = r5     // Catch:{ all -> 0x0053 }
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                androidx.camera.core.ProcessingImageReader r0 = androidx.camera.core.ProcessingImageReader.this
                r0.closeAndCompleteFutureIfNecessary()
                return
            L_0x0053:
                r4 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0053 }
                throw r4
            L_0x0056:
                r0 = move-exception
                monitor-exit(r5)     // Catch:{ all -> 0x0056 }
                throw r0
            L_0x0059:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0059 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.ProcessingImageReader.AnonymousClass3.onSuccess(java.util.List):void");
        }

        public void onFailure(Throwable throwable) {
        }
    };
    CallbackToFutureAdapter.Completer<Void> mCloseCompleter;
    private ListenableFuture<Void> mCloseFuture;
    boolean mClosed = false;
    Executor mErrorCallbackExecutor;
    Executor mExecutor;
    private ImageReaderProxy.OnImageAvailableListener mImageProcessedListener = new ImageReaderProxy.OnImageAvailableListener() {
        public void onImageAvailable(ImageReaderProxy reader) {
            ImageReaderProxy.OnImageAvailableListener listener;
            Executor executor;
            synchronized (ProcessingImageReader.this.mLock) {
                listener = ProcessingImageReader.this.mListener;
                executor = ProcessingImageReader.this.mExecutor;
                ProcessingImageReader.this.mSettableImageProxyBundle.reset();
                ProcessingImageReader.this.setupSettableImageProxyBundleCallbacks();
            }
            if (listener == null) {
                return;
            }
            if (executor != null) {
                executor.execute(new ProcessingImageReader$2$$ExternalSyntheticLambda0(this, listener));
            } else {
                listener.onImageAvailable(ProcessingImageReader.this);
            }
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onImageAvailable$0$androidx-camera-core-ProcessingImageReader$2  reason: not valid java name */
        public /* synthetic */ void m156lambda$onImageAvailable$0$androidxcameracoreProcessingImageReader$2(ImageReaderProxy.OnImageAvailableListener listener) {
            listener.onImageAvailable(ProcessingImageReader.this);
        }
    };
    final ImageReaderProxy mInputImageReader;
    ImageReaderProxy.OnImageAvailableListener mListener;
    final Object mLock = new Object();
    OnProcessingErrorCallback mOnProcessingErrorCallback;
    final ImageReaderProxy mOutputImageReader;
    final Executor mPostProcessExecutor;
    boolean mProcessing = false;
    SettableImageProxyBundle mSettableImageProxyBundle = new SettableImageProxyBundle(Collections.emptyList(), this.mTagBundleKey);
    private ListenableFuture<List<ImageProxy>> mSettableImageProxyFutureList = Futures.immediateFuture(new ArrayList());
    private String mTagBundleKey = new String();
    private ImageReaderProxy.OnImageAvailableListener mTransformedListener = new ImageReaderProxy.OnImageAvailableListener() {
        public void onImageAvailable(ImageReaderProxy reader) {
            ProcessingImageReader.this.imageIncoming(reader);
        }
    };
    private final ListenableFuture<Void> mUnderlyingCaptureProcessorCloseFuture;

    interface OnProcessingErrorCallback {
        void notifyProcessingError(String str, Throwable th);
    }

    ProcessingImageReader(Builder builder) {
        if (builder.mInputImageReader.getMaxImages() >= builder.mCaptureBundle.getCaptureStages().size()) {
            ImageReaderProxy imageReaderProxy = builder.mInputImageReader;
            this.mInputImageReader = imageReaderProxy;
            int outputWidth = imageReaderProxy.getWidth();
            int outputHeight = imageReaderProxy.getHeight();
            if (builder.mOutputFormat == 256) {
                outputWidth = ((int) (((float) (outputWidth * outputHeight)) * 1.5f)) + EXIF_MAX_SIZE_BYTES;
                outputHeight = 1;
            }
            AndroidImageReaderProxy androidImageReaderProxy = new AndroidImageReaderProxy(ImageReader.newInstance(outputWidth, outputHeight, builder.mOutputFormat, imageReaderProxy.getMaxImages()));
            this.mOutputImageReader = androidImageReaderProxy;
            this.mPostProcessExecutor = builder.mPostProcessExecutor;
            CaptureProcessor captureProcessor = builder.mCaptureProcessor;
            this.mCaptureProcessor = captureProcessor;
            captureProcessor.onOutputSurface(androidImageReaderProxy.getSurface(), builder.mOutputFormat);
            captureProcessor.onResolutionUpdate(new Size(imageReaderProxy.getWidth(), imageReaderProxy.getHeight()));
            this.mUnderlyingCaptureProcessorCloseFuture = captureProcessor.getCloseFuture();
            setCaptureBundle(builder.mCaptureBundle);
            return;
        }
        throw new IllegalArgumentException("MetadataImageReader is smaller than CaptureBundle.");
    }

    public ImageProxy acquireLatestImage() {
        ImageProxy acquireLatestImage;
        synchronized (this.mLock) {
            acquireLatestImage = this.mOutputImageReader.acquireLatestImage();
        }
        return acquireLatestImage;
    }

    public ImageProxy acquireNextImage() {
        ImageProxy acquireNextImage;
        synchronized (this.mLock) {
            acquireNextImage = this.mOutputImageReader.acquireNextImage();
        }
        return acquireNextImage;
    }

    public void close() {
        synchronized (this.mLock) {
            if (!this.mClosed) {
                this.mInputImageReader.clearOnImageAvailableListener();
                this.mOutputImageReader.clearOnImageAvailableListener();
                this.mClosed = true;
                this.mCaptureProcessor.close();
                closeAndCompleteFutureIfNecessary();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void closeAndCompleteFutureIfNecessary() {
        boolean closed;
        boolean processing;
        CallbackToFutureAdapter.Completer<Void> closeCompleter;
        synchronized (this.mLock) {
            closed = this.mClosed;
            processing = this.mProcessing;
            closeCompleter = this.mCloseCompleter;
            if (closed && !processing) {
                this.mInputImageReader.close();
                this.mSettableImageProxyBundle.close();
                this.mOutputImageReader.close();
            }
        }
        if (closed && !processing) {
            this.mUnderlyingCaptureProcessorCloseFuture.addListener(new ProcessingImageReader$$ExternalSyntheticLambda2(this, closeCompleter), CameraXExecutors.directExecutor());
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$closeAndCompleteFutureIfNecessary$0$androidx-camera-core-ProcessingImageReader  reason: not valid java name */
    public /* synthetic */ void m154lambda$closeAndCompleteFutureIfNecessary$0$androidxcameracoreProcessingImageReader(CallbackToFutureAdapter.Completer closeCompleter) {
        cancelSettableImageProxyBundleFutureList();
        if (closeCompleter != null) {
            closeCompleter.set(null);
        }
    }

    /* access modifiers changed from: package-private */
    public ListenableFuture<Void> getCloseFuture() {
        ListenableFuture<Void> closeFuture;
        synchronized (this.mLock) {
            if (!this.mClosed || this.mProcessing) {
                if (this.mCloseFuture == null) {
                    this.mCloseFuture = CallbackToFutureAdapter.getFuture(new ProcessingImageReader$$ExternalSyntheticLambda1(this));
                }
                closeFuture = Futures.nonCancellationPropagating(this.mCloseFuture);
            } else {
                closeFuture = Futures.transform(this.mUnderlyingCaptureProcessorCloseFuture, ProcessingImageReader$$ExternalSyntheticLambda0.INSTANCE, CameraXExecutors.directExecutor());
            }
        }
        return closeFuture;
    }

    static /* synthetic */ Void lambda$getCloseFuture$1(Void nullVoid) {
        return null;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getCloseFuture$2$androidx-camera-core-ProcessingImageReader  reason: not valid java name */
    public /* synthetic */ Object m155lambda$getCloseFuture$2$androidxcameracoreProcessingImageReader(CallbackToFutureAdapter.Completer completer) throws Exception {
        synchronized (this.mLock) {
            this.mCloseCompleter = completer;
        }
        return "ProcessingImageReader-close";
    }

    public int getHeight() {
        int height;
        synchronized (this.mLock) {
            height = this.mInputImageReader.getHeight();
        }
        return height;
    }

    public int getWidth() {
        int width;
        synchronized (this.mLock) {
            width = this.mInputImageReader.getWidth();
        }
        return width;
    }

    public int getImageFormat() {
        int imageFormat;
        synchronized (this.mLock) {
            imageFormat = this.mOutputImageReader.getImageFormat();
        }
        return imageFormat;
    }

    public int getMaxImages() {
        int maxImages;
        synchronized (this.mLock) {
            maxImages = this.mInputImageReader.getMaxImages();
        }
        return maxImages;
    }

    public Surface getSurface() {
        Surface surface;
        synchronized (this.mLock) {
            surface = this.mInputImageReader.getSurface();
        }
        return surface;
    }

    public void setOnImageAvailableListener(ImageReaderProxy.OnImageAvailableListener listener, Executor executor) {
        synchronized (this.mLock) {
            this.mListener = (ImageReaderProxy.OnImageAvailableListener) Preconditions.checkNotNull(listener);
            this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
            this.mInputImageReader.setOnImageAvailableListener(this.mTransformedListener, executor);
            this.mOutputImageReader.setOnImageAvailableListener(this.mImageProcessedListener, executor);
        }
    }

    public void clearOnImageAvailableListener() {
        synchronized (this.mLock) {
            this.mListener = null;
            this.mExecutor = null;
            this.mInputImageReader.clearOnImageAvailableListener();
            this.mOutputImageReader.clearOnImageAvailableListener();
            if (!this.mProcessing) {
                this.mSettableImageProxyBundle.close();
            }
        }
    }

    public void setCaptureBundle(CaptureBundle captureBundle) {
        synchronized (this.mLock) {
            if (!this.mClosed) {
                cancelSettableImageProxyBundleFutureList();
                if (captureBundle.getCaptureStages() != null) {
                    if (this.mInputImageReader.getMaxImages() >= captureBundle.getCaptureStages().size()) {
                        this.mCaptureIdList.clear();
                        for (CaptureStage captureStage : captureBundle.getCaptureStages()) {
                            if (captureStage != null) {
                                this.mCaptureIdList.add(Integer.valueOf(captureStage.getId()));
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("CaptureBundle is larger than InputImageReader.");
                    }
                }
                this.mTagBundleKey = Integer.toString(captureBundle.hashCode());
                this.mSettableImageProxyBundle = new SettableImageProxyBundle(this.mCaptureIdList, this.mTagBundleKey);
                setupSettableImageProxyBundleCallbacks();
            }
        }
    }

    private void cancelSettableImageProxyBundleFutureList() {
        synchronized (this.mLock) {
            if (!this.mSettableImageProxyFutureList.isDone()) {
                this.mSettableImageProxyFutureList.cancel(true);
            }
            this.mSettableImageProxyBundle.reset();
        }
    }

    public String getTagBundleKey() {
        return this.mTagBundleKey;
    }

    /* access modifiers changed from: package-private */
    public CameraCaptureCallback getCameraCaptureCallback() {
        synchronized (this.mLock) {
            ImageReaderProxy imageReaderProxy = this.mInputImageReader;
            if (imageReaderProxy instanceof MetadataImageReader) {
                CameraCaptureCallback cameraCaptureCallback = ((MetadataImageReader) imageReaderProxy).getCameraCaptureCallback();
                return cameraCaptureCallback;
            }
            AnonymousClass4 r1 = new CameraCaptureCallback() {
            };
            return r1;
        }
    }

    public void setOnProcessingErrorCallback(Executor executor, OnProcessingErrorCallback callback) {
        synchronized (this.mLock) {
            this.mErrorCallbackExecutor = executor;
            this.mOnProcessingErrorCallback = callback;
        }
    }

    /* access modifiers changed from: package-private */
    public void setupSettableImageProxyBundleCallbacks() {
        List<ListenableFuture<ImageProxy>> futureList = new ArrayList<>();
        for (Integer id : this.mCaptureIdList) {
            futureList.add(this.mSettableImageProxyBundle.getImageProxy(id.intValue()));
        }
        this.mSettableImageProxyFutureList = Futures.allAsList(futureList);
        Futures.addCallback(Futures.allAsList(futureList), this.mCaptureStageReadyCallback, this.mPostProcessExecutor);
    }

    /* access modifiers changed from: package-private */
    public void imageIncoming(ImageReaderProxy imageReader) {
        synchronized (this.mLock) {
            if (!this.mClosed) {
                ImageProxy image = null;
                try {
                    ImageProxy image2 = imageReader.acquireNextImage();
                    if (image2 != null) {
                        Integer tagValue = (Integer) image2.getImageInfo().getTagBundle().getTag(this.mTagBundleKey);
                        if (!this.mCaptureIdList.contains(tagValue)) {
                            Logger.w(TAG, "ImageProxyBundle does not contain this id: " + tagValue);
                            image2.close();
                        } else {
                            this.mSettableImageProxyBundle.addImageProxy(image2);
                        }
                    }
                } catch (IllegalStateException e) {
                    try {
                        Logger.e(TAG, "Failed to acquire latest image.", e);
                        if (image != null) {
                            Integer tagValue2 = (Integer) image.getImageInfo().getTagBundle().getTag(this.mTagBundleKey);
                            if (!this.mCaptureIdList.contains(tagValue2)) {
                                Logger.w(TAG, "ImageProxyBundle does not contain this id: " + tagValue2);
                                image.close();
                            } else {
                                this.mSettableImageProxyBundle.addImageProxy(image);
                            }
                        }
                    } catch (Throwable th) {
                        if (image != null) {
                            Integer tagValue3 = (Integer) image.getImageInfo().getTagBundle().getTag(this.mTagBundleKey);
                            if (!this.mCaptureIdList.contains(tagValue3)) {
                                Logger.w(TAG, "ImageProxyBundle does not contain this id: " + tagValue3);
                                image.close();
                            } else {
                                this.mSettableImageProxyBundle.addImageProxy(image);
                            }
                        }
                        throw th;
                    }
                }
            }
        }
    }

    static final class Builder {
        protected final CaptureBundle mCaptureBundle;
        protected final CaptureProcessor mCaptureProcessor;
        protected final ImageReaderProxy mInputImageReader;
        protected int mOutputFormat;
        protected Executor mPostProcessExecutor;

        Builder(ImageReaderProxy imageReader, CaptureBundle captureBundle, CaptureProcessor captureProcessor) {
            this.mPostProcessExecutor = Executors.newSingleThreadExecutor();
            this.mInputImageReader = imageReader;
            this.mCaptureBundle = captureBundle;
            this.mCaptureProcessor = captureProcessor;
            this.mOutputFormat = imageReader.getImageFormat();
        }

        Builder(int width, int height, int inputFormat, int maxImages, CaptureBundle captureBundle, CaptureProcessor captureProcessor) {
            this(new MetadataImageReader(width, height, inputFormat, maxImages), captureBundle, captureProcessor);
        }

        /* access modifiers changed from: package-private */
        public Builder setPostProcessExecutor(Executor postProcessExecutor) {
            this.mPostProcessExecutor = postProcessExecutor;
            return this;
        }

        /* access modifiers changed from: package-private */
        public Builder setOutputFormat(int outputFormat) {
            this.mOutputFormat = outputFormat;
            return this;
        }

        /* access modifiers changed from: package-private */
        public ProcessingImageReader build() {
            return new ProcessingImageReader(this);
        }
    }
}
