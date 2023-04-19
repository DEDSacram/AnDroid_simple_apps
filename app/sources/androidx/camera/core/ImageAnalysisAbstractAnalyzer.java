package androidx.camera.core;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ImageWriter;
import android.os.Build;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.impl.ImageReaderProxy;
import androidx.camera.core.impl.TagBundle;
import androidx.camera.core.internal.compat.ImageWriterCompat;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.os.OperationCanceledException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

abstract class ImageAnalysisAbstractAnalyzer implements ImageReaderProxy.OnImageAvailableListener {
    private static final RectF NORMALIZED_RECT = new RectF(-1.0f, -1.0f, 1.0f, 1.0f);
    private static final String TAG = "ImageAnalysisAnalyzer";
    private final Object mAnalyzerLock = new Object();
    protected boolean mIsAttached = true;
    private volatile boolean mOnePixelShiftEnabled;
    private Matrix mOriginalSensorToBufferTransformMatrix = new Matrix();
    private Rect mOriginalViewPortCropRect = new Rect();
    private volatile int mOutputImageFormat = 1;
    private volatile boolean mOutputImageRotationEnabled;
    private volatile int mPrevBufferRotationDegrees;
    private SafeCloseImageReaderProxy mProcessedImageReaderProxy;
    private ImageWriter mProcessedImageWriter;
    ByteBuffer mRGBConvertedBuffer;
    private volatile int mRelativeRotation;
    private ImageAnalysis.Analyzer mSubscribedAnalyzer;
    ByteBuffer mURotatedBuffer;
    private Matrix mUpdatedSensorToBufferTransformMatrix = new Matrix();
    private Rect mUpdatedViewPortCropRect = new Rect();
    private Executor mUserExecutor;
    ByteBuffer mVRotatedBuffer;
    ByteBuffer mYRotatedBuffer;

    /* access modifiers changed from: package-private */
    public abstract ImageProxy acquireImage(ImageReaderProxy imageReaderProxy);

    /* access modifiers changed from: package-private */
    public abstract void clearCache();

    /* access modifiers changed from: package-private */
    public abstract void onValidImageAvailable(ImageProxy imageProxy);

    ImageAnalysisAbstractAnalyzer() {
    }

