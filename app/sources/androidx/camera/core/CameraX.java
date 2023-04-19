package androidx.camera.core;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.SparseArray;
import androidx.camera.core.CameraXConfig;
import androidx.camera.core.impl.CameraDeviceSurfaceManager;
import androidx.camera.core.impl.CameraFactory;
import androidx.camera.core.impl.CameraRepository;
import androidx.camera.core.impl.CameraThreadConfig;
import androidx.camera.core.impl.CameraValidator;
import androidx.camera.core.impl.MetadataHolderService;
import androidx.camera.core.impl.UseCaseConfigFactory;
import androidx.camera.core.impl.utils.ContextUtil;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.os.HandlerCompat;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Executor;

public final class CameraX {
    private static final Object MIN_LOG_LEVEL_LOCK = new Object();
    private static final long RETRY_SLEEP_MILLIS = 500;
    private static final String RETRY_TOKEN = "retry_token";
    private static final String TAG = "CameraX";
    private static final long WAIT_INITIALIZED_TIMEOUT_MILLIS = 3000;
    private static final SparseArray<Integer> sMinLogLevelReferenceCountMap = new SparseArray<>();
    private Context mAppContext;
    private final Executor mCameraExecutor;
    private CameraFactory mCameraFactory;
    final CameraRepository mCameraRepository = new CameraRepository();
    private final CameraXConfig mCameraXConfig;
    private UseCaseConfigFactory mDefaultConfigFactory;
    private final ListenableFuture<Void> mInitInternalFuture;
    private InternalInitState mInitState = InternalInitState.UNINITIALIZED;
    private final Object mInitializeLock = new Object();
    private final Integer mMinLogLevel;
    private final Handler mSchedulerHandler;
    private final HandlerThread mSchedulerThread;
    private ListenableFuture<Void> mShutdownInternalFuture = Futures.immediateFuture(null);
    private CameraDeviceSurfaceManager mSurfaceManager;

    private enum InternalInitState {
        UNINITIALIZED,
        INITIALIZING,
        INITIALIZING_ERROR,
        INITIALIZED,
        SHUTDOWN
    }

    public CameraX(Context context, CameraXConfig.Provider configProvider) {
        if (configProvider != null) {
            this.mCameraXConfig = configProvider.getCameraXConfig();
        } else {
            CameraXConfig.Provider provider = getConfigProvider(context);
            if (provider != null) {
                this.mCameraXConfig = provider.getCameraXConfig();
            } else {
                throw new IllegalStateException("CameraX is not configured properly. The most likely cause is you did not include a default implementation in your build such as 'camera-camera2'.");
            }
        }
        Executor executor = this.mCameraXConfig.getCameraExecutor((Executor) null);
        Handler schedulerHandler = this.mCameraXConfig.getSchedulerHandler((Handler) null);
        this.mCameraExecutor = executor == null ? new CameraExecutor() : executor;
        if (schedulerHandler == null) {
            HandlerThread handlerThread = new HandlerThread("CameraX-scheduler", 10);
            this.mSchedulerThread = handlerThread;
            handlerThread.start();
            this.mSchedulerHandler = HandlerCompat.createAsync(handlerThread.getLooper());
        } else {
            this.mSchedulerThread = null;
            this.mSchedulerHandler = schedulerHandler;
        }
        Integer num = (Integer) this.mCameraXConfig.retrieveOption(CameraXConfig.OPTION_MIN_LOGGING_LEVEL, null);
        this.mMinLogLevel = num;
        increaseMinLogLevelReference(num);
        this.mInitInternalFuture = initInternal(context);
    }

    public CameraFactory getCameraFactory() {
        CameraFactory cameraFactory = this.mCameraFactory;
        if (cameraFactory != null) {
            return cameraFactory;
        }
        throw new IllegalStateException("CameraX not initialized yet.");
    }

