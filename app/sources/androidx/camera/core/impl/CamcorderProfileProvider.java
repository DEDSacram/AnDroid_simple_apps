package androidx.camera.core.impl;

public interface CamcorderProfileProvider {
    public static final CamcorderProfileProvider EMPTY = new CamcorderProfileProvider() {
        public boolean hasProfile(int quality) {
            return false;
        }

        public CamcorderProfileProxy get(int quality) {
            return null;
        }
    };

    CamcorderProfileProxy get(int i);

    boolean hasProfile(int i);
}
