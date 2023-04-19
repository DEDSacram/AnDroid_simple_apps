package androidx.camera.core;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Size;
import androidx.camera.core.impl.CameraControlInternal;
import androidx.camera.core.impl.CameraInfoInternal;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.core.impl.MutableOptionsBundle;
import androidx.camera.core.impl.SessionConfig;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.core.internal.TargetConfig;
import androidx.camera.core.internal.utils.UseCaseConfigUtil;
import androidx.core.util.Preconditions;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class UseCase {
    private Size mAttachedResolution;
    private SessionConfig mAttachedSessionConfig = SessionConfig.defaultEmptySessionConfig();
    private CameraInternal mCamera;
    private UseCaseConfig<?> mCameraConfig;
    private final Object mCameraLock = new Object();
    private UseCaseConfig<?> mCurrentConfig;
    private UseCaseConfig<?> mExtendedConfig;
    private State mState = State.INACTIVE;
    private final Set<StateChangeCallback> mStateChangeCallbacks = new HashSet();
    private UseCaseConfig<?> mUseCaseConfig;
    private Rect mViewPortCropRect;

    public interface EventCallback {
        void onAttach(CameraInfo cameraInfo);

        void onDetach();
    }

    enum State {
        ACTIVE,
        INACTIVE
    }

    public interface StateChangeCallback {
        void onUseCaseActive(UseCase useCase);

        void onUseCaseInactive(UseCase useCase);

        void onUseCaseReset(UseCase useCase);

        void onUseCaseUpdated(UseCase useCase);
    }

    public abstract UseCaseConfig<?> getDefaultConfig(boolean z, UseCaseConfigFactory useCaseConfigFactory);

    public abstract UseCaseConfig.Builder<?, ?, ?> getUseCaseConfigBuilder(Config config);

    /* access modifiers changed from: protected */
    public abstract Size onSuggestedResolutionUpdated(Size size);

    protected UseCase(UseCaseConfig<?> currentConfig) {
        this.mUseCaseConfig = currentConfig;
        this.mCurrentConfig = currentConfig;
    }

    public UseCaseConfig<?> mergeConfigs(CameraInfoInternal cameraInfo, UseCaseConfig<?> extendedConfig, UseCaseConfig<?> cameraDefaultConfig) {
        MutableOptionsBundle mergedConfig;
        if (cameraDefaultConfig != null) {
            mergedConfig = MutableOptionsBundle.from(cameraDefaultConfig);
            mergedConfig.removeOption(TargetConfig.OPTION_TARGET_NAME);
        } else {
            mergedConfig = MutableOptionsBundle.create();
        }
        for (Config.Option<?> opt : this.mUseCaseConfig.listOptions()) {
            Config.Option<?> option = opt;
            mergedConfig.insertOption(option, this.mUseCaseConfig.getOptionPriority(opt), this.mUseCaseConfig.retrieveOption(option));
        }
        if (extendedConfig != null) {
            for (Config.Option<?> opt2 : extendedConfig.listOptions()) {
                Config.Option<?> option2 = opt2;
                if (!option2.getId().equals(TargetConfig.OPTION_TARGET_NAME.getId())) {
                    mergedConfig.insertOption(option2, extendedConfig.getOptionPriority(opt2), extendedConfig.retrieveOption(option2));
                }
            }
        }
        if (mergedConfig.containsOption(ImageOutputConfig.OPTION_TARGET_RESOLUTION) && mergedConfig.containsOption(ImageOutputConfig.OPTION_TARGET_ASPECT_RATIO)) {
            mergedConfig.removeOption(ImageOutputConfig.OPTION_TARGET_ASPECT_RATIO);
        }
        return onMergeConfig(cameraInfo, getUseCaseConfigBuilder(mergedConfig));
    }

    /* JADX WARNING: type inference failed for: r3v0, types: [androidx.camera.core.impl.UseCaseConfig$Builder, androidx.camera.core.impl.UseCaseConfig$Builder<?, ?, ?>] */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.camera.core.impl.UseCaseConfig<?> onMergeConfig(androidx.camera.core.impl.CameraInfoInternal r2, androidx.camera.core.impl.UseCaseConfig.Builder<?, ?, ?> r3) {
        /*
            r1 = this;
            androidx.camera.core.impl.UseCaseConfig r0 = r3.getUseCaseConfig()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.UseCase.onMergeConfig(androidx.camera.core.impl.CameraInfoInternal, androidx.camera.core.impl.UseCaseConfig$Builder):androidx.camera.core.impl.UseCaseConfig");
    }

    /* access modifiers changed from: protected */
    public boolean setTargetRotationInternal(int targetRotation) {
        int oldRotation = ((ImageOutputConfig) getCurrentConfig()).getTargetRotation(-1);
        if (oldRotation != -1 && oldRotation == targetRotation) {
            return false;
        }
        UseCaseConfig.Builder useCaseConfigBuilder = getUseCaseConfigBuilder(this.mUseCaseConfig);
        UseCaseConfigUtil.updateTargetRotationAndRelatedConfigs(useCaseConfigBuilder, targetRotation);
        this.mUseCaseConfig = useCaseConfigBuilder.getUseCaseConfig();
        CameraInternal camera = getCamera();
        if (camera == null) {
            this.mCurrentConfig = this.mUseCaseConfig;
            return true;
        }
        this.mCurrentConfig = mergeConfigs(camera.getCameraInfoInternal(), this.mExtendedConfig, this.mCameraConfig);
        return true;
    }

    /* access modifiers changed from: protected */
    public int getTargetRotationInternal() {
        return ((ImageOutputConfig) this.mCurrentConfig).getTargetRotation(0);
    }

    /* access modifiers changed from: protected */
    public int getAppTargetRotation() {
        return ((ImageOutputConfig) this.mCurrentConfig).getAppTargetRotation(-1);
    }

    /* access modifiers changed from: protected */
    public int getRelativeRotation(CameraInternal cameraInternal) {
        return cameraInternal.getCameraInfoInternal().getSensorRotationDegrees(getTargetRotationInternal());
    }

    /* access modifiers changed from: protected */
    public void updateSessionConfig(SessionConfig sessionConfig) {
        this.mAttachedSessionConfig = sessionConfig;
        for (DeferrableSurface surface : sessionConfig.getSurfaces()) {
            if (surface.getContainerClass() == null) {
                surface.setContainerClass(getClass());
            }
        }
    }

    private void addStateChangeCallback(StateChangeCallback callback) {
        this.mStateChangeCallbacks.add(callback);
    }

    private void removeStateChangeCallback(StateChangeCallback callback) {
        this.mStateChangeCallbacks.remove(callback);
    }

    public SessionConfig getSessionConfig() {
        return this.mAttachedSessionConfig;
    }

    /* access modifiers changed from: protected */
    public final void notifyActive() {
        this.mState = State.ACTIVE;
        notifyState();
    }

    /* access modifiers changed from: protected */
    public final void notifyInactive() {
        this.mState = State.INACTIVE;
        notifyState();
    }

    /* access modifiers changed from: protected */
    public final void notifyUpdated() {
        for (StateChangeCallback stateChangeCallback : this.mStateChangeCallbacks) {
            stateChangeCallback.onUseCaseUpdated(this);
        }
    }

    /* access modifiers changed from: protected */
    public final void notifyReset() {
        for (StateChangeCallback stateChangeCallback : this.mStateChangeCallbacks) {
            stateChangeCallback.onUseCaseReset(this);
        }
    }

    /* renamed from: androidx.camera.core.UseCase$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$camera$core$UseCase$State;

        static {
            int[] iArr = new int[State.values().length];
            $SwitchMap$androidx$camera$core$UseCase$State = iArr;
            try {
                iArr[State.INACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$camera$core$UseCase$State[State.ACTIVE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public final void notifyState() {
        switch (AnonymousClass1.$SwitchMap$androidx$camera$core$UseCase$State[this.mState.ordinal()]) {
            case 1:
                for (StateChangeCallback stateChangeCallback : this.mStateChangeCallbacks) {
                    stateChangeCallback.onUseCaseInactive(this);
                }
                return;
            case 2:
                for (StateChangeCallback stateChangeCallback2 : this.mStateChangeCallbacks) {
                    stateChangeCallback2.onUseCaseActive(this);
                }
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: protected */
    public String getCameraId() {
        return ((CameraInternal) Preconditions.checkNotNull(getCamera(), "No camera attached to use case: " + this)).getCameraInfoInternal().getCameraId();
    }

    /* access modifiers changed from: protected */
    public boolean isCurrentCamera(String cameraId) {
        if (getCamera() == null) {
            return false;
        }
        return Objects.equals(cameraId, getCameraId());
    }

    public String getName() {
        return this.mCurrentConfig.getTargetName("<UnknownUseCase-" + hashCode() + ">");
    }

    public UseCaseConfig<?> getCurrentConfig() {
        return this.mCurrentConfig;
    }

    public CameraInternal getCamera() {
        CameraInternal cameraInternal;
        synchronized (this.mCameraLock) {
            cameraInternal = this.mCamera;
        }
        return cameraInternal;
    }

    public Size getAttachedSurfaceResolution() {
        return this.mAttachedResolution;
    }

    public void updateSuggestedResolution(Size suggestedResolution) {
        this.mAttachedResolution = onSuggestedResolutionUpdated(suggestedResolution);
    }

    /* access modifiers changed from: protected */
    public void onCameraControlReady() {
    }

    public void onAttach(CameraInternal camera, UseCaseConfig<?> extendedConfig, UseCaseConfig<?> cameraConfig) {
        synchronized (this.mCameraLock) {
            this.mCamera = camera;
            addStateChangeCallback(camera);
        }
        this.mExtendedConfig = extendedConfig;
        this.mCameraConfig = cameraConfig;
        UseCaseConfig<?> mergeConfigs = mergeConfigs(camera.getCameraInfoInternal(), this.mExtendedConfig, this.mCameraConfig);
        this.mCurrentConfig = mergeConfigs;
        EventCallback eventCallback = mergeConfigs.getUseCaseEventCallback((EventCallback) null);
        if (eventCallback != null) {
            eventCallback.onAttach(camera.getCameraInfoInternal());
        }
        onAttached();
    }

    public void onAttached() {
    }

    public void onDetach(CameraInternal camera) {
        onDetached();
        EventCallback eventCallback = this.mCurrentConfig.getUseCaseEventCallback((EventCallback) null);
        if (eventCallback != null) {
            eventCallback.onDetach();
        }
        synchronized (this.mCameraLock) {
            Preconditions.checkArgument(camera == this.mCamera);
            removeStateChangeCallback(this.mCamera);
            this.mCamera = null;
        }
        this.mAttachedResolution = null;
        this.mViewPortCropRect = null;
        this.mCurrentConfig = this.mUseCaseConfig;
        this.mExtendedConfig = null;
        this.mCameraConfig = null;
    }

    public void onDetached() {
    }

    public void onStateAttached() {
        onCameraControlReady();
    }

    public void onStateDetached() {
    }

    /* access modifiers changed from: protected */
    public CameraControlInternal getCameraControl() {
        synchronized (this.mCameraLock) {
            CameraInternal cameraInternal = this.mCamera;
            if (cameraInternal == null) {
                CameraControlInternal cameraControlInternal = CameraControlInternal.DEFAULT_EMPTY_INSTANCE;
                return cameraControlInternal;
            }
            CameraControlInternal cameraControlInternal2 = cameraInternal.getCameraControlInternal();
            return cameraControlInternal2;
        }
    }

    public void setViewPortCropRect(Rect viewPortCropRect) {
        this.mViewPortCropRect = viewPortCropRect;
    }

    public Rect getViewPortCropRect() {
        return this.mViewPortCropRect;
    }

    public void setSensorToBufferTransformMatrix(Matrix sensorToBufferTransformMatrix) {
    }

    public int getImageFormat() {
        return this.mCurrentConfig.getInputFormat();
    }

    public ResolutionInfo getResolutionInfo() {
        return getResolutionInfoInternal();
    }

    /* access modifiers changed from: protected */
    public ResolutionInfo getResolutionInfoInternal() {
        CameraInternal camera = getCamera();
        Size resolution = getAttachedSurfaceResolution();
        if (camera == null || resolution == null) {
            return null;
        }
        Rect cropRect = getViewPortCropRect();
        if (cropRect == null) {
            cropRect = new Rect(0, 0, resolution.getWidth(), resolution.getHeight());
        }
        return ResolutionInfo.create(resolution, cropRect, getRelativeRotation(camera));
    }
}
