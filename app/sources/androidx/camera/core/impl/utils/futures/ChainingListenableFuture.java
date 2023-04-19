package androidx.camera.core.impl.utils.futures;

import androidx.camera.core.impl.utils.executor.CameraXExecutors;
import androidx.core.util.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class ChainingListenableFuture<I, O> extends FutureChain<O> implements Runnable {
    private AsyncFunction<? super I, ? extends O> mFunction;
    private ListenableFuture<? extends I> mInputFuture;
    private final BlockingQueue<Boolean> mMayInterruptIfRunningChannel = new LinkedBlockingQueue(1);
    private final CountDownLatch mOutputCreated = new CountDownLatch(1);
    volatile ListenableFuture<? extends O> mOutputFuture;

    ChainingListenableFuture(AsyncFunction<? super I, ? extends O> function, ListenableFuture<? extends I> inputFuture) {
        this.mFunction = (AsyncFunction) Preconditions.checkNotNull(function);
        this.mInputFuture = (ListenableFuture) Preconditions.checkNotNull(inputFuture);
    }

    public O get() throws InterruptedException, ExecutionException {
        if (!isDone()) {
            ListenableFuture<? extends I> inputFuture = this.mInputFuture;
            if (inputFuture != null) {
                inputFuture.get();
            }
            this.mOutputCreated.await();
            ListenableFuture<? extends O> outputFuture = this.mOutputFuture;
            if (outputFuture != null) {
                outputFuture.get();
            }
        }
        return super.get();
    }

    public O get(long timeout, TimeUnit unit) throws TimeoutException, ExecutionException, InterruptedException {
        if (!isDone()) {
            if (unit != TimeUnit.NANOSECONDS) {
                timeout = TimeUnit.NANOSECONDS.convert(timeout, unit);
                unit = TimeUnit.NANOSECONDS;
            }
            ListenableFuture<? extends I> inputFuture = this.mInputFuture;
            if (inputFuture != null) {
                long start = System.nanoTime();
                inputFuture.get(timeout, unit);
                timeout -= Math.max(0, System.nanoTime() - start);
            }
            long start2 = System.nanoTime();
            if (this.mOutputCreated.await(timeout, unit)) {
                timeout -= Math.max(0, System.nanoTime() - start2);
                ListenableFuture<? extends O> outputFuture = this.mOutputFuture;
                if (outputFuture != null) {
                    outputFuture.get(timeout, unit);
                }
            } else {
                throw new TimeoutException();
            }
        }
        return super.get(timeout, unit);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        if (!super.cancel(mayInterruptIfRunning)) {
            return false;
        }
        putUninterruptibly(this.mMayInterruptIfRunningChannel, Boolean.valueOf(mayInterruptIfRunning));
        cancel(this.mInputFuture, mayInterruptIfRunning);
        cancel(this.mOutputFuture, mayInterruptIfRunning);
        return true;
    }

    private void cancel(Future<?> future, boolean mayInterruptIfRunning) {
        if (future != null) {
            future.cancel(mayInterruptIfRunning);
        }
    }

    public void run() {
        try {
            try {
                final ListenableFuture<? extends O> outputFuture = this.mFunction.apply(Futures.getUninterruptibly(this.mInputFuture));
                this.mOutputFuture = outputFuture;
                if (isCancelled()) {
                    outputFuture.cancel(((Boolean) takeUninterruptibly(this.mMayInterruptIfRunningChannel)).booleanValue());
                    this.mOutputFuture = null;
                    this.mFunction = null;
                    this.mInputFuture = null;
                    this.mOutputCreated.countDown();
                    return;
                }
                outputFuture.addListener(new Runnable() {
                    public void run() {
                        try {
                            ChainingListenableFuture.this.set(Futures.getUninterruptibly(outputFuture));
                        } catch (CancellationException e) {
                            ChainingListenableFuture.this.cancel(false);
                            ChainingListenableFuture.this.mOutputFuture = null;
                            return;
                        } catch (ExecutionException e2) {
                            ChainingListenableFuture.this.setException(e2.getCause());
                        } catch (Throwable th) {
                            ChainingListenableFuture.this.mOutputFuture = null;
                            throw th;
                        }
                        ChainingListenableFuture.this.mOutputFuture = null;
                    }
                }, CameraXExecutors.directExecutor());
                this.mFunction = null;
                this.mInputFuture = null;
                this.mOutputCreated.countDown();
            } catch (UndeclaredThrowableException e) {
                setException(e.getCause());
            } catch (Exception e2) {
                setException(e2);
            } catch (Error e3) {
                setException(e3);
            } catch (Throwable th) {
                this.mFunction = null;
                this.mInputFuture = null;
                this.mOutputCreated.countDown();
                throw th;
            }
        } catch (CancellationException e4) {
            cancel(false);
            this.mFunction = null;
            this.mInputFuture = null;
            this.mOutputCreated.countDown();
        } catch (ExecutionException e5) {
            setException(e5.getCause());
            this.mFunction = null;
            this.mInputFuture = null;
            this.mOutputCreated.countDown();
        }
    }

    private <E> E takeUninterruptibly(BlockingQueue<E> queue) {
        E take;
        boolean interrupted = false;
        while (true) {
            try {
                take = queue.take();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            } catch (Throwable th) {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                throw th;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return take;
    }

    private <E> void putUninterruptibly(BlockingQueue<E> queue, E element) {
        boolean interrupted = false;
        while (true) {
            try {
                queue.put(element);
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            } catch (Throwable th) {
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
                throw th;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
