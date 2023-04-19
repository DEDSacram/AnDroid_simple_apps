package androidx.camera.core.internal;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Logger;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.UseCase;
import androidx.camera.core.ViewPort;
import androidx.camera.core.impl.AttachedSurfaceInfo;
import androidx.camera.core.impl.CameraConfig;
import androidx.camera.core.impl.CameraConfigs;
import androidx.camera.core.impl.CameraControlInternal;
import androidx.camera.core.impl.CameraDeviceSurfaceManager;
import androidx.camera.core.impl.CameraInfoInternal;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.Config;
import androidx.camera.core.impl.UseCaseConfig;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public final class CameraUseCaseAdapter implements Camera {
    private static final String TAG = "CameraUseCaseAdapter";
    private boolean mAttached = true;
    private CameraConfig mCameraConfig = CameraConfigs.emptyConfig();
    private final CameraDeviceSurfaceManager mCameraDeviceSurfaceManager;
    private CameraInternal mCameraInternal;
    private final LinkedHashSet<CameraInternal> mCameraInternals;
    private List<UseCase> mExtraUseCases = new ArrayList();
    private final CameraId mId;
    private Config mInteropConfig = null;
    private final Object mLock = new Object();
    private final UseCaseConfigFactory mUseCaseConfigFactory;
    private final List<UseCase> mUseCases = new ArrayList();
    private ViewPort mViewPort;

    public CameraUseCaseAdapter(LinkedHashSet<CameraInternal> cameras, CameraDeviceSurfaceManager cameraDeviceSurfaceManager, UseCaseConfigFactory useCaseConfigFactory) {
        this.mCameraInternal = (CameraInternal) cameras.iterator().next();
        LinkedHashSet<CameraInternal> linkedHashSet = new LinkedHashSet<>(cameras);
        this.mCameraInternals = linkedHashSet;
        this.mId = new CameraId(linkedHashSet);
        this.mCameraDeviceSurfaceManager = cameraDeviceSurfaceManager;
        this.mUseCaseConfigFactory = useCaseConfigFactory;
    }

    public static CameraId generateCameraId(LinkedHashSet<CameraInternal> cameras) {
        return new CameraId(cameras);
    }

    public CameraId getCameraId() {
        return this.mId;
    }

    public boolean isEquivalent(CameraUseCaseAdapter cameraUseCaseAdapter) {
        return this.mId.equals(cameraUseCaseAdapter.getCameraId());
    }

    public void setViewPort(ViewPort viewPort) {
        synchronized (this.mLock) {
            this.mViewPort = viewPort;
        }
    }

    public void addUseCases(Collection<UseCase> useCases) throws CameraException {
        synchronized (this.mLock) {
            List<UseCase> newUseCases = new ArrayList<>();
            for (UseCase useCase : useCases) {
                if (this.mUseCases.contains(useCase)) {
                    Logger.d(TAG, "Attempting to attach already attached UseCase");
                } else {
                    newUseCases.add(useCase);
                }
            }
            List<UseCase> allUseCases = new ArrayList<>(this.mUseCases);
            List<UseCase> requiredExtraUseCases = Collections.emptyList();
            List<UseCase> removedExtraUseCases = Collections.emptyList();
            if (isCoexistingPreviewImageCaptureRequired()) {
                allUseCases.removeAll(this.mExtraUseCases);
                allUseCases.addAll(newUseCases);
                requiredExtraUseCases = calculateRequiredExtraUseCases(allUseCases, new ArrayList(this.mExtraUseCases));
                List<UseCase> addedExtraUseCases = new ArrayList<>(requiredExtraUseCases);
                addedExtraUseCases.removeAll(this.mExtraUseCases);
                newUseCases.addAll(addedExtraUseCases);
                removedExtraUseCases = new ArrayList<>(this.mExtraUseCases);
                removedExtraUseCases.removeAll(requiredExtraUseCases);
            }
            Map<UseCase, ConfigPair> configs = getConfigs(newUseCases, this.mCameraConfig.getUseCaseConfigFactory(), this.mUseCaseConfigFactory);
            try {
                List<UseCase> boundUseCases = new ArrayList<>(this.mUseCases);
                boundUseCases.removeAll(removedExtraUseCases);
                Map<UseCase, Size> suggestedResolutionsMap = calculateSuggestedResolutions(this.mCameraInternal.getCameraInfoInternal(), newUseCases, boundUseCases, configs);
                updateViewPort(suggestedResolutionsMap, useCases);
                this.mExtraUseCases = requiredExtraUseCases;
                detachUnnecessaryUseCases(removedExtraUseCases);
                for (UseCase useCase2 : newUseCases) {
                    ConfigPair configPair = configs.get(useCase2);
                    useCase2.onAttach(this.mCameraInternal, configPair.mExtendedConfig, configPair.mCameraConfig);
                    useCase2.updateSuggestedResolution((Size) Preconditions.checkNotNull(suggestedResolutionsMap.get(useCase2)));
                }
                this.mUseCases.addAll(newUseCases);
                if (this.mAttached) {
                    this.mCameraInternal.attachUseCases(newUseCases);
                }
                for (UseCase useCase3 : newUseCases) {
                    useCase3.notifyState();
                }
            } catch (IllegalArgumentException e) {
                throw new CameraException(e.getMessage());
            }
        }
    }

    public void removeUseCases(Collection<UseCase> useCases) {
        synchronized (this.mLock) {
            detachUnnecessaryUseCases(new ArrayList(useCases));
            if (isCoexistingPreviewImageCaptureRequired()) {
                this.mExtraUseCases.removeAll(useCases);
                try {
                    addUseCases(Collections.emptyList());
                } catch (CameraException e) {
                    throw new IllegalArgumentException("Failed to add extra fake Preview or ImageCapture use case!");
                }
            }
        }
    }

    public List<UseCase> getUseCases() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mUseCases);
        }
        return arrayList;
    }

    public void attachUseCases() {
        synchronized (this.mLock) {
            if (!this.mAttached) {
                this.mCameraInternal.attachUseCases(this.mUseCases);
                restoreInteropConfig();
                for (UseCase useCase : this.mUseCases) {
                    useCase.notifyState();
                }
                this.mAttached = true;
            }
        }
    }

    public void setActiveResumingMode(boolean enabled) {
        this.mCameraInternal.setActiveResumingMode(enabled);
    }

    public void detachUseCases() {
        synchronized (this.mLock) {
            if (this.mAttached) {
                this.mCameraInternal.detachUseCases(new ArrayList(this.mUseCases));
                cacheInteropConfig();
                this.mAttached = false;
            }
        }
    }

    private void restoreInteropConfig() {
        synchronized (this.mLock) {
            if (this.mInteropConfig != null) {
                this.mCameraInternal.getCameraControlInternal().addInteropConfig(this.mInteropConfig);
            }
        }
    }

    private void cacheInteropConfig() {
        synchronized (this.mLock) {
            CameraControlInternal cameraControlInternal = this.mCameraInternal.getCameraControlInternal();
            this.mInteropConfig = cameraControlInternal.getInteropConfig();
            cameraControlInternal.clearInteropConfig();
        }
    }

    private Map<UseCase, Size> calculateSuggestedResolutions(CameraInfoInternal cameraInfoInternal, List<UseCase> newUseCases, List<UseCase> currentUseCases, Map<UseCase, ConfigPair> configPairMap) {
        List<AttachedSurfaceInfo> existingSurfaces = new ArrayList<>();
        String cameraId = cameraInfoInternal.getCameraId();
        Map<UseCase, Size> suggestedResolutions = new HashMap<>();
        for (UseCase useCase : currentUseCases) {
            existingSurfaces.add(AttachedSurfaceInfo.create(this.mCameraDeviceSurfaceManager.transformSurfaceConfig(cameraId, useCase.getImageFormat(), useCase.getAttachedSurfaceResolution()), useCase.getImageFormat(), useCase.getAttachedSurfaceResolution(), useCase.getCurrentConfig().getTargetFramerate((Range<Integer>) null)));
            suggestedResolutions.put(useCase, useCase.getAttachedSurfaceResolution());
        }
        if (!newUseCases.isEmpty()) {
            Map<UseCaseConfig<?>, UseCase> configToUseCaseMap = new HashMap<>();
            for (UseCase useCase2 : newUseCases) {
                ConfigPair configPair = configPairMap.get(useCase2);
                configToUseCaseMap.put(useCase2.mergeConfigs(cameraInfoInternal, configPair.mExtendedConfig, configPair.mCameraConfig), useCase2);
            }
            Map<UseCaseConfig<?>, Size> useCaseConfigSizeMap = this.mCameraDeviceSurfaceManager.getSuggestedResolutions(cameraId, existingSurfaces, new ArrayList(configToUseCaseMap.keySet()));
            for (Map.Entry<UseCaseConfig<?>, UseCase> entry : configToUseCaseMap.entrySet()) {
                suggestedResolutions.put(entry.getValue(), useCaseConfigSizeMap.get(entry.getKey()));
            }
        }
        return suggestedResolutions;
    }

    private void updateViewPort(Map<UseCase, Size> suggestedResolutionsMap, Collection<UseCase> useCases) {
        synchronized (this.mLock) {
            if (this.mViewPort != null) {
                Map<UseCase, Rect> cropRectMap = ViewPorts.calculateViewPortRects(this.mCameraInternal.getCameraControlInternal().getSensorRect(), this.mCameraInternal.getCameraInfoInternal().getLensFacing().intValue() == 0, this.mViewPort.getAspectRatio(), this.mCameraInternal.getCameraInfoInternal().getSensorRotationDegrees(this.mViewPort.getRotation()), this.mViewPort.getScaleType(), this.mViewPort.getLayoutDirection(), suggestedResolutionsMap);
                for (UseCase useCase : useCases) {
                    useCase.setViewPortCropRect((Rect) Preconditions.checkNotNull(cropRectMap.get(useCase)));
                    useCase.setSensorToBufferTransformMatrix(calculateSensorToBufferTransformMatrix(this.mCameraInternal.getCameraControlInternal().getSensorRect(), suggestedResolutionsMap.get(useCase)));
                }
            }
        }
    }

    private static Matrix calculateSensorToBufferTransformMatrix(Rect fullSensorRect, Size useCaseSize) {
        Preconditions.checkArgument(fullSensorRect.width() > 0 && fullSensorRect.height() > 0, "Cannot compute viewport crop rects zero sized sensor rect.");
        RectF fullSensorRectF = new RectF(fullSensorRect);
        Matrix sensorToUseCaseTransformation = new Matrix();
        sensorToUseCaseTransformation.setRectToRect(new RectF(0.0f, 0.0f, (float) useCaseSize.getWidth(), (float) useCaseSize.getHeight()), fullSensorRectF, Matrix.ScaleToFit.CENTER);
        sensorToUseCaseTransformation.invert(sensorToUseCaseTransformation);
        return sensorToUseCaseTransformation;
    }

    private static class ConfigPair {
        UseCaseConfig<?> mCameraConfig;
        UseCaseConfig<?> mExtendedConfig;

        ConfigPair(UseCaseConfig<?> extendedConfig, UseCaseConfig<?> cameraConfig) {
            this.mExtendedConfig = extendedConfig;
            this.mCameraConfig = cameraConfig;
        }
    }

    private Map<UseCase, ConfigPair> getConfigs(List<UseCase> useCases, UseCaseConfigFactory extendedFactory, UseCaseConfigFactory cameraFactory) {
        Map<UseCase, ConfigPair> configs = new HashMap<>();
        for (UseCase useCase : useCases) {
            configs.put(useCase, new ConfigPair(useCase.getDefaultConfig(false, extendedFactory), useCase.getDefaultConfig(true, cameraFactory)));
        }
        return configs;
    }

    public static final class CameraId {
        private final List<String> mIds = new ArrayList();

        CameraId(LinkedHashSet<CameraInternal> cameraInternals) {
            Iterator it = cameraInternals.iterator();
            while (it.hasNext()) {
                this.mIds.add(((CameraInternal) it.next()).getCameraInfoInternal().getCameraId());
            }
        }

        public boolean equals(Object cameraId) {
            if (cameraId instanceof CameraId) {
                return this.mIds.equals(((CameraId) cameraId).mIds);
            }
            return false;
        }

        public int hashCode() {
            return this.mIds.hashCode() * 53;
        }
    }

    public static final class CameraException extends Exception {
        public CameraException() {
        }

        public CameraException(String message) {
            super(message);
        }

        public CameraException(Throwable cause) {
            super(cause);
        }
    }

    public CameraControl getCameraControl() {
        return this.mCameraInternal.getCameraControlInternal();
    }

    public CameraInfo getCameraInfo() {
        return this.mCameraInternal.getCameraInfoInternal();
    }

    public LinkedHashSet<CameraInternal> getCameraInternals() {
        return this.mCameraInternals;
    }

    public CameraConfig getExtendedConfig() {
        CameraConfig cameraConfig;
        synchronized (this.mLock) {
            cameraConfig = this.mCameraConfig;
        }
        return cameraConfig;
    }

    public void setExtendedConfig(CameraConfig cameraConfig) {
        synchronized (this.mLock) {
            if (cameraConfig == null) {
                cameraConfig = CameraConfigs.emptyConfig();
            }
            if (!this.mUseCases.isEmpty()) {
                if (!this.mCameraConfig.getCompatibilityId().equals(cameraConfig.getCompatibilityId())) {
                    throw new IllegalStateException("Need to unbind all use cases before binding with extension enabled");
                }
            }
            this.mCameraConfig = cameraConfig;
            this.mCameraInternal.setExtendedConfig(cameraConfig);
        }
    }

    public boolean isUseCasesCombinationSupported(UseCase... useCases) {
        synchronized (this.mLock) {
            try {
                calculateSuggestedResolutions(this.mCameraInternal.getCameraInfoInternal(), Arrays.asList(useCases), Collections.emptyList(), getConfigs(Arrays.asList(useCases), this.mCameraConfig.getUseCaseConfigFactory(), this.mUseCaseConfigFactory));
            } catch (IllegalArgumentException e) {
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return true;
    }

    private List<UseCase> calculateRequiredExtraUseCases(List<UseCase> boundUseCases, List<UseCase> extraUseCases) {
        List<UseCase> requiredExtraUseCases = new ArrayList<>(extraUseCases);
        boolean isExtraPreviewRequired = isExtraPreviewRequired(boundUseCases);
        boolean isExtraImageCaptureRequired = isExtraImageCaptureRequired(boundUseCases);
        UseCase existingExtraPreview = null;
        UseCase existingExtraImageCapture = null;
        for (UseCase useCase : extraUseCases) {
            if (isPreview(useCase)) {
                existingExtraPreview = useCase;
            } else if (isImageCapture(useCase)) {
                existingExtraImageCapture = useCase;
            }
        }
        if (isExtraPreviewRequired && existingExtraPreview == null) {
            requiredExtraUseCases.add(createExtraPreview());
        } else if (!isExtraPreviewRequired && existingExtraPreview != null) {
            requiredExtraUseCases.remove(existingExtraPreview);
        }
        if (isExtraImageCaptureRequired && existingExtraImageCapture == null) {
            requiredExtraUseCases.add(createExtraImageCapture());
        } else if (!isExtraImageCaptureRequired && existingExtraImageCapture != null) {
            requiredExtraUseCases.remove(existingExtraImageCapture);
        }
        return requiredExtraUseCases;
    }

    private void detachUnnecessaryUseCases(List<UseCase> unnecessaryUseCases) {
        synchronized (this.mLock) {
            if (!unnecessaryUseCases.isEmpty()) {
                this.mCameraInternal.detachUseCases(unnecessaryUseCases);
                for (UseCase useCase : unnecessaryUseCases) {
                    if (this.mUseCases.contains(useCase)) {
                        useCase.onDetach(this.mCameraInternal);
                    } else {
                        Logger.e(TAG, "Attempting to detach non-attached UseCase: " + useCase);
                    }
                }
                this.mUseCases.removeAll(unnecessaryUseCases);
            }
        }
    }

    private boolean isCoexistingPreviewImageCaptureRequired() {
        boolean z;
        synchronized (this.mLock) {
            z = true;
            if (this.mCameraConfig.getUseCaseCombinationRequiredRule() != 1) {
                z = false;
            }
        }
        return z;
    }

    private boolean isExtraPreviewRequired(List<UseCase> useCases) {
        boolean hasPreview = false;
        boolean hasImageCapture = false;
        for (UseCase useCase : useCases) {
            if (isPreview(useCase)) {
                hasPreview = true;
            } else if (isImageCapture(useCase)) {
                hasImageCapture = true;
            }
        }
        return hasImageCapture && !hasPreview;
    }

    private boolean isExtraImageCaptureRequired(List<UseCase> useCases) {
        boolean hasPreview = false;
        boolean hasImageCapture = false;
        for (UseCase useCase : useCases) {
            if (isPreview(useCase)) {
                hasPreview = true;
            } else if (isImageCapture(useCase)) {
                hasImageCapture = true;
            }
        }
        return hasPreview && !hasImageCapture;
    }

    private boolean isPreview(UseCase useCase) {
        return useCase instanceof Preview;
    }

    private boolean isImageCapture(UseCase useCase) {
        return useCase instanceof ImageCapture;
    }

    private Preview createExtraPreview() {
        Preview preview = new Preview.Builder().setTargetName("Preview-Extra").build();
        preview.setSurfaceProvider(CameraUseCaseAdapter$$ExternalSyntheticLambda0.INSTANCE);
        return preview;
    }

    static /* synthetic */ void lambda$createExtraPreview$1(SurfaceRequest surfaceRequest) {
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        surfaceTexture.setDefaultBufferSize(surfaceRequest.getResolution().getWidth(), surfaceRequest.getResolution().getHeight());
        surfaceTexture.detachFromGLContext();
        Surface surface = new Surface(surfaceTexture);
        surfaceRequest.provideSurface(surface, CameraXExecutors.directExecutor(), new CameraUseCaseAdapter$$ExternalSyntheticLambda1(surface, surfaceTexture));
    }

    static /* synthetic */ void lambda$createExtraPreview$0(Surface surface, SurfaceTexture surfaceTexture, SurfaceRequest.Result surfaceResponse) {
        surface.release();
        surfaceTexture.release();
    }

    private ImageCapture createExtraImageCapture() {
        return new ImageCapture.Builder().setTargetName("ImageCapture-Extra").build();
    }
}