    public void onImageAvailable(ImageReaderProxy imageReaderProxy) {
        try {
            ImageProxy imageProxy = acquireImage(imageReaderProxy);
            if (imageProxy != null) {
                onValidImageAvailable(imageProxy);
            }
        } catch (IllegalStateException e) {
            Logger.e(TAG, "Failed to acquire image.", e);
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00a6  */
    com.google.common.util.concurrent.ListenableFuture<java.lang.Void> analyzeImage(androidx.camera.core.ImageProxy r26) {
        /*
            r25 = this;
            r9 = r25
            r10 = r26
            boolean r0 = r9.mOutputImageRotationEnabled
            r8 = 0
            if (r0 == 0) goto L_0x000c
            int r0 = r9.mRelativeRotation
            goto L_0x000d
        L_0x000c:
            r0 = r8
        L_0x000d:
            r11 = r0
            java.lang.Object r1 = r9.mAnalyzerLock
            monitor-enter(r1)
            java.util.concurrent.Executor r0 = r9.mUserExecutor     // Catch:{ all -> 0x0114 }
            r12 = r0
            androidx.camera.core.ImageAnalysis$Analyzer r0 = r9.mSubscribedAnalyzer     // Catch:{ all -> 0x0114 }
            r13 = r0
            boolean r0 = r9.mOutputImageRotationEnabled     // Catch:{ all -> 0x0114 }
            r14 = 1
            if (r0 == 0) goto L_0x0022
            int r0 = r9.mPrevBufferRotationDegrees     // Catch:{ all -> 0x0114 }
            if (r11 == r0) goto L_0x0022
            r0 = r14
            goto L_0x0023
        L_0x0022:
            r0 = r8
        L_0x0023:
            r15 = r0
            if (r15 == 0) goto L_0x0029
            r9.recreateImageReaderProxy(r10, r11)     // Catch:{ all -> 0x0114 }
        L_0x0029:
            boolean r0 = r9.mOutputImageRotationEnabled     // Catch:{ all -> 0x0114 }
            if (r0 == 0) goto L_0x0030
            r25.createHelperBuffer(r26)     // Catch:{ all -> 0x0114 }
        L_0x0030:
            androidx.camera.core.SafeCloseImageReaderProxy r0 = r9.mProcessedImageReaderProxy     // Catch:{ all -> 0x0114 }
            r7 = r0
            android.media.ImageWriter r0 = r9.mProcessedImageWriter     // Catch:{ all -> 0x0114 }
            r16 = r0
            java.nio.ByteBuffer r0 = r9.mRGBConvertedBuffer     // Catch:{ all -> 0x0114 }
            r6 = r0
            java.nio.ByteBuffer r0 = r9.mYRotatedBuffer     // Catch:{ all -> 0x0114 }
            r17 = r0
            java.nio.ByteBuffer r0 = r9.mURotatedBuffer     // Catch:{ all -> 0x0114 }
            r18 = r0
            java.nio.ByteBuffer r0 = r9.mVRotatedBuffer     // Catch:{ all -> 0x0114 }
            r19 = r0
            monitor-exit(r1)     // Catch:{ all -> 0x0114 }
            if (r13 == 0) goto L_0x0104
            if (r12 == 0) goto L_0x0104
            boolean r0 = r9.mIsAttached
            if (r0 == 0) goto L_0x0104
            r0 = 0
            if (r7 == 0) goto L_0x0098
            int r1 = r9.mOutputImageFormat
            r2 = 2
            if (r1 != r2) goto L_0x0064
            boolean r1 = r9.mOnePixelShiftEnabled
            androidx.camera.core.ImageProxy r0 = androidx.camera.core.ImageProcessingUtil.convertYUVToRGB(r10, r7, r6, r11, r1)
            r22 = r0
            r20 = r6
            r21 = r7
            goto L_0x009e
        L_0x0064:
            int r1 = r9.mOutputImageFormat
            if (r1 != r14) goto L_0x0093
            boolean r1 = r9.mOnePixelShiftEnabled
            if (r1 == 0) goto L_0x006f
            androidx.camera.core.ImageProcessingUtil.applyPixelShiftForYUV(r26)
        L_0x006f:
            if (r16 == 0) goto L_0x008e
            if (r17 == 0) goto L_0x008e
            if (r18 == 0) goto L_0x008e
            if (r19 == 0) goto L_0x008e
            r1 = r26
            r2 = r7
            r3 = r16
            r4 = r17
            r5 = r18
            r20 = r6
            r6 = r19
            r21 = r7
            r7 = r11
            androidx.camera.core.ImageProxy r0 = androidx.camera.core.ImageProcessingUtil.rotateYUV(r1, r2, r3, r4, r5, r6, r7)
            r22 = r0
            goto L_0x009e
        L_0x008e:
            r20 = r6
            r21 = r7
            goto L_0x009c
        L_0x0093:
            r20 = r6
            r21 = r7
            goto L_0x009c
        L_0x0098:
            r20 = r6
            r21 = r7
        L_0x009c:
            r22 = r0
        L_0x009e:
            if (r22 != 0) goto L_0x00a1
            r8 = r14
        L_0x00a1:
            r14 = r8
            if (r14 == 0) goto L_0x00a6
            r6 = r10
            goto L_0x00a8
        L_0x00a6:
            r6 = r22
        L_0x00a8:
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r8 = r0
            android.graphics.Matrix r0 = new android.graphics.Matrix
            r0.<init>()
            r7 = r0
            java.lang.Object r1 = r9.mAnalyzerLock
            monitor-enter(r1)
            if (r15 == 0) goto L_0x00d7
            if (r14 != 0) goto L_0x00d7
            int r0 = r26.getWidth()     // Catch:{ all -> 0x00d1 }
            int r2 = r26.getHeight()     // Catch:{ all -> 0x00d1 }
            int r3 = r6.getWidth()     // Catch:{ all -> 0x00d1 }
            int r4 = r6.getHeight()     // Catch:{ all -> 0x00d1 }
            r9.recalculateTransformMatrixAndCropRect(r0, r2, r3, r4)     // Catch:{ all -> 0x00d1 }
            goto L_0x00d7
        L_0x00d1:
            r0 = move-exception
            r23 = r7
            r24 = r8
            goto L_0x0100
        L_0x00d7:
            r9.mPrevBufferRotationDegrees = r11     // Catch:{ all -> 0x00fb }
            android.graphics.Rect r0 = r9.mUpdatedViewPortCropRect     // Catch:{ all -> 0x00fb }
            r8.set(r0)     // Catch:{ all -> 0x00fb }
            android.graphics.Matrix r0 = r9.mUpdatedSensorToBufferTransformMatrix     // Catch:{ all -> 0x00fb }
            r7.set(r0)     // Catch:{ all -> 0x00fb }
            monitor-exit(r1)     // Catch:{ all -> 0x00fb }
            androidx.camera.core.ImageAnalysisAbstractAnalyzer$$ExternalSyntheticLambda0 r0 = new androidx.camera.core.ImageAnalysisAbstractAnalyzer$$ExternalSyntheticLambda0
            r1 = r0
            r2 = r25
            r3 = r12
            r4 = r26
            r5 = r7
            r23 = r7
            r7 = r8
            r24 = r8
            r8 = r13
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)
            com.google.common.util.concurrent.ListenableFuture r0 = androidx.concurrent.futures.CallbackToFutureAdapter.getFuture(r0)
            goto L_0x0113
        L_0x00fb:
            r0 = move-exception
            r23 = r7
            r24 = r8
        L_0x0100:
            monitor-exit(r1)     // Catch:{ all -> 0x0102 }
            throw r0
        L_0x0102:
            r0 = move-exception
            goto L_0x0100
        L_0x0104:
            r20 = r6
            r21 = r7
            androidx.core.os.OperationCanceledException r0 = new androidx.core.os.OperationCanceledException
            java.lang.String r1 = "No analyzer or executor currently set."
            r0.<init>(r1)
            com.google.common.util.concurrent.ListenableFuture r0 = androidx.camera.core.impl.utils.futures.Futures.immediateFailedFuture(r0)
        L_0x0113:
            return r0
        L_0x0114:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0114 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.ImageAnalysisAbstractAnalyzer.analyzeImage(androidx.camera.core.ImageProxy):com.google.common.util.concurrent.ListenableFuture");
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$analyzeImage$1$androidx-camera-core-ImageAnalysisAbstractAnalyzer  reason: not valid java name */
    public /* synthetic */ Object m139lambda$analyzeImage$1$androidxcameracoreImageAnalysisAbstractAnalyzer(Executor executor, ImageProxy imageProxy, Matrix transformMatrix, ImageProxy outputImageProxy, Rect cropRect, ImageAnalysis.Analyzer analyzer, CallbackToFutureAdapter.Completer completer) throws Exception {
        Executor executor2 = executor;
        executor.execute(new ImageAnalysisAbstractAnalyzer$$ExternalSyntheticLambda1(this, imageProxy, transformMatrix, outputImageProxy, cropRect, analyzer, completer));
        return "analyzeImage";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$analyzeImage$0$androidx-camera-core-ImageAnalysisAbstractAnalyzer  reason: not valid java name */
    public /* synthetic */ void m138lambda$analyzeImage$0$androidxcameracoreImageAnalysisAbstractAnalyzer(ImageProxy imageProxy, Matrix transformMatrix, ImageProxy outputImageProxy, Rect cropRect, ImageAnalysis.Analyzer analyzer, CallbackToFutureAdapter.Completer completer) {
        int i;
        if (this.mIsAttached) {
            TagBundle tagBundle = imageProxy.getImageInfo().getTagBundle();
            long timestamp = imageProxy.getImageInfo().getTimestamp();
            if (this.mOutputImageRotationEnabled) {
                i = 0;
            } else {
                i = this.mRelativeRotation;
            }
            ImageProxy outputSettableImageProxy = new SettableImageProxy(outputImageProxy, ImmutableImageInfo.create(tagBundle, timestamp, i, transformMatrix));
            if (!cropRect.isEmpty()) {
                outputSettableImageProxy.setCropRect(cropRect);
            }
            analyzer.analyze(outputSettableImageProxy);
            completer.set(null);
            return;
        }
        completer.setException(new OperationCanceledException("ImageAnalysis is detached"));
    }

    private static SafeCloseImageReaderProxy createImageReaderProxy(int imageWidth, int imageHeight, int rotation, int format, int maxImages) {
        boolean flipWH = rotation == 90 || rotation == 270;
        return new SafeCloseImageReaderProxy(ImageReaderProxys.createIsolatedReader(flipWH ? imageHeight : imageWidth, flipWH ? imageWidth : imageHeight, format, maxImages));
    }

    /* access modifiers changed from: package-private */
    public void setRelativeRotation(int relativeRotation) {
        this.mRelativeRotation = relativeRotation;
    }

    /* access modifiers changed from: package-private */
    public void setOutputImageRotationEnabled(boolean outputImageRotationEnabled) {
        this.mOutputImageRotationEnabled = outputImageRotationEnabled;
    }

    /* access modifiers changed from: package-private */
    public void setOutputImageFormat(int outputImageFormat) {
        this.mOutputImageFormat = outputImageFormat;
    }

    /* access modifiers changed from: package-private */
    public void setOnePixelShiftEnabled(boolean onePixelShiftEnabled) {
        this.mOnePixelShiftEnabled = onePixelShiftEnabled;
    }

    /* access modifiers changed from: package-private */
    public void setViewPortCropRect(Rect viewPortCropRect) {
        synchronized (this.mAnalyzerLock) {
            this.mOriginalViewPortCropRect = viewPortCropRect;
            this.mUpdatedViewPortCropRect = new Rect(this.mOriginalViewPortCropRect);
        }
    }

    /* access modifiers changed from: package-private */
    public void setSensorToBufferTransformMatrix(Matrix sensorToBufferTransformMatrix) {
        synchronized (this.mAnalyzerLock) {
            this.mOriginalSensorToBufferTransformMatrix = sensorToBufferTransformMatrix;
            this.mUpdatedSensorToBufferTransformMatrix = new Matrix(this.mOriginalSensorToBufferTransformMatrix);
        }
    }

    /* access modifiers changed from: package-private */
    public void setProcessedImageReaderProxy(SafeCloseImageReaderProxy processedImageReaderProxy) {
        synchronized (this.mAnalyzerLock) {
            this.mProcessedImageReaderProxy = processedImageReaderProxy;
        }
    }

    /* access modifiers changed from: package-private */
    public void setAnalyzer(Executor userExecutor, ImageAnalysis.Analyzer subscribedAnalyzer) {
        if (subscribedAnalyzer == null) {
            clearCache();
        }
        synchronized (this.mAnalyzerLock) {
            this.mSubscribedAnalyzer = subscribedAnalyzer;
            this.mUserExecutor = userExecutor;
        }
    }

    /* access modifiers changed from: package-private */
    public void attach() {
        this.mIsAttached = true;
    }

    /* access modifiers changed from: package-private */
    public void detach() {
        this.mIsAttached = false;
        clearCache();
    }

    private void createHelperBuffer(ImageProxy imageProxy) {
        if (this.mOutputImageFormat == 1) {
            if (this.mYRotatedBuffer == null) {
                this.mYRotatedBuffer = ByteBuffer.allocateDirect(imageProxy.getWidth() * imageProxy.getHeight());
            }
            this.mYRotatedBuffer.position(0);
            if (this.mURotatedBuffer == null) {
                this.mURotatedBuffer = ByteBuffer.allocateDirect((imageProxy.getWidth() * imageProxy.getHeight()) / 4);
            }
            this.mURotatedBuffer.position(0);
            if (this.mVRotatedBuffer == null) {
                this.mVRotatedBuffer = ByteBuffer.allocateDirect((imageProxy.getWidth() * imageProxy.getHeight()) / 4);
            }
            this.mVRotatedBuffer.position(0);
        } else if (this.mOutputImageFormat == 2 && this.mRGBConvertedBuffer == null) {
            this.mRGBConvertedBuffer = ByteBuffer.allocateDirect(imageProxy.getWidth() * imageProxy.getHeight() * 4);
        }
    }

    private void recreateImageReaderProxy(ImageProxy imageProxy, int relativeRotation) {
        SafeCloseImageReaderProxy safeCloseImageReaderProxy = this.mProcessedImageReaderProxy;
        if (safeCloseImageReaderProxy != null) {
            safeCloseImageReaderProxy.safeClose();
            this.mProcessedImageReaderProxy = createImageReaderProxy(imageProxy.getWidth(), imageProxy.getHeight(), relativeRotation, this.mProcessedImageReaderProxy.getImageFormat(), this.mProcessedImageReaderProxy.getMaxImages());
            if (Build.VERSION.SDK_INT >= 23 && this.mOutputImageFormat == 1) {
                ImageWriter imageWriter = this.mProcessedImageWriter;
                if (imageWriter != null) {
                    ImageWriterCompat.close(imageWriter);
                }
                this.mProcessedImageWriter = ImageWriterCompat.newInstance(this.mProcessedImageReaderProxy.getSurface(), this.mProcessedImageReaderProxy.getMaxImages());
            }
        }
    }

    private void recalculateTransformMatrixAndCropRect(int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight) {
        Matrix additionalTransformMatrix = getAdditionalTransformMatrixAppliedByProcessor(originalWidth, originalHeight, rotatedWidth, rotatedHeight, this.mRelativeRotation);
        this.mUpdatedViewPortCropRect = getUpdatedCropRect(this.mOriginalViewPortCropRect, additionalTransformMatrix);
        this.mUpdatedSensorToBufferTransformMatrix.setConcat(this.mOriginalSensorToBufferTransformMatrix, additionalTransformMatrix);
    }

    static Rect getUpdatedCropRect(Rect originalCropRect, Matrix additionalTransformMatrix) {
        RectF rectF = new RectF(originalCropRect);
        additionalTransformMatrix.mapRect(rectF);
        Rect rect = new Rect();
        rectF.round(rect);
        return rect;
    }

    static Matrix getAdditionalTransformMatrixAppliedByProcessor(int originalWidth, int originalHeight, int rotatedWidth, int rotatedHeight, int relativeRotation) {
        Matrix matrix = new Matrix();
        if (relativeRotation > 0) {
            matrix.setRectToRect(new RectF(0.0f, 0.0f, (float) originalWidth, (float) originalHeight), NORMALIZED_RECT, Matrix.ScaleToFit.FILL);
            matrix.postRotate((float) relativeRotation);
            matrix.postConcat(getNormalizedToBuffer(new RectF(0.0f, 0.0f, (float) rotatedWidth, (float) rotatedHeight)));
        }
        return matrix;
    }

    private static Matrix getNormalizedToBuffer(RectF buffer) {
        Matrix normalizedToBuffer = new Matrix();
        normalizedToBuffer.setRectToRect(NORMALIZED_RECT, buffer, Matrix.ScaleToFit.FILL);
        return normalizedToBuffer;
    }
}
