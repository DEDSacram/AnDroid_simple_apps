package androidx.camera.core.impl.utils.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

final class HandlerScheduledExecutorService extends AbstractExecutorService implements ScheduledExecutorService {
    private static ThreadLocal<ScheduledExecutorService> sThreadLocalInstance = new ThreadLocal<ScheduledExecutorService>() {
        public ScheduledExecutorService initialValue() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                return CameraXExecutors.mainThreadExecutor();
            }
            if (Looper.myLooper() != null) {
                return new HandlerScheduledExecutorService(new Handler(Looper.myLooper()));
            }
            return null;
        }
    };
    private final Handler mHandler;

    HandlerScheduledExecutorService(Handler handler) {
        this.mHandler = handler;
    }

    static ScheduledExecutorService currentThreadExecutor() {
        ScheduledExecutorService executor = sThreadLocalInstance.get();
        if (executor != null) {
            return executor;
        }
        Looper looper = Looper.myLooper();
        if (looper != null) {
            ScheduledExecutorService executor2 = new HandlerScheduledExecutorService(new Handler(looper));
            sThreadLocalInstance.set(executor2);
            return executor2;
        }
        throw new IllegalStateException("Current thread has no looper!");
    }

    public ScheduledFuture<?> schedule(final Runnable command, long delay, TimeUnit unit) {
        return schedule(new Callable<Void>() {
            public Void call() {
                command.run();
                return null;
            }
        }, delay, unit);
    }

    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        long runAtMillis = SystemClock.uptimeMillis() + TimeUnit.MILLISECONDS.convert(delay, unit);
        HandlerScheduledFuture<V> future = new HandlerScheduledFuture<>(this.mHandler, runAtMillis, callable);
        if (this.mHandler.postAtTime(future, runAtMillis)) {
            return future;
        }
        return Futures.immediateFailedScheduledFuture(createPostFailedException());
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        throw new UnsupportedOperationException(HandlerScheduledExecutorService.class.getSimpleName() + " does not yet support fixed-rate scheduling.");
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException(HandlerScheduledExecutorService.class.getSimpleName() + " does not yet support fixed-delay scheduling.");
    }

    public void shutdown() {
        throw new UnsupportedOperationException(HandlerScheduledExecutorService.class.getSimpleName() + " cannot be shut down. Use Looper.quitSafely().");
    }

    public List<Runnable> shutdownNow() {
        throw new UnsupportedOperationException(HandlerScheduledExecutorService.class.getSimpleName() + " cannot be shut down. Use Looper.quitSafely().");
    }

    public boolean isShutdown() {
        return false;
    }

    public boolean isTerminated() {
        return false;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException(HandlerScheduledExecutorService.class.getSimpleName() + " cannot be shut down. Use Looper.quitSafely().");
    }

    public void execute(Runnable command) {
        if (!this.mHandler.post(command)) {
            throw createPostFailedException();
        }
    }

    private RejectedExecutionException createPostFailedException() {
        return new RejectedExecutionException(this.mHandler + " is shutting down");
    }

    private static class HandlerScheduledFuture<V> implements RunnableScheduledFuture<V> {
        final AtomicReference<CallbackToFutureAdapter.Completer<V>> mCompleter = new AtomicReference<>((Object) null);
        private final ListenableFuture<V> mDelegate;
        private final long mRunAtMillis;
        private final Callable<V> mTask;

        HandlerScheduledFuture(final Handler handler, long runAtMillis, final Callable<V> task) {
            this.mRunAtMillis = runAtMillis;
            this.mTask = task;
            this.mDelegate = CallbackToFutureAdapter.getFuture(new CallbackToFutureAdapter.Resolver<V>() {
                public Object attachCompleter(CallbackToFutureAdapter.Completer<V> completer) throws RejectedExecutionException {
                    completer.addCancellationListener(new Runnable() {
                        public void run() {
                            if (HandlerScheduledFuture.this.mCompleter.getAndSet((Object) null) != null) {
                                handler.removeCallbacks(HandlerScheduledFuture.this);
                            }
                        }
                    }, CameraXExecutors.directExecutor());
                    HandlerScheduledFuture.this.mCompleter.set(completer);
                    return "HandlerScheduledFuture-" + task.toString();
                }
            });
        }

        public boolean isPeriodic() {
            return false;
        }

        public long getDelay(TimeUnit unit) {
            return unit.convert(this.mRunAtMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        public int compareTo(Delayed o) {
            return Long.compare(getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }

        public void run() {
            CallbackToFutureAdapter.Completer<V> completer = this.mCompleter.getAndSet((Object) null);
            if (completer != null) {
                try {
                    completer.set(this.mTask.call());
                } catch (Exception e) {
                    completer.setException(e);
                }
            }
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

        public V get() throws ExecutionException, InterruptedException {
            return this.mDelegate.get();
        }

        public V get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
            return this.mDelegate.get(timeout, unit);
        }
    }
}
