package androidx.camera.view;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Size;
import android.view.Display;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Logger;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.VideoCapture;
import androidx.camera.core.ViewPort;
import androidx.camera.core.ZoomState;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.RotationProvider;
import androidx.camera.view.transform.OutputTransform;
import androidx.camera.view.video.OnVideoSavedCallback;
import androidx.camera.view.video.OutputFileOptions;
import androidx.camera.view.video.OutputFileResults;
import androidx.core.util.Preconditions;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CameraController {
    private static final float AE_SIZE = 0.25f;
    private static final float AF_SIZE = 0.16666667f;
    private static final String CAMERA_NOT_ATTACHED = "Use cases not attached to camera.";
    private static final String CAMERA_NOT_INITIALIZED = "Camera not initialized.";
    public static final int COORDINATE_SYSTEM_VIEW_REFERENCED = 1;
    public static final int IMAGE_ANALYSIS = 2;
    public static final int IMAGE_CAPTURE = 1;
    private static final String IMAGE_CAPTURE_DISABLED = "ImageCapture disabled.";
    private static final String PREVIEW_VIEW_NOT_ATTACHED = "PreviewView not attached to CameraController.";
    private static final String TAG = "CameraController";
    public static final int TAP_TO_FOCUS_FAILED = 4;
    public static final int TAP_TO_FOCUS_FOCUSED = 2;
    public static final int TAP_TO_FOCUS_NOT_FOCUSED = 3;
    public static final int TAP_TO_FOCUS_NOT_STARTED = 0;
    public static final int TAP_TO_FOCUS_STARTED = 1;
    public static final int VIDEO_CAPTURE = 4;
    private static final String VIDEO_CAPTURE_DISABLED = "VideoCapture disabled.";
    private ImageAnalysis.Analyzer mAnalysisAnalyzer;
    private Executor mAnalysisBackgroundExecutor;
    private Executor mAnalysisExecutor;
    private final Context mAppContext;
    Camera mCamera;
    ProcessCameraProvider mCameraProvider;
    CameraSelector mCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
    final RotationProvider.Listener mDeviceRotationListener;
    private int mEnabledUseCases = 3;
    ImageAnalysis mImageAnalysis;
    OutputSize mImageAnalysisTargetSize;
    ImageCapture mImageCapture;
    Executor mImageCaptureIoExecutor;
    OutputSize mImageCaptureTargetSize;
    private final ListenableFuture<Void> mInitializationFuture;
    private boolean mPinchToZoomEnabled = true;
    Preview mPreview;
    Display mPreviewDisplay;
    OutputSize mPreviewTargetSize;
    private final RotationProvider mRotationProvider;
    Preview.SurfaceProvider mSurfaceProvider;
    private boolean mTapToFocusEnabled = true;
    final MutableLiveData<Integer> mTapToFocusState = new MutableLiveData<>(0);
    private final ForwardingLiveData<Integer> mTorchState = new ForwardingLiveData<>();
    VideoCapture mVideoCapture;
    OutputSize mVideoCaptureOutputSize;
    final AtomicBoolean mVideoIsRecording = new AtomicBoolean(false);
    ViewPort mViewPort;
    private final ForwardingLiveData<ZoomState> mZoomState = new ForwardingLiveData<>();

    @Retention(RetentionPolicy.SOURCE)
    public @interface TapToFocusStates {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface UseCases {
    }

    /* access modifiers changed from: package-private */
    public abstract Camera startCamera();

    CameraController(Context context) {
        Context applicationContext = getApplicationContext(context);
        this.mAppContext = applicationContext;
        this.mPreview = new Preview.Builder().build();
        this.mImageCapture = new ImageCapture.Builder().build();
        this.mImageAnalysis = new ImageAnalysis.Builder().build();
        this.mVideoCapture = new VideoCapture.Builder().build();
        this.mInitializationFuture = Futures.transform(ProcessCameraProvider.getInstance(applicationContext), new CameraController$$ExternalSyntheticLambda0(this), CameraXExecutors.mainThreadExecutor());
        this.mRotationProvider = new RotationProvider(applicationContext);
        this.mDeviceRotationListener = new CameraController$$ExternalSyntheticLambda1(this);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$0$androidx-camera-view-CameraController  reason: not valid java name */
    public /* synthetic */ Void m195lambda$new$0$androidxcameraviewCameraController(ProcessCameraProvider provider) {
        this.mCameraProvider = provider;
        startCameraAndTrackStates();
        return null;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$new$1$androidx-camera-view-CameraController  reason: not valid java name */
    public /* synthetic */ void m196lambda$new$1$androidxcameraviewCameraController(int rotation) {
        this.mImageAnalysis.setTargetRotation(rotation);
        this.mImageCapture.setTargetRotation(rotation);
        this.mVideoCapture.setTargetRotation(rotation);
    }

    private static Context getApplicationContext(Context context) {
        String attributeTag;
        Context applicationContext = context.getApplicationContext();
        if (Build.VERSION.SDK_INT < 30 || (attributeTag = Api30Impl.getAttributionTag(context)) == null) {
            return applicationContext;
        }
        return Api30Impl.createAttributionContext(applicationContext, attributeTag);
    }

    public ListenableFuture<Void> getInitializationFuture() {
        return this.mInitializationFuture;
    }

    private boolean isCameraInitialized() {
        return this.mCameraProvider != null;
    }

    private boolean isPreviewViewAttached() {
        return (this.mSurfaceProvider == null || this.mViewPort == null || this.mPreviewDisplay == null) ? false : true;
    }

    private boolean isCameraAttached() {
        return this.mCamera != null;
    }

    public void setEnabledUseCases(int enabledUseCases) {
        Threads.checkMainThread();
        if (enabledUseCases != this.mEnabledUseCases) {
            int oldEnabledUseCases = this.mEnabledUseCases;
            this.mEnabledUseCases = enabledUseCases;
            if (!isVideoCaptureEnabled()) {
                stopRecording();
            }
            startCameraAndTrackStates(new CameraController$$ExternalSyntheticLambda2(this, oldEnabledUseCases));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setEnabledUseCases$2$androidx-camera-view-CameraController  reason: not valid java name */
    public /* synthetic */ void m198lambda$setEnabledUseCases$2$androidxcameraviewCameraController(int oldEnabledUseCases) {
        this.mEnabledUseCases = oldEnabledUseCases;
    }

    private boolean isUseCaseEnabled(int useCaseMask) {
        return (this.mEnabledUseCases & useCaseMask) != 0;
    }

    private void setTargetOutputSize(ImageOutputConfig.Builder<?> builder, OutputSize outputSize) {
        if (outputSize != null) {
            if (outputSize.getResolution() != null) {
                builder.setTargetResolution(outputSize.getResolution());
            } else if (outputSize.getAspectRatio() != -1) {
                builder.setTargetAspectRatio(outputSize.getAspectRatio());
            } else {
                Logger.e(TAG, "Invalid target surface size. " + outputSize);
            }
        }
    }

    private boolean isOutputSizeEqual(OutputSize currentSize, OutputSize newSize) {
        if (currentSize == newSize) {
            return true;
        }
        if (currentSize == null || !currentSize.equals(newSize)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void attachPreviewSurface(Preview.SurfaceProvider surfaceProvider, ViewPort viewPort, Display display) {
        Threads.checkMainThread();
        if (this.mSurfaceProvider != surfaceProvider) {
            this.mSurfaceProvider = surfaceProvider;
            this.mPreview.setSurfaceProvider(surfaceProvider);
        }
        this.mViewPort = viewPort;
        this.mPreviewDisplay = display;
        startListeningToRotationEvents();
        startCameraAndTrackStates();
    }

    /* access modifiers changed from: package-private */
    public void clearPreviewSurface() {
        Threads.checkMainThread();
        ProcessCameraProvider processCameraProvider = this.mCameraProvider;
        if (processCameraProvider != null) {
            processCameraProvider.unbind(this.mPreview, this.mImageCapture, this.mImageAnalysis, this.mVideoCapture);
        }
        this.mPreview.setSurfaceProvider((Preview.SurfaceProvider) null);
        this.mCamera = null;
        this.mSurfaceProvider = null;
        this.mViewPort = null;
        this.mPreviewDisplay = null;
        stopListeningToRotationEvents();
    }

    private void startListeningToRotationEvents() {
        this.mRotationProvider.addListener(CameraXExecutors.mainThreadExecutor(), this.mDeviceRotationListener);
    }

    private void stopListeningToRotationEvents() {
        this.mRotationProvider.removeListener(this.mDeviceRotationListener);
    }

    public void setPreviewTargetSize(OutputSize targetSize) {
        Threads.checkMainThread();
        if (!isOutputSizeEqual(this.mPreviewTargetSize, targetSize)) {
            this.mPreviewTargetSize = targetSize;
            unbindPreviewAndRecreate();
            startCameraAndTrackStates();
        }
    }

    public OutputSize getPreviewTargetSize() {
        Threads.checkMainThread();
        return this.mPreviewTargetSize;
    }

    private void unbindPreviewAndRecreate() {
        if (isCameraInitialized()) {
            this.mCameraProvider.unbind(this.mPreview);
        }
        Preview.Builder builder = new Preview.Builder();
        setTargetOutputSize(builder, this.mPreviewTargetSize);
        this.mPreview = builder.build();
    }

    public boolean isImageCaptureEnabled() {
        Threads.checkMainThread();
        return isUseCaseEnabled(1);
    }

    public int getImageCaptureFlashMode() {
        Threads.checkMainThread();
        return this.mImageCapture.getFlashMode();
    }

    public void setImageCaptureFlashMode(int flashMode) {
        Threads.checkMainThread();
        this.mImageCapture.setFlashMode(flashMode);
    }

    public void takePicture(ImageCapture.OutputFileOptions outputFileOptions, Executor executor, ImageCapture.OnImageSavedCallback imageSavedCallback) {
        Threads.checkMainThread();
        Preconditions.checkState(isCameraInitialized(), CAMERA_NOT_INITIALIZED);
        Preconditions.checkState(isImageCaptureEnabled(), IMAGE_CAPTURE_DISABLED);
        updateMirroringFlagInOutputFileOptions(outputFileOptions);
        this.mImageCapture.m144lambda$takePicture$4$androidxcameracoreImageCapture(outputFileOptions, executor, imageSavedCallback);
    }

    /* access modifiers changed from: package-private */
    public void updateMirroringFlagInOutputFileOptions(ImageCapture.OutputFileOptions outputFileOptions) {
        if (this.mCameraSelector.getLensFacing() != null && !outputFileOptions.getMetadata().isReversedHorizontalSet()) {
            outputFileOptions.getMetadata().setReversedHorizontal(this.mCameraSelector.getLensFacing().intValue() == 0);
        }
    }

    public void takePicture(Executor executor, ImageCapture.OnImageCapturedCallback callback) {
        Threads.checkMainThread();
        Preconditions.checkState(isCameraInitialized(), CAMERA_NOT_INITIALIZED);
        Preconditions.checkState(isImageCaptureEnabled(), IMAGE_CAPTURE_DISABLED);
        this.mImageCapture.m143lambda$takePicture$3$androidxcameracoreImageCapture(executor, callback);
    }

    public void setImageCaptureMode(int captureMode) {
        Threads.checkMainThread();
        if (this.mImageCapture.getCaptureMode() != captureMode) {
            unbindImageCaptureAndRecreate(captureMode);
            startCameraAndTrackStates();
        }
    }

    public int getImageCaptureMode() {
        Threads.checkMainThread();
        return this.mImageCapture.getCaptureMode();
    }

    public void setImageCaptureTargetSize(OutputSize targetSize) {
        Threads.checkMainThread();
        if (!isOutputSizeEqual(this.mImageCaptureTargetSize, targetSize)) {
            this.mImageCaptureTargetSize = targetSize;
            unbindImageCaptureAndRecreate(getImageCaptureMode());
            startCameraAndTrackStates();
        }
    }

    public OutputSize getImageCaptureTargetSize() {
        Threads.checkMainThread();
        return this.mImageCaptureTargetSize;
    }

    public void setImageCaptureIoExecutor(Executor executor) {
        Threads.checkMainThread();
        if (this.mImageCaptureIoExecutor != executor) {
            this.mImageCaptureIoExecutor = executor;
            unbindImageCaptureAndRecreate(this.mImageCapture.getCaptureMode());
            startCameraAndTrackStates();
        }
    }

    public Executor getImageCaptureIoExecutor() {
        Threads.checkMainThread();
        return this.mImageCaptureIoExecutor;
    }

    private void unbindImageCaptureAndRecreate(int imageCaptureMode) {
        if (isCameraInitialized()) {
            this.mCameraProvider.unbind(this.mImageCapture);
        }
        ImageCapture.Builder builder = new ImageCapture.Builder().setCaptureMode(imageCaptureMode);
        setTargetOutputSize(builder, this.mImageCaptureTargetSize);
        Executor executor = this.mImageCaptureIoExecutor;
        if (executor != null) {
            builder.setIoExecutor(executor);
        }
        this.mImageCapture = builder.build();
    }

    public boolean isImageAnalysisEnabled() {
        Threads.checkMainThread();
        return isUseCaseEnabled(2);
    }

    public void setImageAnalysisAnalyzer(Executor executor, ImageAnalysis.Analyzer analyzer) {
        Threads.checkMainThread();
        if (this.mAnalysisAnalyzer != analyzer || this.mAnalysisExecutor != executor) {
            ImageAnalysis.Analyzer oldAnalyzer = this.mAnalysisAnalyzer;
            this.mAnalysisExecutor = executor;
            this.mAnalysisAnalyzer = analyzer;
            this.mImageAnalysis.setAnalyzer(executor, analyzer);
            restartCameraIfAnalyzerResolutionChanged(oldAnalyzer, analyzer);
        }
    }

    public void clearImageAnalysisAnalyzer() {
        Threads.checkMainThread();
        ImageAnalysis.Analyzer oldAnalyzer = this.mAnalysisAnalyzer;
        this.mAnalysisExecutor = null;
        this.mAnalysisAnalyzer = null;
        this.mImageAnalysis.clearAnalyzer();
        restartCameraIfAnalyzerResolutionChanged(oldAnalyzer, (ImageAnalysis.Analyzer) null);
    }

    private void restartCameraIfAnalyzerResolutionChanged(ImageAnalysis.Analyzer oldAnalyzer, ImageAnalysis.Analyzer newAnalyzer) {
        Size oldResolution;
        Size newResolution = null;
        if (oldAnalyzer == null) {
            oldResolution = null;
        } else {
            oldResolution = oldAnalyzer.getTargetResolutionOverride();
        }
        if (newAnalyzer != null) {
            newResolution = newAnalyzer.getTargetResolutionOverride();
        }
        if (!Objects.equals(oldResolution, newResolution)) {
            unbindImageAnalysisAndRecreate(this.mImageAnalysis.getBackpressureStrategy(), this.mImageAnalysis.getImageQueueDepth());
            startCameraAndTrackStates();
        }
    }

    public int getImageAnalysisBackpressureStrategy() {
        Threads.checkMainThread();
        return this.mImageAnalysis.getBackpressureStrategy();
    }

    public void setImageAnalysisBackpressureStrategy(int strategy) {
        Threads.checkMainThread();
        if (this.mImageAnalysis.getBackpressureStrategy() != strategy) {
            unbindImageAnalysisAndRecreate(strategy, this.mImageAnalysis.getImageQueueDepth());
            startCameraAndTrackStates();
        }
    }

    public void setImageAnalysisImageQueueDepth(int depth) {
        Threads.checkMainThread();
        if (this.mImageAnalysis.getImageQueueDepth() != depth) {
            unbindImageAnalysisAndRecreate(this.mImageAnalysis.getBackpressureStrategy(), depth);
            startCameraAndTrackStates();
        }
    }

    public int getImageAnalysisImageQueueDepth() {
        Threads.checkMainThread();
        return this.mImageAnalysis.getImageQueueDepth();
    }

    public void setImageAnalysisTargetSize(OutputSize targetSize) {
        Threads.checkMainThread();
        if (!isOutputSizeEqual(this.mImageAnalysisTargetSize, targetSize)) {
            this.mImageAnalysisTargetSize = targetSize;
            unbindImageAnalysisAndRecreate(this.mImageAnalysis.getBackpressureStrategy(), this.mImageAnalysis.getImageQueueDepth());
            startCameraAndTrackStates();
        }
    }

    public OutputSize getImageAnalysisTargetSize() {
        Threads.checkMainThread();
        return this.mImageAnalysisTargetSize;
    }

    public void setImageAnalysisBackgroundExecutor(Executor executor) {
        Threads.checkMainThread();
        if (this.mAnalysisBackgroundExecutor != executor) {
            this.mAnalysisBackgroundExecutor = executor;
            unbindImageAnalysisAndRecreate(this.mImageAnalysis.getBackpressureStrategy(), this.mImageAnalysis.getImageQueueDepth());
            startCameraAndTrackStates();
        }
    }

    public Executor getImageAnalysisBackgroundExecutor() {
        Threads.checkMainThread();
        return this.mAnalysisBackgroundExecutor;
    }

    private void unbindImageAnalysisAndRecreate(int strategy, int imageQueueDepth) {
        ImageAnalysis.Analyzer analyzer;
        Threads.checkMainThread();
        if (isCameraInitialized()) {
            this.mCameraProvider.unbind(this.mImageAnalysis);
        }
        ImageAnalysis.Builder builder = new ImageAnalysis.Builder().setBackpressureStrategy(strategy).setImageQueueDepth(imageQueueDepth);
        setTargetOutputSize(builder, this.mImageAnalysisTargetSize);
        Executor executor = this.mAnalysisBackgroundExecutor;
        if (executor != null) {
            builder.setBackgroundExecutor(executor);
        }
        ImageAnalysis build = builder.build();
        this.mImageAnalysis = build;
        Executor executor2 = this.mAnalysisExecutor;
        if (executor2 != null && (analyzer = this.mAnalysisAnalyzer) != null) {
            build.setAnalyzer(executor2, analyzer);
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePreviewViewTransform(OutputTransform outputTransform) {
        Threads.checkMainThread();
        ImageAnalysis.Analyzer analyzer = this.mAnalysisAnalyzer;
        if (analyzer != null) {
            if (outputTransform == null) {
                analyzer.updateTransform((Matrix) null);
            } else if (analyzer.getTargetCoordinateSystem() == 1) {
                this.mAnalysisAnalyzer.updateTransform(outputTransform.getMatrix());
            }
        }
    }

    public boolean isVideoCaptureEnabled() {
        Threads.checkMainThread();
        return isUseCaseEnabled(4);
    }

    public void startRecording(OutputFileOptions outputFileOptions, Executor executor, final OnVideoSavedCallback callback) {
        Threads.checkMainThread();
        Preconditions.checkState(isCameraInitialized(), CAMERA_NOT_INITIALIZED);
        Preconditions.checkState(isVideoCaptureEnabled(), VIDEO_CAPTURE_DISABLED);
        this.mVideoCapture.m162lambda$startRecording$0$androidxcameracoreVideoCapture(outputFileOptions.toVideoCaptureOutputFileOptions(), executor, new VideoCapture.OnVideoSavedCallback() {
            public void onVideoSaved(VideoCapture.OutputFileResults outputFileResults) {
                CameraController.this.mVideoIsRecording.set(false);
                callback.onVideoSaved(OutputFileResults.create(outputFileResults.getSavedUri()));
            }

            public void onError(int videoCaptureError, String message, Throwable cause) {
                CameraController.this.mVideoIsRecording.set(false);
                callback.onError(videoCaptureError, message, cause);
            }
        });
        this.mVideoIsRecording.set(true);
    }

    public void stopRecording() {
        Threads.checkMainThread();
        if (this.mVideoIsRecording.get()) {
            this.mVideoCapture.m166lambda$stopRecording$5$androidxcameracoreVideoCapture();
        }
    }

    public boolean isRecording() {
        Threads.checkMainThread();
        return this.mVideoIsRecording.get();
    }

    public void setVideoCaptureTargetSize(OutputSize targetSize) {
        Threads.checkMainThread();
        if (!isOutputSizeEqual(this.mVideoCaptureOutputSize, targetSize)) {
            this.mVideoCaptureOutputSize = targetSize;
            unbindVideoAndRecreate();
            startCameraAndTrackStates();
        }
    }

    public OutputSize getVideoCaptureTargetSize() {
        Threads.checkMainThread();
        return this.mVideoCaptureOutputSize;
    }

    private void unbindVideoAndRecreate() {
        if (isCameraInitialized()) {
            this.mCameraProvider.unbind(this.mVideoCapture);
        }
        VideoCapture.Builder builder = new VideoCapture.Builder();
        setTargetOutputSize(builder, this.mVideoCaptureOutputSize);
        this.mVideoCapture = builder.build();
    }

    public void setCameraSelector(CameraSelector cameraSelector) {
        Threads.checkMainThread();
        if (this.mCameraSelector != cameraSelector) {
            CameraSelector oldCameraSelector = this.mCameraSelector;
            this.mCameraSelector = cameraSelector;
            ProcessCameraProvider processCameraProvider = this.mCameraProvider;
            if (processCameraProvider != null) {
                processCameraProvider.unbind(this.mPreview, this.mImageCapture, this.mImageAnalysis, this.mVideoCapture);
                startCameraAndTrackStates(new CameraController$$ExternalSyntheticLambda3(this, oldCameraSelector));
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$setCameraSelector$3$androidx-camera-view-CameraController  reason: not valid java name */
    public /* synthetic */ void m197lambda$setCameraSelector$3$androidxcameraviewCameraController(CameraSelector oldCameraSelector) {
        this.mCameraSelector = oldCameraSelector;
    }

    public boolean hasCamera(CameraSelector cameraSelector) {
        Threads.checkMainThread();
        Preconditions.checkNotNull(cameraSelector);
        ProcessCameraProvider processCameraProvider = this.mCameraProvider;
        if (processCameraProvider != null) {
            try {
                return processCameraProvider.hasCamera(cameraSelector);
            } catch (CameraInfoUnavailableException e) {
                Logger.w(TAG, "Failed to check camera availability", e);
                return false;
            }
        } else {
            throw new IllegalStateException("Camera not initialized. Please wait for the initialization future to finish. See #getInitializationFuture().");
        }
    }

    public CameraSelector getCameraSelector() {
        Threads.checkMainThread();
        return this.mCameraSelector;
    }

    public boolean isPinchToZoomEnabled() {
        Threads.checkMainThread();
        return this.mPinchToZoomEnabled;
    }

    public void setPinchToZoomEnabled(boolean enabled) {
        Threads.checkMainThread();
        this.mPinchToZoomEnabled = enabled;
    }

    /* access modifiers changed from: package-private */
    public void onPinchToZoom(float pinchToZoomScale) {
        if (!isCameraAttached()) {
            Logger.w(TAG, CAMERA_NOT_ATTACHED);
        } else if (!this.mPinchToZoomEnabled) {
            Logger.d(TAG, "Pinch to zoom disabled.");
        } else {
            Logger.d(TAG, "Pinch to zoom with scale: " + pinchToZoomScale);
            ZoomState zoomState = getZoomState().getValue();
            if (zoomState != null) {
                setZoomRatio(Math.min(Math.max(zoomState.getZoomRatio() * speedUpZoomBy2X(pinchToZoomScale), zoomState.getMinZoomRatio()), zoomState.getMaxZoomRatio()));
            }
        }
    }

    private float speedUpZoomBy2X(float scaleFactor) {
        if (scaleFactor > 1.0f) {
            return ((scaleFactor - 1.0f) * 2.0f) + 1.0f;
        }
        return 1.0f - ((1.0f - scaleFactor) * 2.0f);
    }

    /* access modifiers changed from: package-private */
    public void onTapToFocus(MeteringPointFactory meteringPointFactory, float x, float y) {
        if (!isCameraAttached()) {
            Logger.w(TAG, CAMERA_NOT_ATTACHED);
        } else if (!this.mTapToFocusEnabled) {
            Logger.d(TAG, "Tap to focus disabled. ");
        } else {
            Logger.d(TAG, "Tap to focus started: " + x + ", " + y);
            this.mTapToFocusState.postValue(1);
            MeteringPoint afPoint = meteringPointFactory.createPoint(x, y, AF_SIZE);
            Futures.addCallback(this.mCamera.getCameraControl().startFocusAndMetering(new FocusMeteringAction.Builder(afPoint, 1).addPoint(meteringPointFactory.createPoint(x, y, AE_SIZE), 2).build()), new FutureCallback<FocusMeteringResult>() {
                public void onSuccess(FocusMeteringResult result) {
                    if (result != null) {
                        Logger.d(CameraController.TAG, "Tap to focus onSuccess: " + result.isFocusSuccessful());
                        CameraController.this.mTapToFocusState.postValue(Integer.valueOf(result.isFocusSuccessful() ? 2 : 3));
                    }
                }

                public void onFailure(Throwable t) {
                    if (t instanceof CameraControl.OperationCanceledException) {
                        Logger.d(CameraController.TAG, "Tap-to-focus is canceled by new action.");
                        return;
                    }
                    Logger.d(CameraController.TAG, "Tap to focus failed.", t);
                    CameraController.this.mTapToFocusState.postValue(4);
                }
            }, CameraXExecutors.directExecutor());
        }
    }

    public boolean isTapToFocusEnabled() {
        Threads.checkMainThread();
        return this.mTapToFocusEnabled;
    }

    public void setTapToFocusEnabled(boolean enabled) {
        Threads.checkMainThread();
        this.mTapToFocusEnabled = enabled;
    }

    public LiveData<Integer> getTapToFocusState() {
        Threads.checkMainThread();
        return this.mTapToFocusState;
    }

    public LiveData<ZoomState> getZoomState() {
        Threads.checkMainThread();
        return this.mZoomState;
    }

    public CameraInfo getCameraInfo() {
        Threads.checkMainThread();
        Camera camera = this.mCamera;
        if (camera == null) {
            return null;
        }
        return camera.getCameraInfo();
    }

    public CameraControl getCameraControl() {
        Threads.checkMainThread();
        Camera camera = this.mCamera;
        if (camera == null) {
            return null;
        }
        return camera.getCameraControl();
    }

    public ListenableFuture<Void> setZoomRatio(float zoomRatio) {
        Threads.checkMainThread();
        if (isCameraAttached()) {
            return this.mCamera.getCameraControl().setZoomRatio(zoomRatio);
        }
        Logger.w(TAG, CAMERA_NOT_ATTACHED);
        return Futures.immediateFuture(null);
    }

    public ListenableFuture<Void> setLinearZoom(float linearZoom) {
        Threads.checkMainThread();
        if (isCameraAttached()) {
            return this.mCamera.getCameraControl().setLinearZoom(linearZoom);
        }
        Logger.w(TAG, CAMERA_NOT_ATTACHED);
        return Futures.immediateFuture(null);
    }

    public LiveData<Integer> getTorchState() {
        Threads.checkMainThread();
        return this.mTorchState;
    }

    public ListenableFuture<Void> enableTorch(boolean torchEnabled) {
        Threads.checkMainThread();
        if (isCameraAttached()) {
            return this.mCamera.getCameraControl().enableTorch(torchEnabled);
        }
        Logger.w(TAG, CAMERA_NOT_ATTACHED);
        return Futures.immediateFuture(null);
    }

    /* access modifiers changed from: package-private */
    public void startCameraAndTrackStates() {
        startCameraAndTrackStates((Runnable) null);
    }

    /* access modifiers changed from: package-private */
    public void startCameraAndTrackStates(Runnable restoreStateRunnable) {
        try {
            this.mCamera = startCamera();
            if (!isCameraAttached()) {
                Logger.d(TAG, CAMERA_NOT_ATTACHED);
                return;
            }
            this.mZoomState.setSource(this.mCamera.getCameraInfo().getZoomState());
            this.mTorchState.setSource(this.mCamera.getCameraInfo().getTorchState());
        } catch (IllegalArgumentException exception) {
            if (restoreStateRunnable != null) {
                restoreStateRunnable.run();
            }
            throw new IllegalStateException("The selected camera does not support the enabled use cases. Please disable use case and/or select a different camera. e.g. #setVideoCaptureEnabled(false)", exception);
        }
    }

    /* access modifiers changed from: protected */
    public UseCaseGroup createUseCaseGroup() {
        if (!isCameraInitialized()) {
            Logger.d(TAG, CAMERA_NOT_INITIALIZED);
            return null;
        } else if (!isPreviewViewAttached()) {
            Logger.d(TAG, PREVIEW_VIEW_NOT_ATTACHED);
            return null;
        } else {
            UseCaseGroup.Builder builder = new UseCaseGroup.Builder().addUseCase(this.mPreview);
            if (isImageCaptureEnabled()) {
                builder.addUseCase(this.mImageCapture);
            } else {
                this.mCameraProvider.unbind(this.mImageCapture);
            }
            if (isImageAnalysisEnabled()) {
                builder.addUseCase(this.mImageAnalysis);
            } else {
                this.mCameraProvider.unbind(this.mImageAnalysis);
            }
            if (isVideoCaptureEnabled()) {
                builder.addUseCase(this.mVideoCapture);
            } else {
                this.mCameraProvider.unbind(this.mVideoCapture);
            }
            builder.setViewPort(this.mViewPort);
            return builder.build();
        }
    }

    private static class Api30Impl {
        private Api30Impl() {
        }

        static Context createAttributionContext(Context context, String attributeTag) {
            return context.createAttributionContext(attributeTag);
        }

        static String getAttributionTag(Context context) {
            return context.getAttributionTag();
        }
    }

    public static final class OutputSize {
        public static final int UNASSIGNED_ASPECT_RATIO = -1;
        private final int mAspectRatio;
        private final Size mResolution;

        @Retention(RetentionPolicy.SOURCE)
        public @interface OutputAspectRatio {
        }

        public OutputSize(int aspectRatio) {
            Preconditions.checkArgument(aspectRatio != -1);
            this.mAspectRatio = aspectRatio;
            this.mResolution = null;
        }

        public OutputSize(Size resolution) {
            Preconditions.checkNotNull(resolution);
            this.mAspectRatio = -1;
            this.mResolution = resolution;
        }

        public int getAspectRatio() {
            return this.mAspectRatio;
        }

        public Size getResolution() {
            return this.mResolution;
        }

        public String toString() {
            return "aspect ratio: " + this.mAspectRatio + " resolution: " + this.mResolution;
        }
    }
}
