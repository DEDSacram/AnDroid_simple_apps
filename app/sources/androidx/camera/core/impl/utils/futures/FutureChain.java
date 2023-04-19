package androidx.camera.core.impl.utils.futures;

import androidx.arch.core.util.Function;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureChain<V> implements ListenableFuture<V> {
    CallbackToFutureAdapter.Completer<V> mCompleter;
    private final ListenableFuture<V> mDelegate;

    public static <V> FutureChain<V> from(ListenableFuture<V> future) {
        return future instanceof FutureChain ? (FutureChain) future : new FutureChain<>(future);
    }

    public final <T> FutureChain<T> transformAsync(AsyncFunction<? super V, T> function, Executor executor) {
        return (FutureChain) Futures.transformAsync(this, function, executor);
    }

    public final <T> FutureChain<T> transform(Function<? super V, T> function, Executor executor) {
        return (FutureChain) Futures.transform(this, function, executor);
    }

    public final void addCallback(FutureCallback<? super V> callback, Executor executor) {
        Futures.addCallback(this, callback, executor);
    }

    FutureChain(ListenableFuture<V> delegate) {
        this.mDelegate = (ListenableFuture) Preconditions.checkNotNull(delegate);
    }

    FutureChain() {
        this.mDelegate = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<V>() {
            public Object attachCompleter(CallbackToFutureAdapter.Completer<V> completer) {
                Preconditions.checkState(FutureChain.this.mCompleter == null, "The result can only set once!");
                FutureChain.this.mCompleter = completer;
                return "FutureChain[" + FutureChain.this + "]";
            }
        });
    }

    public void addListener(Runnable listener, Executor executor) {
        this.mDelegate.addListener(listener, executor);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.mDelegate.cancel(mayInterruptIfRunning);
    }

    public boolean isCancelled() {
        return this.mDelegate.isCancelled();
    }

    public boolean isDone() {
        return this.mDelegate.isDone();
    }

    public V get() throws InterruptedException, ExecutionException {
        return this.mDelegate.get();
    }

    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.mDelegate.get(timeout, unit);
    }

    /* access modifiers changed from: package-private */
    public boolean set(V value) {
        CallbackToFutureAdapter.Completer<V> completer = this.mCompleter;
        if (completer != null) {
            return completer.set(value);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean setException(Throwable throwable) {
        CallbackToFutureAdapter.Completer<V> completer = this.mCompleter;
        if (completer != null) {
            return completer.setException(throwable);
        }
        return false;
    }
}
