package androidx.camera.core.impl;

import android.os.SystemClock;
import androidx.camera.core.impl.Observable;
import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public final class LiveDataObservable<T> implements Observable<T> {
    final MutableLiveData<Result<T>> mLiveData = new MutableLiveData<>();
    private final Map<Observable.Observer<? super T>, LiveDataObserverAdapter<T>> mObservers = new HashMap();

    public void postValue(T value) {
        this.mLiveData.postValue(Result.fromValue(value));
    }

    public void postError(Throwable error) {
        this.mLiveData.postValue(Result.fromError(error));
    }

    public LiveData<Result<T>> getLiveData() {
        return this.mLiveData;
    }

    public ListenableFuture<T> fetchData() {
        return CallbackToFutureAdapter.getFuture(new LiveDataObservable$$ExternalSyntheticLambda0(this));
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$fetchData$1$androidx-camera-core-impl-LiveDataObservable  reason: not valid java name */
    public /* synthetic */ Object m176lambda$fetchData$1$androidxcameracoreimplLiveDataObservable(CallbackToFutureAdapter.Completer completer) throws Exception {
        CameraXExecutors.mainThreadExecutor().execute(new LiveDataObservable$$ExternalSyntheticLambda3(this, completer));
        return this + " [fetch@" + SystemClock.uptimeMillis() + "]";
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$fetchData$0$androidx-camera-core-impl-LiveDataObservable  reason: not valid java name */
    public /* synthetic */ void m175lambda$fetchData$0$androidxcameracoreimplLiveDataObservable(CallbackToFutureAdapter.Completer completer) {
        Result<T> result = this.mLiveData.getValue();
        if (result == null) {
            completer.setException(new IllegalStateException("Observable has not yet been initialized with a value."));
        } else if (result.completedSuccessfully()) {
            completer.set(result.getValue());
        } else {
            Preconditions.checkNotNull(result.getError());
            completer.setException(result.getError());
        }
    }

    public void addObserver(Executor executor, Observable.Observer<? super T> observer) {
        synchronized (this.mObservers) {
            LiveDataObserverAdapter<T> oldAdapter = this.mObservers.get(observer);
            if (oldAdapter != null) {
                oldAdapter.disable();
            }
            LiveDataObserverAdapter<T> newAdapter = new LiveDataObserverAdapter<>(executor, observer);
            this.mObservers.put(observer, newAdapter);
            CameraXExecutors.mainThreadExecutor().execute(new LiveDataObservable$$ExternalSyntheticLambda2(this, oldAdapter, newAdapter));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$addObserver$2$androidx-camera-core-impl-LiveDataObservable  reason: not valid java name */
    public /* synthetic */ void m174lambda$addObserver$2$androidxcameracoreimplLiveDataObservable(LiveDataObserverAdapter oldAdapter, LiveDataObserverAdapter newAdapter) {
        if (oldAdapter != null) {
            this.mLiveData.removeObserver(oldAdapter);
        }
        this.mLiveData.observeForever(newAdapter);
    }

    public void removeObserver(Observable.Observer<? super T> observer) {
        synchronized (this.mObservers) {
            LiveDataObserverAdapter<T> adapter = this.mObservers.remove(observer);
            if (adapter != null) {
                adapter.disable();
                CameraXExecutors.mainThreadExecutor().execute(new LiveDataObservable$$ExternalSyntheticLambda1(this, adapter));
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$removeObserver$3$androidx-camera-core-impl-LiveDataObservable  reason: not valid java name */
    public /* synthetic */ void m177lambda$removeObserver$3$androidxcameracoreimplLiveDataObservable(LiveDataObserverAdapter adapter) {
        this.mLiveData.removeObserver(adapter);
    }

    public static final class Result<T> {
        private final Throwable mError;
        private final T mValue;

        private Result(T value, Throwable error) {
            this.mValue = value;
            this.mError = error;
        }

        static <T> Result<T> fromValue(T value) {
            return new Result<>(value, (Throwable) null);
        }

        static <T> Result<T> fromError(Throwable error) {
            return new Result<>((Object) null, (Throwable) Preconditions.checkNotNull(error));
        }

        public boolean completedSuccessfully() {
            return this.mError == null;
        }

        public T getValue() {
            if (completedSuccessfully()) {
                return this.mValue;
            }
            throw new IllegalStateException("Result contains an error. Does not contain a value.");
        }

        public Throwable getError() {
            return this.mError;
        }

        public String toString() {
            String str;
            StringBuilder append = new StringBuilder().append("[Result: <");
            if (completedSuccessfully()) {
                str = "Value: " + this.mValue;
            } else {
                str = "Error: " + this.mError;
            }
            return append.append(str).append(">]").toString();
        }
    }

    private static final class LiveDataObserverAdapter<T> implements Observer<Result<T>> {
        final AtomicBoolean mActive = new AtomicBoolean(true);
        final Executor mExecutor;
        final Observable.Observer<? super T> mObserver;

        LiveDataObserverAdapter(Executor executor, Observable.Observer<? super T> observer) {
            this.mExecutor = executor;
            this.mObserver = observer;
        }

        /* access modifiers changed from: package-private */
        public void disable() {
            this.mActive.set(false);
        }

        public void onChanged(Result<T> result) {
            this.mExecutor.execute(new LiveDataObservable$LiveDataObserverAdapter$$ExternalSyntheticLambda0(this, result));
        }

        /* access modifiers changed from: package-private */
        /* renamed from: lambda$onChanged$0$androidx-camera-core-impl-LiveDataObservable$LiveDataObserverAdapter  reason: not valid java name */
        public /* synthetic */ void m178lambda$onChanged$0$androidxcameracoreimplLiveDataObservable$LiveDataObserverAdapter(Result result) {
            if (this.mActive.get()) {
                if (result.completedSuccessfully()) {
                    this.mObserver.onNewData(result.getValue());
                    return;
                }
                Preconditions.checkNotNull(result.getError());
                this.mObserver.onError(result.getError());
            }
        }
    }
}
