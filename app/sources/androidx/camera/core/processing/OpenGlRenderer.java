package androidx.camera.core.processing;

import android.view.Surface;
import androidx.core.util.Preconditions;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class OpenGlRenderer {
    private final AtomicBoolean mInitialized = new AtomicBoolean(false);
    private final ThreadLocal<Long> mNativeContext = new ThreadLocal<>();

    private static native void closeContext(long j);

    private static native List<String> getShaderVariableNames();

    private static native int getTexName(long j);

    private static native long initContext(String str);

    private static native boolean renderTexture(long j, long j2, float[] fArr);

    private static native boolean setWindowSurface(long j, Surface surface);

    static {
        System.loadLibrary("camerax_core_opengl_renderer_jni");
    }

    public void init(ShaderProvider shaderProvider) {
        long nativeContext;
        checkInitializedOrThrow(false);
        if (shaderProvider == ShaderProvider.DEFAULT) {
            nativeContext = initContext((String) null);
        } else {
            List<String> varNames = getShaderVariableNames();
            Preconditions.checkState(varNames.size() == 2);
            try {
                nativeContext = initContext(shaderProvider.createFragmentShader(varNames.get(1), varNames.get(0)));
            } catch (Throwable t) {
                throw new IllegalArgumentException("Unable to create custom fragment shader", t);
            }
        }
        this.mNativeContext.set(Long.valueOf(nativeContext));
        this.mInitialized.set(true);
    }

    public void release() {
        if (this.mInitialized.getAndSet(false)) {
            closeContext(getNativeContextOrThrow());
            this.mNativeContext.remove();
        }
    }

    public void setOutputSurface(Surface surface) {
        checkInitializedOrThrow(true);
        setWindowSurface(getNativeContextOrThrow(), surface);
    }

    public int getTextureName() {
        checkInitializedOrThrow(true);
        return getTexName(getNativeContextOrThrow());
    }

    public void render(long timestampNs, float[] textureTransform) {
        checkInitializedOrThrow(true);
        renderTexture(getNativeContextOrThrow(), timestampNs, textureTransform);
    }

    private void checkInitializedOrThrow(boolean shouldInitialized) {
        String message;
        boolean result = shouldInitialized == this.mInitialized.get();
        if (shouldInitialized) {
            message = "OpenGlRenderer is not initialized";
        } else {
            message = "OpenGlRenderer is already initialized";
        }
        Preconditions.checkState(result, message);
    }

    private long getNativeContextOrThrow() {
        Long nativeContext = this.mNativeContext.get();
        Preconditions.checkState(nativeContext != null, "Method call must be called on the GL thread.");
        return nativeContext.longValue();
    }
}
