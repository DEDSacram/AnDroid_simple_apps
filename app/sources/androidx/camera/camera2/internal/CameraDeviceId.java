package androidx.camera.camera2.internal;

abstract class CameraDeviceId {
    public abstract String getBrand();

    public abstract String getCameraId();

    public abstract String getDevice();

    public abstract String getModel();

    CameraDeviceId() {
    }

    public static CameraDeviceId create(String brand, String device, String model, String cameraId) {
        return new AutoValue_CameraDeviceId(brand.toLowerCase(), device.toLowerCase(), model.toLowerCase(), cameraId.toLowerCase());
    }
}
