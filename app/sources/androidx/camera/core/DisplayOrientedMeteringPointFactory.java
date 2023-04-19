package androidx.camera.core;

import android.graphics.PointF;
import android.view.Display;
import androidx.camera.core.impl.CameraInfoInternal;

public final class DisplayOrientedMeteringPointFactory extends MeteringPointFactory {
    private final CameraInfo mCameraInfo;
    private final Display mDisplay;
    private final float mHeight;
    private final float mWidth;

    public DisplayOrientedMeteringPointFactory(Display display, CameraInfo cameraInfo, float width, float height) {
        this.mWidth = width;
        this.mHeight = height;
        this.mDisplay = display;
        this.mCameraInfo = cameraInfo;
    }

    private Integer getLensFacing() {
        CameraInfo cameraInfo = this.mCameraInfo;
        if (cameraInfo instanceof CameraInfoInternal) {
            return ((CameraInfoInternal) cameraInfo).getLensFacing();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public PointF convertPoint(float x, float y) {
        float width = this.mWidth;
        float height = this.mHeight;
        Integer lensFacing = getLensFacing();
        boolean compensateForMirroring = lensFacing != null && lensFacing.intValue() == 0;
        int relativeCameraOrientation = getRelativeCameraOrientation(compensateForMirroring);
        float outputX = x;
        float outputY = y;
        float outputWidth = width;
        float outputHeight = height;
        if (relativeCameraOrientation == 90 || relativeCameraOrientation == 270) {
            outputX = y;
            outputY = x;
            outputWidth = height;
            outputHeight = width;
        }
        switch (relativeCameraOrientation) {
            case 90:
                outputY = outputHeight - outputY;
                break;
            case 180:
                outputX = outputWidth - outputX;
                outputY = outputHeight - outputY;
                break;
            case 270:
                outputX = outputWidth - outputX;
                break;
        }
        if (compensateForMirroring) {
            outputX = outputWidth - outputX;
        }
        return new PointF(outputX / outputWidth, outputY / outputHeight);
    }

    private int getRelativeCameraOrientation(boolean compensateForMirroring) {
        try {
            int rotationDegrees = this.mCameraInfo.getSensorRotationDegrees(this.mDisplay.getRotation());
            if (compensateForMirroring) {
                return (360 - rotationDegrees) % 360;
            }
            return rotationDegrees;
        } catch (Exception e) {
            return 0;
        }
    }
}