    private static CameraXConfig.Provider getConfigProvider(Context context) {
        Application application = ContextUtil.getApplicationFromContext(context);
        if (application instanceof CameraXConfig.Provider) {
            return (CameraXConfig.Provider) application;
        }
        try {
            Context appContext = ContextUtil.getApplicationContext(context);
            ServiceInfo serviceInfo = appContext.getPackageManager().getServiceInfo(new ComponentName(appContext, MetadataHolderService.class), 640);
            String defaultProviderClassName = null;
            if (serviceInfo.metaData != null) {
                defaultProviderClassName = serviceInfo.metaData.getString("androidx.camera.core.impl.MetadataHolderService.DEFAULT_CONFIG_PROVIDER");
            }
            if (defaultProviderClassName != null) {
                return (CameraXConfig.Provider) Class.forName(defaultProviderClassName).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            Logger.e(TAG, "No default CameraXConfig.Provider specified in meta-data. The most likely cause is you did not include a default implementation in your build such as 'camera-camera2'.");
            return null;
        } catch (PackageManager.NameNotFoundException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | NullPointerException | InvocationTargetException e) {
            Logger.e(TAG, "Failed to retrieve default CameraXConfig.Provider from meta-data", e);
            return null;
        }
    }

    public CameraDeviceSurfaceManager getCameraDeviceSurfaceManager() {
        CameraDeviceSurfaceManager cameraDeviceSurfaceManager = this.mSurfaceManager;
        if (cameraDeviceSurfaceManager != null) {
            return cameraDeviceSurfaceManager;
        }
        throw new IllegalStateException("CameraX not initialized yet.");
    }

    public CameraRepository getCameraRepository() {
        return this.mCameraRepository;
    }

    public UseCaseConfigFactory getDefaultConfigFactory() {
        UseCaseConfigFactory useCaseConfigFactory = this.mDefaultConfigFactory;
        if (useCaseConfigFactory != null) {
            return useCaseConfigFactory;
        }
        throw new IllegalStateException("CameraX not initialized yet.");
    }

    public ListenableFuture<Void> getInitializeFuture() {
        return this.mInitInternalFuture;
    }

    public ListenableFuture<Void> shutdown() {
        return shutdownInternal();
    }

    private ListenableFuture<Void> initInternal(Context context) {
        ListenableFuture<Void> future;
        synchronized (this.mInitializeLock) {
            Preconditions.checkState(this.mInitState == InternalInitState.UNINITIALIZED, "CameraX.initInternal() should only be called once per instance");
            this.mInitState = InternalInitState.INITIALIZING;
            future = CallbackToFutureAdapter.getFuture(new CameraX$$ExternalSyntheticLambda1(this, context));
        }
        return future;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$initInternal$0$androidx-camera-core-CameraX  reason: not valid java name */
    public /* synthetic */ Object m131lambda$initInternal$0$androidxcameracoreCameraX(Context context, CallbackToFutureAdapter.Completer completer) throws Exception {
        initAndRetryRecursively(this.mCameraExecutor, SystemClock.elapsedRealtime(), context, completer);
        return "CameraX initInternal";
    }

    private void initAndRetryRecursively(Executor cameraExecutor, long startMs, Context context, CallbackToFutureAdapter.Completer<Void> completer) {
        cameraExecutor.execute(new CameraX$$ExternalSyntheticLambda2(this, context, cameraExecutor, completer, startMs));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$initAndRetryRecursively$2$androidx-camera-core-CameraX  reason: not valid java name */
    public /* synthetic */ void m130lambda$initAndRetryRecursively$2$androidxcameracoreCameraX(Context context, Executor cameraExecutor, CallbackToFutureAdapter.Completer completer, long startMs) {
        try {
            Application applicationFromContext = ContextUtil.getApplicationFromContext(context);
            this.mAppContext = applicationFromContext;
            if (applicationFromContext == null) {
                this.mAppContext = ContextUtil.getApplicationContext(context);
            }
            CameraFactory.Provider cameraFactoryProvider = this.mCameraXConfig.getCameraFactoryProvider((CameraFactory.Provider) null);
            if (cameraFactoryProvider != null) {
                CameraThreadConfig cameraThreadConfig = CameraThreadConfig.create(this.mCameraExecutor, this.mSchedulerHandler);
                CameraSelector availableCamerasLimiter = this.mCameraXConfig.getAvailableCamerasLimiter((CameraSelector) null);
                this.mCameraFactory = cameraFactoryProvider.newInstance(this.mAppContext, cameraThreadConfig, availableCamerasLimiter);
                CameraDeviceSurfaceManager.Provider surfaceManagerProvider = this.mCameraXConfig.getDeviceSurfaceManagerProvider((CameraDeviceSurfaceManager.Provider) null);
                if (surfaceManagerProvider != null) {
                    this.mSurfaceManager = surfaceManagerProvider.newInstance(this.mAppContext, this.mCameraFactory.getCameraManager(), this.mCameraFactory.getAvailableCameraIds());
                    UseCaseConfigFactory.Provider configFactoryProvider = this.mCameraXConfig.getUseCaseConfigFactoryProvider((UseCaseConfigFactory.Provider) null);
                    if (configFactoryProvider != null) {
                        this.mDefaultConfigFactory = configFactoryProvider.newInstance(this.mAppContext);
                        if (cameraExecutor instanceof CameraExecutor) {
                            ((CameraExecutor) cameraExecutor).init(this.mCameraFactory);
                        }
                        this.mCameraRepository.init(this.mCameraFactory);
                        CameraValidator.validateCameras(this.mAppContext, this.mCameraRepository, availableCamerasLimiter);
                        setStateToInitialized();
                        completer.set(null);
                        return;
                    }
                    throw new InitializationException((Throwable) new IllegalArgumentException("Invalid app configuration provided. Missing UseCaseConfigFactory."));
                }
                throw new InitializationException((Throwable) new IllegalArgumentException("Invalid app configuration provided. Missing CameraDeviceSurfaceManager."));
            }
            throw new InitializationException((Throwable) new IllegalArgumentException("Invalid app configuration provided. Missing CameraFactory."));
        } catch (InitializationException | CameraValidator.CameraIdListIncorrectException | RuntimeException e) {
            if (SystemClock.elapsedRealtime() - startMs < 2500) {
                Logger.w(TAG, "Retry init. Start time " + startMs + " current time " + SystemClock.elapsedRealtime(), e);
                HandlerCompat.postDelayed(this.mSchedulerHandler, new CameraX$$ExternalSyntheticLambda4(this, cameraExecutor, startMs, completer), RETRY_TOKEN, RETRY_SLEEP_MILLIS);
                return;
            }
            synchronized (this.mInitializeLock) {
                this.mInitState = InternalInitState.INITIALIZING_ERROR;
                if (e instanceof CameraValidator.CameraIdListIncorrectException) {
                    Logger.e(TAG, "The device might underreport the amount of the cameras. Finish the initialize task since we are already reaching the maximum number of retries.");
                    completer.set(null);
                } else if (e instanceof InitializationException) {
                    completer.setException(e);
                } else {
                    completer.setException(new InitializationException((Throwable) e));
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$initAndRetryRecursively$1$androidx-camera-core-CameraX  reason: not valid java name */
    public /* synthetic */ void m129lambda$initAndRetryRecursively$1$androidxcameracoreCameraX(Executor cameraExecutor, long startMs, CallbackToFutureAdapter.Completer completer) {
        initAndRetryRecursively(cameraExecutor, startMs, this.mAppContext, completer);
    }

    private void setStateToInitialized() {
        synchronized (this.mInitializeLock) {
            this.mInitState = InternalInitState.INITIALIZED;
        }
    }

    private ListenableFuture<Void> shutdownInternal() {
        synchronized (this.mInitializeLock) {
            this.mSchedulerHandler.removeCallbacksAndMessages(RETRY_TOKEN);
            switch (AnonymousClass1.$SwitchMap$androidx$camera$core$CameraX$InternalInitState[this.mInitState.ordinal()]) {
                case 1:
                    this.mInitState = InternalInitState.SHUTDOWN;
                    ListenableFuture<Void> immediateFuture = Futures.immediateFuture(null);
                    return immediateFuture;
                case 2:
                    throw new IllegalStateException("CameraX could not be shutdown when it is initializing.");
                case 3:
                case 4:
                    this.mInitState = InternalInitState.SHUTDOWN;
                    decreaseMinLogLevelReference(this.mMinLogLevel);
                    this.mShutdownInternalFuture = CallbackToFutureAdapter.getFuture(new CameraX$$ExternalSyntheticLambda0(this));
                    break;
            }
            ListenableFuture<Void> listenableFuture = this.mShutdownInternalFuture;
            return listenableFuture;
        }
    }

    /* renamed from: androidx.camera.core.CameraX$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$camera$core$CameraX$InternalInitState;

        static {
            int[] iArr = new int[InternalInitState.values().length];
            $SwitchMap$androidx$camera$core$CameraX$InternalInitState = iArr;
            try {
                iArr[InternalInitState.UNINITIALIZED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$camera$core$CameraX$InternalInitState[InternalInitState.INITIALIZING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$androidx$camera$core$CameraX$InternalInitState[InternalInitState.INITIALIZING_ERROR.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$androidx$camera$core$CameraX$InternalInitState[InternalInitState.INITIALIZED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$androidx$camera$core$CameraX$InternalInitState[InternalInitState.SHUTDOWN.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$shutdownInternal$4$androidx-camera-core-CameraX  reason: not valid java name */
    public /* synthetic */ Object m133lambda$shutdownInternal$4$androidxcameracoreCameraX(CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mCameraRepository.deinit().addListener(new CameraX$$ExternalSyntheticLambda3(this, completer), this.mCameraExecutor);
        return "CameraX shutdownInternal";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$shutdownInternal$3$androidx-camera-core-CameraX  reason: not valid java name */
    public /* synthetic */ void m132lambda$shutdownInternal$3$androidxcameracoreCameraX(CallbackToFutureAdapter.Completer completer) {
        if (this.mSchedulerThread != null) {
            Executor executor = this.mCameraExecutor;
            if (executor instanceof CameraExecutor) {
                ((CameraExecutor) executor).deinit();
            }
            this.mSchedulerThread.quit();
        }
        completer.set(null);
    }

    /* access modifiers changed from: package-private */
    public boolean isInitialized() {
        boolean z;
        synchronized (this.mInitializeLock) {
            z = this.mInitState == InternalInitState.INITIALIZED;
        }
        return z;
    }

    private static void increaseMinLogLevelReference(Integer minLogLevel) {
        synchronized (MIN_LOG_LEVEL_LOCK) {
            if (minLogLevel != null) {
                Preconditions.checkArgumentInRange(minLogLevel.intValue(), 3, 6, "minLogLevel");
                int refCount = 1;
                SparseArray<Integer> sparseArray = sMinLogLevelReferenceCountMap;
                if (sparseArray.get(minLogLevel.intValue()) != null) {
                    refCount = sparseArray.get(minLogLevel.intValue()).intValue() + 1;
                }
                sparseArray.put(minLogLevel.intValue(), Integer.valueOf(refCount));
                updateOrResetMinLogLevel();
            }
        }
    }

    private static void decreaseMinLogLevelReference(Integer minLogLevel) {
        synchronized (MIN_LOG_LEVEL_LOCK) {
            if (minLogLevel != null) {
                SparseArray<Integer> sparseArray = sMinLogLevelReferenceCountMap;
                int refCount = sparseArray.get(minLogLevel.intValue()).intValue() - 1;
                if (refCount == 0) {
                    sparseArray.remove(minLogLevel.intValue());
                } else {
                    sparseArray.put(minLogLevel.intValue(), Integer.valueOf(refCount));
                }
                updateOrResetMinLogLevel();
            }
        }
    }

    private static void updateOrResetMinLogLevel() {
        SparseArray<Integer> sparseArray = sMinLogLevelReferenceCountMap;
        if (sparseArray.size() == 0) {
            Logger.resetMinLogLevel();
        } else if (sparseArray.get(3) != null) {
            Logger.setMinLogLevel(3);
        } else if (sparseArray.get(4) != null) {
            Logger.setMinLogLevel(4);
        } else if (sparseArray.get(5) != null) {
            Logger.setMinLogLevel(5);
        } else if (sparseArray.get(6) != null) {
            Logger.setMinLogLevel(6);
        }
    }
}
