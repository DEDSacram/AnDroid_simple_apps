package androidx.camera.core.impl;

import androidx.camera.core.impl.LiveDataObservable;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class LiveDataObservable$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ LiveDataObservable f$0;
    public final /* synthetic */ LiveDataObservable.LiveDataObserverAdapter f$1;

    public /* synthetic */ LiveDataObservable$$ExternalSyntheticLambda1(LiveDataObservable liveDataObservable, LiveDataObservable.LiveDataObserverAdapter liveDataObserverAdapter) {
        this.f$0 = liveDataObservable;
        this.f$1 = liveDataObserverAdapter;
    }

    public final void run() {
        this.f$0.m177lambda$removeObserver$3$androidxcameracoreimplLiveDataObservable(this.f$1);
    }
}
