package androidx.camera.camera2.internal;

final class AutoValue_CameraDeviceId extends CameraDeviceId {
    private final String brand;
    private final String cameraId;
    private final String device;
    private final String model;

    AutoValue_CameraDeviceId(String brand2, String device2, String model2, String cameraId2) {
        if (brand2 != null) {
            this.brand = brand2;
            if (device2 != null) {
                this.device = device2;
                if (model2 != null) {
                    this.model = model2;
                    if (cameraId2 != null) {
                        this.cameraId = cameraId2;
                        return;
                    }
                    throw new NullPointerException("Null cameraId");
                }
                throw new NullPointerException("Null model");
            }
            throw new NullPointerException("Null device");
        }
        throw new NullPointerException("Null brand");
    }

    public String getBrand() {
        return this.brand;
    }

    public String getDevice() {
        return this.device;
    }

    public String getModel() {
        return this.model;
    }

    public String getCameraId() {
        return this.cameraId;
    }

    public String toString() {
        return "CameraDeviceId{brand=" + this.brand + ", device=" + this.device + ", model=" + this.model + ", cameraId=" + this.cameraId + "}";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof CameraDeviceId)) {
            return false;
        }
        CameraDeviceId that = (CameraDeviceId) o;
        if (!this.brand.equals(that.getBrand()) || !this.device.equals(that.getDevice()) || !this.model.equals(that.getModel()) || !this.cameraId.equals(that.getCameraId())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return (((((((1 * 1000003) ^ this.brand.hashCode()) * 1000003) ^ this.device.hashCode()) * 1000003) ^ this.model.hashCode()) * 1000003) ^ this.cameraId.hashCode();
    }
}
