package androidx.camera.lifecycle;

import android.content.Context;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraFilter;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.UseCase;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ViewPort;
import androidx.camera.core.impl.CameraConfig;
import androidx.camera.core.impl.CameraInternal;
import androidx.camera.core.impl.ExtendedCameraConfigProviderStore;
import androidx.camera.core.impl.utils.ContextUtil;
import androidx.camera.core.impl.utils.Threads;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.FutureChain;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.camera.core.internal.CameraUseCaseAdapter;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public final class ProcessCameraProvider implements LifecycleCameraProvider {
    private static final ProcessCameraProvider sAppInstance = new ProcessCameraProvider();
    private CameraX mCameraX;
    private CameraXConfig.Provider mCameraXConfigProvider = null;
    private ListenableFuture<CameraX> mCameraXInitializeFuture;
    private ListenableFuture<Void> mCameraXShutdownFuture = Futures.immediateFuture(null);
    private Context mContext;
    private final LifecycleCameraRepository mLifecycleCameraRepository = new LifecycleCameraRepository();
    private final Object mLock = new Object();

    public static ListenableFuture<ProcessCameraProvider> getInstance(Context context) {
        Preconditions.checkNotNull(context);
        return Futures.transform(sAppInstance.getOrCreateCameraXInstance(context), new ProcessCameraProvider$$ExternalSyntheticLambda0(context), CameraXExecutors.directExecutor());
    }

    static /* synthetic */ ProcessCameraProvider lambda$getInstance$0(Context context, CameraX cameraX) {
        ProcessCameraProvider processCameraProvider = sAppInstance;
        processCameraProvider.setCameraX(cameraX);
        processCameraProvider.setContext(ContextUtil.getApplicationContext(context));
        return processCameraProvider;
    }

    private ListenableFuture<CameraX> getOrCreateCameraXInstance(Context context) {
        synchronized (this.mLock) {
            ListenableFuture<CameraX> listenableFuture = this.mCameraXInitializeFuture;
            if (listenableFuture != null) {
                return listenableFuture;
            }
            ListenableFuture<CameraX> future = CallbackToFutureAdapter.getFuture(new ProcessCameraProvider$$ExternalSyntheticLambda3(this, new CameraX(context, this.mCameraXConfigProvider)));
            this.mCameraXInitializeFuture = future;
            return future;
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getOrCreateCameraXInstance$2$androidx-camera-lifecycle-ProcessCameraProvider  reason: not valid java name */
    public /* synthetic */ Object m194lambda$getOrCreateCameraXInstance$2$androidxcameralifecycleProcessCameraProvider(final CameraX cameraX, final CallbackToFutureAdapter.Completer completer) throws Exception {
        synchronized (this.mLock) {
            Futures.addCallback(FutureChain.from(this.mCameraXShutdownFuture).transformAsync(new ProcessCameraProvider$$ExternalSyntheticLambda2(cameraX), CameraXExecutors.directExecutor()), new FutureCallback<Void>() {
                public void onSuccess(Void result) {
                    completer.set(cameraX);
                }

                public void onFailure(Throwable t) {
                    completer.setException(t);
                }
            }, CameraXExecutors.directExecutor());
        }
        return "ProcessCameraProvider-initializeCameraX";
    }

    public static void configureInstance(CameraXConfig cameraXConfig) {
        sAppInstance.configureInstanceInternal(cameraXConfig);
    }

    private void configureInstanceInternal(CameraXConfig cameraXConfig) {
        synchronized (this.mLock) {
            Preconditions.checkNotNull(cameraXConfig);
            Preconditions.checkState(this.mCameraXConfigProvider == null, "CameraX has already been configured. To use a different configuration, shutdown() must be called.");
            this.mCameraXConfigProvider = new ProcessCameraProvider$$ExternalSyntheticLambda1(cameraXConfig);
        }
    }

    static /* synthetic */ CameraXConfig lambda$configureInstanceInternal$3(CameraXConfig cameraXConfig) {
        return cameraXConfig;
    }

    public ListenableFuture<Void> shutdown() {
        ListenableFuture<Void> shutdownFuture;
        this.mLifecycleCameraRepository.clear();
        CameraX cameraX = this.mCameraX;
        if (cameraX != null) {
            shutdownFuture = cameraX.shutdown();
        } else {
            shutdownFuture = Futures.immediateFuture(null);
        }
        synchronized (this.mLock) {
            this.mCameraXConfigProvider = null;
            this.mCameraXInitializeFuture = null;
            this.mCameraXShutdownFuture = shutdownFuture;
        }
        this.mCameraX = null;
        this.mContext = null;
        return shutdownFuture;
    }

    private void setCameraX(CameraX cameraX) {
        this.mCameraX = cameraX;
    }

    private void setContext(Context context) {
        this.mContext = context;
    }

    public Camera bindToLifecycle(LifecycleOwner lifecycleOwner, CameraSelector cameraSelector, UseCase... useCases) {
        return bindToLifecycle(lifecycleOwner, cameraSelector, (ViewPort) null, useCases);
    }

    public Camera bindToLifecycle(LifecycleOwner lifecycleOwner, CameraSelector cameraSelector, UseCaseGroup useCaseGroup) {
        return bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup.getViewPort(), (UseCase[]) useCaseGroup.getUseCases().toArray(new UseCase[0]));
    }

    /* access modifiers changed from: package-private */
    public Camera bindToLifecycle(LifecycleOwner lifecycleOwner, CameraSelector cameraSelector, ViewPort viewPort, UseCase... useCases) {
        CameraConfig extendedCameraConfig;
        LifecycleOwner lifecycleOwner2 = lifecycleOwner;
        UseCase[] useCaseArr = useCases;
        Threads.checkMainThread();
        CameraSelector.Builder selectorBuilder = CameraSelector.Builder.fromSelector(cameraSelector);
        for (UseCase useCase : useCaseArr) {
            CameraSelector selector = useCase.getCurrentConfig().getCameraSelector((CameraSelector) null);
            if (selector != null) {
                Iterator it = selector.getCameraFilterSet().iterator();
                while (it.hasNext()) {
                    selectorBuilder.addCameraFilter((CameraFilter) it.next());
                }
            }
        }
        LinkedHashSet<CameraInternal> cameraInternals = selectorBuilder.build().filter(this.mCameraX.getCameraRepository().getCameras());
        if (!cameraInternals.isEmpty()) {
            LifecycleCamera lifecycleCameraToBind = this.mLifecycleCameraRepository.getLifecycleCamera(lifecycleOwner2, CameraUseCaseAdapter.generateCameraId(cameraInternals));
            Collection<LifecycleCamera> lifecycleCameras = this.mLifecycleCameraRepository.getLifecycleCameras();
            for (UseCase useCase2 : useCaseArr) {
                for (LifecycleCamera lifecycleCamera : lifecycleCameras) {
                    if (lifecycleCamera.isBound(useCase2) && lifecycleCamera != lifecycleCameraToBind) {
                        throw new IllegalStateException(String.format("Use case %s already bound to a different lifecycle.", new Object[]{useCase2}));
                    }
                }
            }
            if (lifecycleCameraToBind == null) {
                lifecycleCameraToBind = this.mLifecycleCameraRepository.createLifecycleCamera(lifecycleOwner2, new CameraUseCaseAdapter(cameraInternals, this.mCameraX.getCameraDeviceSurfaceManager(), this.mCameraX.getDefaultConfigFactory()));
            }
            CameraConfig cameraConfig = null;
            Iterator it2 = cameraSelector.getCameraFilterSet().iterator();
            while (it2.hasNext()) {
                CameraFilter cameraFilter = (CameraFilter) it2.next();
                if (!(cameraFilter.getIdentifier() == CameraFilter.DEFAULT_ID || (extendedCameraConfig = ExtendedCameraConfigProviderStore.getConfigProvider(cameraFilter.getIdentifier()).getConfig(lifecycleCameraToBind.getCameraInfo(), this.mContext)) == null)) {
                    if (cameraConfig == null) {
                        cameraConfig = extendedCameraConfig;
                    } else {
                        throw new IllegalArgumentException("Cannot apply multiple extended camera configs at the same time.");
                    }
                }
            }
            lifecycleCameraToBind.setExtendedConfig(cameraConfig);
            if (useCaseArr.length == 0) {
                return lifecycleCameraToBind;
            }
            this.mLifecycleCameraRepository.bindToLifecycleCamera(lifecycleCameraToBind, viewPort, Arrays.asList(useCases));
            return lifecycleCameraToBind;
        }
        ViewPort viewPort2 = viewPort;
        throw new IllegalArgumentException("Provided camera selector unable to resolve a camera for the given use case");
    }

    public boolean isBound(UseCase useCase) {
        for (LifecycleCamera lifecycleCamera : this.mLifecycleCameraRepository.getLifecycleCameras()) {
            if (lifecycleCamera.isBound(useCase)) {
                return true;
            }
        }
        return false;
    }

    public void unbind(UseCase... useCases) {
        Threads.checkMainThread();
        this.mLifecycleCameraRepository.unbind(Arrays.asList(useCases));
    }

    public void unbindAll() {
        Threads.checkMainThread();
        this.mLifecycleCameraRepository.unbindAll();
    }

    public boolean hasCamera(CameraSelector cameraSelector) throws CameraInfoUnavailableException {
        try {
            cameraSelector.select(this.mCameraX.getCameraRepository().getCameras());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<CameraInfo> getAvailableCameraInfos() {
        List<CameraInfo> availableCameraInfos = new ArrayList<>();
        for (CameraInternal camera : this.mCameraX.getCameraRepository().getCameras()) {
            availableCameraInfos.add(camera.getCameraInfo());
        }
        return availableCameraInfos;
    }

    private ProcessCameraProvider() {
    }
}
