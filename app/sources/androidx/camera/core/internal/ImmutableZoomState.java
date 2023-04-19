package androidx.camera.core.internal;

import androidx.camera.core.ZoomState;

public abstract class ImmutableZoomState implements ZoomState {
    public abstract float getLinearZoom();

    public abstract float getMaxZoomRatio();

    public abstract float getMinZoomRatio();

    public abstract float getZoomRatio();

    public static ZoomState create(float zoomRatio, float maxZoomRatio, float minZoomRatio, float linearZoom) {
        return new AutoValue_ImmutableZoomState(zoomRatio, maxZoomRatio, minZoomRatio, linearZoom);
    }

    public static ZoomState create(ZoomState zoomState) {
        return new AutoValue_ImmutableZoomState(zoomState.getZoomRatio(), zoomState.getMaxZoomRatio(), zoomState.getMinZoomRatio(), zoomState.getLinearZoom());
    }
}
