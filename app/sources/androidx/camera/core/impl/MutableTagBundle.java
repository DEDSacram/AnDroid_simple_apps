package androidx.camera.core.impl;

import android.util.ArrayMap;
import java.util.Map;

public class MutableTagBundle extends TagBundle {
    private MutableTagBundle(Map<String, Object> source) {
        super(source);
    }

    public static MutableTagBundle create() {
        return new MutableTagBundle(new ArrayMap());
    }

    public static MutableTagBundle from(TagBundle otherTagBundle) {
        Map<String, Object> tags = new ArrayMap<>();
        for (String key : otherTagBundle.listKeys()) {
            tags.put(key, otherTagBundle.getTag(key));
        }
        return new MutableTagBundle(tags);
    }

    public void putTag(String key, Object value) {
        this.mTagMap.put(key, value);
    }

    public void addTagBundle(TagBundle bundle) {
        if (this.mTagMap != null && bundle.mTagMap != null) {
            this.mTagMap.putAll(bundle.mTagMap);
        }
    }
}
