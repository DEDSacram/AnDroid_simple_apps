package androidx.camera.core.processing;

import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;
import androidx.camera.core.SurfaceOutput;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultSurfaceEffect implements SurfaceEffectInternal, SurfaceTexture.OnFrameAvailableListener {
    private final Executor mGlExecutor;
    final Handler mGlHandler;
    private final OpenGlRenderer mGlRenderer;
    final HandlerThread mGlThread;
    private int mInputSurfaceCount = 0;
    private final AtomicBoolean mIsReleased = new AtomicBoolean(false);
    final Map<SurfaceOutput, Surface> mOutputSurfaces = new LinkedHashMap();
    private final float[] mSurfaceOutputMatrix = new float[16];
    private final float[] mTextureMatrix = new float[16];

    public DefaultSurfaceEffect(ShaderProvider shaderProvider) {
        HandlerThread handlerThread = new HandlerThread("GL Thread");
        this.mGlThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        this.mGlHandler = handler;
        this.mGlExecutor = CameraXExecutors.newHandlerExecutor(handler);
        this.mGlRenderer = new OpenGlRenderer();
        try {
            initGlRenderer(shaderProvider);
        } catch (RuntimeException e) {
            release();
            throw e;
        }
    }

    public void onInputSurface(SurfaceRequest surfaceRequest) {
        if (this.mIsReleased.get()) {
            surfaceRequest.willNotProvideSurface();
        } else {
            this.mGlExecutor.execute(new DefaultSurfaceEffect$$ExternalSyntheticLambda5(this, surfaceRequest));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onInputSurface$1$androidx-camera-core-processing-DefaultSurfaceEffect  reason: not valid java name */
    public /* synthetic */ void m185lambda$onInputSurface$1$androidxcameracoreprocessingDefaultSurfaceEffect(SurfaceRequest surfaceRequest) {
        this.mInputSurfaceCount++;
        SurfaceTexture surfaceTexture = new SurfaceTexture(this.mGlRenderer.getTextureName());
        surfaceTexture.setDefaultBufferSize(surfaceRequest.getResolution().getWidth(), surfaceRequest.getResolution().getHeight());
        Surface surface = new Surface(surfaceTexture);
        surfaceRequest.provideSurface(surface, this.mGlExecutor, new DefaultSurfaceEffect$$ExternalSyntheticLambda2(this, surfaceTexture, surface));
        surfaceTexture.setOnFrameAvailableListener(this, this.mGlHandler);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onInputSurface$0$androidx-camera-core-processing-DefaultSurfaceEffect  reason: not valid java name */
    public /* synthetic */ void m184lambda$onInputSurface$0$androidxcameracoreprocessingDefaultSurfaceEffect(SurfaceTexture surfaceTexture, Surface surface, SurfaceRequest.Result result) {
        surfaceTexture.setOnFrameAvailableListener((SurfaceTexture.OnFrameAvailableListener) null);
        surfaceTexture.release();
        surface.release();
        this.mInputSurfaceCount--;
        checkReadyToRelease();
    }

    public void onOutputSurface(SurfaceOutput surfaceOutput) {
        if (this.mIsReleased.get()) {
            surfaceOutput.close();
        } else {
            this.mGlExecutor.execute(new DefaultSurfaceEffect$$ExternalSyntheticLambda4(this, surfaceOutput));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onOutputSurface$3$androidx-camera-core-processing-DefaultSurfaceEffect  reason: not valid java name */
    public /* synthetic */ void m187lambda$onOutputSurface$3$androidxcameracoreprocessingDefaultSurfaceEffect(SurfaceOutput surfaceOutput) {
        this.mOutputSurfaces.put(surfaceOutput, surfaceOutput.getSurface(this.mGlExecutor, new DefaultSurfaceEffect$$ExternalSyntheticLambda0(this, surfaceOutput)));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$onOutputSurface$2$androidx-camera-core-processing-DefaultSurfaceEffect  reason: not valid java name */
    public /* synthetic */ void m186lambda$onOutputSurface$2$androidxcameracoreprocessingDefaultSurfaceEffect(SurfaceOutput surfaceOutput) {
        surfaceOutput.close();
        this.mOutputSurfaces.remove(surfaceOutput);
    }

    public void release() {
        if (!this.mIsReleased.getAndSet(true)) {
            this.mGlExecutor.execute(new DefaultSurfaceEffect$$ExternalSyntheticLambda3(this));
        }
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (!this.mIsReleased.get()) {
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(this.mTextureMatrix);
            for (Map.Entry<SurfaceOutput, Surface> entry : this.mOutputSurfaces.entrySet()) {
                this.mGlRenderer.setOutputSurface(entry.getValue());
                entry.getKey().updateTransformMatrix(this.mSurfaceOutputMatrix, this.mTextureMatrix);
                this.mGlRenderer.render(surfaceTexture.getTimestamp(), this.mSurfaceOutputMatrix);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkReadyToRelease() {
        if (this.mIsReleased.get() && this.mInputSurfaceCount == 0) {
            for (SurfaceOutput surfaceOutput : this.mOutputSurfaces.keySet()) {
                surfaceOutput.close();
            }
            this.mOutputSurfaces.clear();
            this.mGlRenderer.release();
            this.mGlThread.quit();
        }
    }

    private void initGlRenderer(ShaderProvider shaderProvider) {
        try {
            CallbackToFutureAdapter.getFuture(new DefaultSurfaceEffect$$ExternalSyntheticLambda1(this, shaderProvider)).get();
        } catch (InterruptedException | ExecutionException e) {
            Throwable cause = e instanceof ExecutionException ? e.getCause() : e;
            if (cause instanceof RuntimeException) {
                throw ((RuntimeException) cause);
            }
            throw new IllegalStateException("Failed to create DefaultSurfaceEffect", cause);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$initGlRenderer$5$androidx-camera-core-processing-DefaultSurfaceEffect  reason: not valid java name */
    public /* synthetic */ Object m183lambda$initGlRenderer$5$androidxcameracoreprocessingDefaultSurfaceEffect(ShaderProvider shaderProvider, CallbackToFutureAdapter.Completer completer) throws Exception {
        this.mGlExecutor.execute(new DefaultSurfaceEffect$$ExternalSyntheticLambda6(this, shaderProvider, completer));
        return "Init GlRenderer";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$initGlRenderer$4$androidx-camera-core-processing-DefaultSurfaceEffect  reason: not valid java name */
    public /* synthetic */ void m182lambda$initGlRenderer$4$androidxcameracoreprocessingDefaultSurfaceEffect(ShaderProvider shaderProvider, CallbackToFutureAdapter.Completer completer) {
        try {
            this.mGlRenderer.init(shaderProvider);
            completer.set(null);
        } catch (RuntimeException e) {
            completer.setException(e);
        }
    }
}
