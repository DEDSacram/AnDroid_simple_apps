package androidx.camera.core.impl;

import java.util.ArrayList;
import java.util.List;

public class Quirks {
    private final List<Quirk> mQuirks;

    public Quirks(List<Quirk> quirks) {
        this.mQuirks = new ArrayList(quirks);
    }

    public <T extends Quirk> T get(Class<T> quirkClass) {
        for (Quirk quirk : this.mQuirks) {
            if (quirk.getClass() == quirkClass) {
                return quirk;
            }
        }
        return null;
    }

    public boolean contains(Class<? extends Quirk> quirkClass) {
        for (Quirk quirk : this.mQuirks) {
            if (quirkClass.isAssignableFrom(quirk.getClass())) {
                return true;
            }
        }
        return false;
    }
}
