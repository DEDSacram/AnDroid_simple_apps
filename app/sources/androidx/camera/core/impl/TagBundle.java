package androidx.camera.core.impl;

import android.util.ArrayMap;
import android.util.Pair;
import java.util.Map;
import java.util.Set;

public class TagBundle {
    private static final TagBundle EMPTY_TAGBUNDLE = new TagBundle(new ArrayMap());
    protected final Map<String, Object> mTagMap;

    protected TagBundle(Map<String, Object> tagMap) {
        this.mTagMap = tagMap;
    }

    public static TagBundle emptyBundle() {
        return EMPTY_TAGBUNDLE;
    }

    public static TagBundle create(Pair<String, Object> source) {
        Map<String, Object> map = new ArrayMap<>();
        map.put((String) source.first, source.second);
        return new TagBundle(map);
    }

    public static TagBundle from(TagBundle otherTagBundle) {
        Map<String, Object> tags = new ArrayMap<>();
        for (String key : otherTagBundle.listKeys()) {
            tags.put(key, otherTagBundle.getTag(key));
        }
        return new TagBundle(tags);
    }

    public Object getTag(String key) {
        return this.mTagMap.get(key);
    }

    public Set<String> listKeys() {
        return this.mTagMap.keySet();
    }
}
