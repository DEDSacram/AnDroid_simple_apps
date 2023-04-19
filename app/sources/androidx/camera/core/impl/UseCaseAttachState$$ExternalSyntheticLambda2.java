package androidx.camera.core.impl;

import androidx.camera.core.impl.UseCaseAttachState;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class UseCaseAttachState$$ExternalSyntheticLambda2 implements UseCaseAttachState.AttachStateFilter {
    public static final /* synthetic */ UseCaseAttachState$$ExternalSyntheticLambda2 INSTANCE = new UseCaseAttachState$$ExternalSyntheticLambda2();

    private /* synthetic */ UseCaseAttachState$$ExternalSyntheticLambda2() {
    }

    public final boolean filter(UseCaseAttachState.UseCaseAttachInfo useCaseAttachInfo) {
        return useCaseAttachInfo.getAttached();
    }
}
