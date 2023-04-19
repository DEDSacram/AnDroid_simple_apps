package androidx.camera.core.impl;

import androidx.camera.core.impl.Observable;
import androidx.camera.core.impl.utils.futures.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public final class ConstantObservable<T> implements Observable<T> {
    private static final ConstantObservable<Object> NULL_OBSERVABLE = new ConstantObservable<>((Object) null);
    private static final String TAG = "ConstantObservable";
    private final ListenableFuture<T> mValueFuture;

    public static <U> Observable<U> withValue(U value) {
        if (value == null) {
            return NULL_OBSERVABLE;
        }
        return new ConstantObservable(value);
    }

    private ConstantObservable(T value) {
        this.mValueFuture = Futures.immediateFuture(value);
    }

    public ListenableFuture<T> fetchData() {
        return this.mValueFuture;
    }

    public void addObserver(Executor executor, Observable.Observer<? super T> observer) {
        this.mValueFuture.addListener(new ConstantObservable$$ExternalSyntheticLambda0(this, observer), executor);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$addObserver$0$androidx-camera-core-impl-ConstantObservable  reason: not valid java name */
    public /* synthetic */ void m171lambda$addObserver$0$androidxcameracoreimplConstantObservable(Observable.Observer observer) {
        try {
            observer.onNewData(this.mValueFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            observer.onError(e);
        }
    }

    public void removeObserver(Observable.Observer<? super T> observer) {
    }
}
