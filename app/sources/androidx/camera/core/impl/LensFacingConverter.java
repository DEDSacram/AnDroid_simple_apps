package androidx.camera.core.impl;

public class LensFacingConverter {
    private LensFacingConverter() {
    }

    public static Integer[] values() {
        return new Integer[]{0, 1};
    }

    public static int valueOf(String name) {
        if (name != null) {
            char c = 65535;
            switch (name.hashCode()) {
                case 2030823:
                    if (name.equals("BACK")) {
                        c = 1;
                        break;
                    }
                    break;
                case 67167753:
                    if (name.equals("FRONT")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    throw new IllegalArgumentException("Unknown len facing name " + name);
            }
        } else {
            throw new NullPointerException("name cannot be null");
        }
    }

    public static String nameOf(int lensFacing) {
        switch (lensFacing) {
            case 0:
                return "FRONT";
            case 1:
                return "BACK";
            default:
                throw new IllegalArgumentException("Unknown lens facing " + lensFacing);
        }
    }
}
