package androidx.camera.camera2.internal;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Pair;
import androidx.camera.camera2.internal.compat.CameraAccessExceptionCompat;
import androidx.camera.camera2.internal.compat.CameraCharacteristicsCompat;
import androidx.camera.camera2.internal.compat.CameraManagerCompat;
import androidx.camera.camera2.internal.compat.quirk.CameraQuirks;
import androidx.camera.camera2.internal.compat.workaround.FlashAvailabilityChecker;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraState;
import androidx.camera.core.ExposureState;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.Logger;
import androidx.camera.core.ZoomState;
import androidx.camera.core.impl.CamcorderProfileProvider;
import androidx.camera.core.impl.CameraCaptureCallback;
import androidx.camera.core.impl.CameraInfoInternal;
import androidx.camera.core.impl.Quirks;
import androidx.camera.core.impl.utils.CameraOrientationUtil;
import androidx.core.util.Preconditions;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public final class Camera2CameraInfoImpl implements CameraInfoInternal {
    private static final String TAG = "Camera2CameraInfo";
    private final CamcorderProfileProvider mCamera2CamcorderProfileProvider;
    private Camera2CameraControlImpl mCamera2CameraControlImpl;
    private final Camera2CameraInfo mCamera2CameraInfo;
    private List<Pair<CameraCaptureCallback, Executor>> mCameraCaptureCallbacks = null;
    private final CameraCharacteristicsCompat mCameraCharacteristicsCompat;
    private final String mCameraId;
    private final CameraManagerCompat mCameraManager;
    private final Quirks mCameraQuirks;
    private final RedirectableLiveData<CameraState> mCameraStateLiveData;
    private final Object mLock = new Object();
    private RedirectableLiveData<Integer> mRedirectTorchStateLiveData = null;
    private RedirectableLiveData<ZoomState> mRedirectZoomStateLiveData = null;

    Camera2CameraInfoImpl(String cameraId, CameraManagerCompat cameraManager) throws CameraAccessExceptionCompat {
        String str = (String) Preconditions.checkNotNull(cameraId);
        this.mCameraId = str;
        this.mCameraManager = cameraManager;
        CameraCharacteristicsCompat cameraCharacteristicsCompat = cameraManager.getCameraCharacteristicsCompat(str);
        this.mCameraCharacteristicsCompat = cameraCharacteristicsCompat;
        this.mCamera2CameraInfo = new Camera2CameraInfo(this);
        this.mCameraQuirks = CameraQuirks.get(cameraId, cameraCharacteristicsCompat);
        this.mCamera2CamcorderProfileProvider = new Camera2CamcorderProfileProvider(cameraId, cameraCharacteristicsCompat);
        this.mCameraStateLiveData = new RedirectableLiveData<>(CameraState.create(CameraState.Type.CLOSED));
    }

    /* access modifiers changed from: package-private */
    public void linkWithCameraControl(Camera2CameraControlImpl camera2CameraControlImpl) {
        synchronized (this.mLock) {
            this.mCamera2CameraControlImpl = camera2CameraControlImpl;
            RedirectableLiveData<ZoomState> redirectableLiveData = this.mRedirectZoomStateLiveData;
            if (redirectableLiveData != null) {
                redirectableLiveData.redirectTo(camera2CameraControlImpl.getZoomControl().getZoomState());
            }
            RedirectableLiveData<Integer> redirectableLiveData2 = this.mRedirectTorchStateLiveData;
            if (redirectableLiveData2 != null) {
                redirectableLiveData2.redirectTo(this.mCamera2CameraControlImpl.getTorchControl().getTorchState());
            }
            List<Pair<CameraCaptureCallback, Executor>> list = this.mCameraCaptureCallbacks;
            if (list != null) {
                for (Pair<CameraCaptureCallback, Executor> pair : list) {
                    this.mCamera2CameraControlImpl.addSessionCameraCaptureCallback((Executor) pair.second, (CameraCaptureCallback) pair.first);
                }
                this.mCameraCaptureCallbacks = null;
            }
        }
        logDeviceInfo();
    }

    /* access modifiers changed from: package-private */
    public void setCameraStateSource(LiveData<CameraState> cameraStateSource) {
        this.mCameraStateLiveData.redirectTo(cameraStateSource);
    }

    public String getCameraId() {
        return this.mCameraId;
    }

    public CameraCharacteristicsCompat getCameraCharacteristicsCompat() {
        return this.mCameraCharacteristicsCompat;
    }

    public Integer getLensFacing() {
        Integer lensFacing = (Integer) this.mCameraCharacteristicsCompat.get(CameraCharacteristics.LENS_FACING);
        Preconditions.checkNotNull(lensFacing);
        switch (lensFacing.intValue()) {
            case 0:
                return 0;
            case 1:
                return 1;
            default:
                return null;
        }
    }

    public int getSensorRotationDegrees(int relativeRotation) {
        int sensorOrientation = getSensorOrientation();
        int relativeRotationDegrees = CameraOrientationUtil.surfaceRotationToDegrees(relativeRotation);
        Integer lensFacing = getLensFacing();
        boolean isOppositeFacingScreen = true;
        if (lensFacing == null || 1 != lensFacing.intValue()) {
            isOppositeFacingScreen = false;
        }
        return CameraOrientationUtil.getRelativeImageRotation(relativeRotationDegrees, sensorOrientation, isOppositeFacingScreen);
    }

    /* access modifiers changed from: package-private */
    public int getSensorOrientation() {
        Integer sensorOrientation = (Integer) this.mCameraCharacteristicsCompat.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Preconditions.checkNotNull(sensorOrientation);
        return sensorOrientation.intValue();
    }

    /* access modifiers changed from: package-private */
    public int getSupportedHardwareLevel() {
        Integer deviceLevel = (Integer) this.mCameraCharacteristicsCompat.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        Preconditions.checkNotNull(deviceLevel);
        return deviceLevel.intValue();
    }

    public int getSensorRotationDegrees() {
        return getSensorRotationDegrees(0);
    }

    private void logDeviceInfo() {
        logDeviceLevel();
    }

    private void logDeviceLevel() {
        String levelString;
        int deviceLevel = getSupportedHardwareLevel();
        switch (deviceLevel) {
            case 0:
                levelString = "INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED";
                break;
            case 1:
                levelString = "INFO_SUPPORTED_HARDWARE_LEVEL_FULL";
                break;
            case 2:
                levelString = "INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY";
                break;
            case 3:
                levelString = "INFO_SUPPORTED_HARDWARE_LEVEL_3";
                break;
            case 4:
                levelString = "INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL";
                break;
            default:
                levelString = "Unknown value: " + deviceLevel;
                break;
        }
        Logger.i(TAG, "Device Level: " + levelString);
    }

    public boolean hasFlashUnit() {
        return FlashAvailabilityChecker.isFlashAvailable(this.mCameraCharacteristicsCompat);
    }

    public LiveData<Integer> getTorchState() {
        synchronized (this.mLock) {
            Camera2CameraControlImpl camera2CameraControlImpl = this.mCamera2CameraControlImpl;
            if (camera2CameraControlImpl == null) {
                if (this.mRedirectTorchStateLiveData == null) {
                    this.mRedirectTorchStateLiveData = new RedirectableLiveData<>(0);
                }
                RedirectableLiveData<Integer> redirectableLiveData = this.mRedirectTorchStateLiveData;
                return redirectableLiveData;
            }
            RedirectableLiveData<Integer> redirectableLiveData2 = this.mRedirectTorchStateLiveData;
            if (redirectableLiveData2 != null) {
                return redirectableLiveData2;
            }
            LiveData<Integer> torchState = camera2CameraControlImpl.getTorchControl().getTorchState();
            return torchState;
        }
    }

    public LiveData<ZoomState> getZoomState() {
        synchronized (this.mLock) {
            Camera2CameraControlImpl camera2CameraControlImpl = this.mCamera2CameraControlImpl;
            if (camera2CameraControlImpl == null) {
                if (this.mRedirectZoomStateLiveData == null) {
                    this.mRedirectZoomStateLiveData = new RedirectableLiveData<>(ZoomControl.getDefaultZoomState(this.mCameraCharacteristicsCompat));
                }
                RedirectableLiveData<ZoomState> redirectableLiveData = this.mRedirectZoomStateLiveData;
                return redirectableLiveData;
            }
            RedirectableLiveData<ZoomState> redirectableLiveData2 = this.mRedirectZoomStateLiveData;
            if (redirectableLiveData2 != null) {
                return redirectableLiveData2;
            }
            LiveData<ZoomState> zoomState = camera2CameraControlImpl.getZoomControl().getZoomState();
            return zoomState;
        }
    }

    public ExposureState getExposureState() {
        synchronized (this.mLock) {
            Camera2CameraControlImpl camera2CameraControlImpl = this.mCamera2CameraControlImpl;
            if (camera2CameraControlImpl == null) {
                ExposureState defaultExposureState = ExposureControl.getDefaultExposureState(this.mCameraCharacteristicsCompat);
                return defaultExposureState;
            }
            ExposureState exposureState = camera2CameraControlImpl.getExposureControl().getExposureState();
            return exposureState;
        }
    }

    public LiveData<CameraState> getCameraState() {
        return this.mCameraStateLiveData;
    }

    public String getImplementationType() {
        return getSupportedHardwareLevel() == 2 ? CameraInfo.IMPLEMENTATION_TYPE_CAMERA2_LEGACY : CameraInfo.IMPLEMENTATION_TYPE_CAMERA2;
    }

    public boolean isFocusMeteringSupported(FocusMeteringAction action) {
        synchronized (this.mLock) {
            Camera2CameraControlImpl camera2CameraControlImpl = this.mCamera2CameraControlImpl;
            if (camera2CameraControlImpl == null) {
                return false;
            }
            boolean isFocusMeteringSupported = camera2CameraControlImpl.getFocusMeteringControl().isFocusMeteringSupported(action);
            return isFocusMeteringSupported;
        }
    }

    public boolean isZslSupported() {
        return Build.VERSION.SDK_INT >= 23 && isPrivateReprocessingSupported();
    }

    public boolean isPrivateReprocessingSupported() {
        return ZslUtil.isCapabilitySupported(this.mCameraCharacteristicsCompat, 4);
    }

    public CamcorderProfileProvider getCamcorderProfileProvider() {
        return this.mCamera2CamcorderProfileProvider;
    }

    public void addSessionCaptureCallback(Executor executor, CameraCaptureCallback callback) {
        synchronized (this.mLock) {
            Camera2CameraControlImpl camera2CameraControlImpl = this.mCamera2CameraControlImpl;
            if (camera2CameraControlImpl == null) {
                if (this.mCameraCaptureCallbacks == null) {
                    this.mCameraCaptureCallbacks = new ArrayList();
                }
                this.mCameraCaptureCallbacks.add(new Pair(callback, executor));
                return;
            }
            camera2CameraControlImpl.addSessionCameraCaptureCallback(executor, callback);
        }
    }

    public void removeSessionCaptureCallback(CameraCaptureCallback callback) {
        synchronized (this.mLock) {
            Camera2CameraControlImpl camera2CameraControlImpl = this.mCamera2CameraControlImpl;
            if (camera2CameraControlImpl == null) {
                List<Pair<CameraCaptureCallback, Executor>> list = this.mCameraCaptureCallbacks;
                if (list != null) {
                    Iterator<Pair<CameraCaptureCallback, Executor>> it = list.iterator();
                    while (it.hasNext()) {
                        if (it.next().first == callback) {
                            it.remove();
                        }
                    }
                    return;
                }
                return;
            }
            camera2CameraControlImpl.removeSessionCameraCaptureCallback(callback);
        }
    }

    public Quirks getCameraQuirks() {
        return this.mCameraQuirks;
    }

    public Camera2CameraInfo getCamera2CameraInfo() {
        return this.mCamera2CameraInfo;
    }

    public Map<String, CameraCharacteristics> getCameraCharacteristicsMap() {
        LinkedHashMap<String, CameraCharacteristics> map = new LinkedHashMap<>();
        map.put(this.mCameraId, this.mCameraCharacteristicsCompat.toCameraCharacteristics());
        for (String physicalCameraId : this.mCameraCharacteristicsCompat.getPhysicalCameraIds()) {
            if (!Objects.equals(physicalCameraId, this.mCameraId)) {
                try {
                    map.put(physicalCameraId, this.mCameraManager.getCameraCharacteristicsCompat(physicalCameraId).toCameraCharacteristics());
                } catch (CameraAccessExceptionCompat e) {
                    Logger.e(TAG, "Failed to get CameraCharacteristics for cameraId " + physicalCameraId, e);
                }
            }
        }
        return map;
    }

    static class RedirectableLiveData<T> extends MediatorLiveData<T> {
        private final T mInitialValue;
        private LiveData<T> mLiveDataSource;

        RedirectableLiveData(T initialValue) {
            this.mInitialValue = initialValue;
        }

        /* access modifiers changed from: package-private */
        public void redirectTo(LiveData<T> liveDataSource) {
            LiveData<T> liveData = this.mLiveDataSource;
            if (liveData != null) {
                super.removeSource(liveData);
            }
            this.mLiveDataSource = liveDataSource;
            super.addSource(liveDataSource, new Camera2CameraInfoImpl$RedirectableLiveData$$ExternalSyntheticLambda0(this));
        }

        public <S> void addSource(LiveData<S> liveData, Observer<? super S> observer) {
            throw new UnsupportedOperationException();
        }

        public T getValue() {
            LiveData<T> liveData = this.mLiveDataSource;
            return liveData == null ? this.mInitialValue : liveData.getValue();
        }
    }
}
