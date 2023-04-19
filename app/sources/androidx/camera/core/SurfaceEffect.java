package androidx.camera.core;

public interface SurfaceEffect extends CameraEffect {
    public static final int PREVIEW = 1;
    public static final int VIDEO_CAPTURE = 2;

    void onInputSurface(SurfaceRequest surfaceRequest);

    void onOutputSurface(SurfaceOutput surfaceOutput);
}
