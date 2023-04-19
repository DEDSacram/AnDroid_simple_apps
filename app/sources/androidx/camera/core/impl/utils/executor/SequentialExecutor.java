package androidx.camera.core.impl.utils.executor;

import androidx.core.util.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;

final class SequentialExecutor implements Executor {
    private static final String TAG = "SequentialExecutor";
    private final Executor mExecutor;
    final Deque<Runnable> mQueue = new ArrayDeque();
    private final QueueWorker mWorker = new QueueWorker();
    long mWorkerRunCount = 0;
    WorkerRunningState mWorkerRunningState = WorkerRunningState.IDLE;

    enum WorkerRunningState {
        IDLE,
        QUEUING,
        QUEUED,
        RUNNING
    }

    SequentialExecutor(Executor executor) {
        this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0025, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r9.mExecutor.execute(r9.mWorker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0033, code lost:
        if (r9.mWorkerRunningState == androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUING) goto L_0x0036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0036, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0038, code lost:
        if (r0 == false) goto L_0x003b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
        r6 = r9.mQueue;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x003d, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0042, code lost:
        if (r9.mWorkerRunCount != r1) goto L_0x004e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0048, code lost:
        if (r9.mWorkerRunningState != androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUING) goto L_0x004e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004a, code lost:
        r9.mWorkerRunningState = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUED;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004e, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x004f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0053, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0058, code lost:
        monitor-enter(r9.mQueue);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x005d, code lost:
        if (r9.mWorkerRunningState == androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.IDLE) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x006e, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0071, code lost:
        if ((r5 instanceof java.util.concurrent.RejectedExecutionException) == false) goto L_0x0078;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0076, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0078, code lost:
        throw r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void execute(final java.lang.Runnable r10) {
        /*
            r9 = this;
            androidx.core.util.Preconditions.checkNotNull(r10)
            java.util.Deque<java.lang.Runnable> r0 = r9.mQueue
            monitor-enter(r0)
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r1 = r9.mWorkerRunningState     // Catch:{ all -> 0x0083 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r2 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.RUNNING     // Catch:{ all -> 0x0083 }
            if (r1 == r2) goto L_0x007c
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r1 = r9.mWorkerRunningState     // Catch:{ all -> 0x0083 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r2 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUED     // Catch:{ all -> 0x0083 }
            if (r1 != r2) goto L_0x0014
            goto L_0x007c
        L_0x0014:
            long r1 = r9.mWorkerRunCount     // Catch:{ all -> 0x0083 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$1 r3 = new androidx.camera.core.impl.utils.executor.SequentialExecutor$1     // Catch:{ all -> 0x0083 }
            r3.<init>(r10)     // Catch:{ all -> 0x0083 }
            java.util.Deque<java.lang.Runnable> r4 = r9.mQueue     // Catch:{ all -> 0x0083 }
            r4.add(r3)     // Catch:{ all -> 0x0083 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r4 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUING     // Catch:{ all -> 0x0083 }
            r9.mWorkerRunningState = r4     // Catch:{ all -> 0x0083 }
            monitor-exit(r0)     // Catch:{ all -> 0x0083 }
            r0 = 1
            r4 = 0
            java.util.concurrent.Executor r5 = r9.mExecutor     // Catch:{ RuntimeException -> 0x0055, Error -> 0x0053 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$QueueWorker r6 = r9.mWorker     // Catch:{ RuntimeException -> 0x0055, Error -> 0x0053 }
            r5.execute(r6)     // Catch:{ RuntimeException -> 0x0055, Error -> 0x0053 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r5 = r9.mWorkerRunningState
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r6 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUING
            if (r5 == r6) goto L_0x0036
            goto L_0x0037
        L_0x0036:
            r0 = r4
        L_0x0037:
            r5 = r0
            if (r5 == 0) goto L_0x003b
            return
        L_0x003b:
            java.util.Deque<java.lang.Runnable> r6 = r9.mQueue
            monitor-enter(r6)
            long r7 = r9.mWorkerRunCount     // Catch:{ all -> 0x0050 }
            int r0 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1))
            if (r0 != 0) goto L_0x004e
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r0 = r9.mWorkerRunningState     // Catch:{ all -> 0x0050 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r4 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUING     // Catch:{ all -> 0x0050 }
            if (r0 != r4) goto L_0x004e
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r0 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUED     // Catch:{ all -> 0x0050 }
            r9.mWorkerRunningState = r0     // Catch:{ all -> 0x0050 }
        L_0x004e:
            monitor-exit(r6)     // Catch:{ all -> 0x0050 }
            return
        L_0x0050:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0050 }
            throw r0
        L_0x0053:
            r5 = move-exception
            goto L_0x0056
        L_0x0055:
            r5 = move-exception
        L_0x0056:
            java.util.Deque<java.lang.Runnable> r6 = r9.mQueue
            monitor-enter(r6)
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r7 = r9.mWorkerRunningState     // Catch:{ all -> 0x0079 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r8 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.IDLE     // Catch:{ all -> 0x0079 }
            if (r7 == r8) goto L_0x0065
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r7 = r9.mWorkerRunningState     // Catch:{ all -> 0x0079 }
            androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r8 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.QUEUING     // Catch:{ all -> 0x0079 }
            if (r7 != r8) goto L_0x006e
        L_0x0065:
            java.util.Deque<java.lang.Runnable> r7 = r9.mQueue     // Catch:{ all -> 0x0079 }
            boolean r7 = r7.removeLastOccurrence(r3)     // Catch:{ all -> 0x0079 }
            if (r7 == 0) goto L_0x006e
            goto L_0x006f
        L_0x006e:
            r0 = r4
        L_0x006f:
            boolean r4 = r5 instanceof java.util.concurrent.RejectedExecutionException     // Catch:{ all -> 0x0079 }
            if (r4 == 0) goto L_0x0077
            if (r0 != 0) goto L_0x0077
            monitor-exit(r6)     // Catch:{ all -> 0x0079 }
            return
        L_0x0077:
            throw r5     // Catch:{ all -> 0x0079 }
        L_0x0079:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0079 }
            throw r0
        L_0x007c:
            java.util.Deque<java.lang.Runnable> r1 = r9.mQueue     // Catch:{ all -> 0x0083 }
            r1.add(r10)     // Catch:{ all -> 0x0083 }
            monitor-exit(r0)     // Catch:{ all -> 0x0083 }
            return
        L_0x0083:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0083 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.impl.utils.executor.SequentialExecutor.execute(java.lang.Runnable):void");
    }

    final class QueueWorker implements Runnable {
        QueueWorker() {
        }

        public void run() {
            try {
                workOnQueue();
            } catch (Error e) {
                synchronized (SequentialExecutor.this.mQueue) {
                    SequentialExecutor.this.mWorkerRunningState = WorkerRunningState.IDLE;
                    throw e;
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
            java.lang.Thread.currentThread().interrupt();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x003f, code lost:
            if (r0 == false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x0041, code lost:
            java.lang.Thread.currentThread().interrupt();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x004e, code lost:
            r0 = r0 | java.lang.Thread.interrupted();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
            r3.run();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
            if (r0 == false) goto L_?;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void workOnQueue() {
            /*
                r8 = this;
                r0 = 0
                r1 = 0
            L_0x0002:
                androidx.camera.core.impl.utils.executor.SequentialExecutor r2 = androidx.camera.core.impl.utils.executor.SequentialExecutor.this     // Catch:{ all -> 0x0070 }
                java.util.Deque<java.lang.Runnable> r2 = r2.mQueue     // Catch:{ all -> 0x0070 }
                monitor-enter(r2)     // Catch:{ all -> 0x0070 }
                if (r1 != 0) goto L_0x002c
                androidx.camera.core.impl.utils.executor.SequentialExecutor r3 = androidx.camera.core.impl.utils.executor.SequentialExecutor.this     // Catch:{ all -> 0x006d }
                androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r3 = r3.mWorkerRunningState     // Catch:{ all -> 0x006d }
                androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r4 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.RUNNING     // Catch:{ all -> 0x006d }
                if (r3 != r4) goto L_0x001c
                monitor-exit(r2)     // Catch:{ all -> 0x006d }
                if (r0 == 0) goto L_0x001b
                java.lang.Thread r2 = java.lang.Thread.currentThread()
                r2.interrupt()
            L_0x001b:
                return
            L_0x001c:
                androidx.camera.core.impl.utils.executor.SequentialExecutor r3 = androidx.camera.core.impl.utils.executor.SequentialExecutor.this     // Catch:{ all -> 0x006d }
                long r4 = r3.mWorkerRunCount     // Catch:{ all -> 0x006d }
                r6 = 1
                long r4 = r4 + r6
                r3.mWorkerRunCount = r4     // Catch:{ all -> 0x006d }
                androidx.camera.core.impl.utils.executor.SequentialExecutor r3 = androidx.camera.core.impl.utils.executor.SequentialExecutor.this     // Catch:{ all -> 0x006d }
                androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r4 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.RUNNING     // Catch:{ all -> 0x006d }
                r3.mWorkerRunningState = r4     // Catch:{ all -> 0x006d }
                r1 = 1
            L_0x002c:
                androidx.camera.core.impl.utils.executor.SequentialExecutor r3 = androidx.camera.core.impl.utils.executor.SequentialExecutor.this     // Catch:{ all -> 0x006d }
                java.util.Deque<java.lang.Runnable> r3 = r3.mQueue     // Catch:{ all -> 0x006d }
                java.lang.Object r3 = r3.poll()     // Catch:{ all -> 0x006d }
                java.lang.Runnable r3 = (java.lang.Runnable) r3     // Catch:{ all -> 0x006d }
                if (r3 != 0) goto L_0x0049
                androidx.camera.core.impl.utils.executor.SequentialExecutor r4 = androidx.camera.core.impl.utils.executor.SequentialExecutor.this     // Catch:{ all -> 0x006d }
                androidx.camera.core.impl.utils.executor.SequentialExecutor$WorkerRunningState r5 = androidx.camera.core.impl.utils.executor.SequentialExecutor.WorkerRunningState.IDLE     // Catch:{ all -> 0x006d }
                r4.mWorkerRunningState = r5     // Catch:{ all -> 0x006d }
                monitor-exit(r2)     // Catch:{ all -> 0x006d }
                if (r0 == 0) goto L_0x0048
                java.lang.Thread r2 = java.lang.Thread.currentThread()
                r2.interrupt()
            L_0x0048:
                return
            L_0x0049:
                monitor-exit(r2)     // Catch:{ all -> 0x006d }
                boolean r2 = java.lang.Thread.interrupted()     // Catch:{ all -> 0x0070 }
                r0 = r0 | r2
                r3.run()     // Catch:{ RuntimeException -> 0x0053 }
                goto L_0x006c
            L_0x0053:
                r2 = move-exception
                java.lang.String r4 = "SequentialExecutor"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0070 }
                r5.<init>()     // Catch:{ all -> 0x0070 }
                java.lang.String r6 = "Exception while executing runnable "
                java.lang.StringBuilder r5 = r5.append(r6)     // Catch:{ all -> 0x0070 }
                java.lang.StringBuilder r5 = r5.append(r3)     // Catch:{ all -> 0x0070 }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0070 }
                androidx.camera.core.Logger.e(r4, r5, r2)     // Catch:{ all -> 0x0070 }
            L_0x006c:
                goto L_0x0002
            L_0x006d:
                r3 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x006d }
                throw r3     // Catch:{ all -> 0x0070 }
            L_0x0070:
                r2 = move-exception
                if (r0 == 0) goto L_0x007a
                java.lang.Thread r3 = java.lang.Thread.currentThread()
                r3.interrupt()
            L_0x007a:
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.camera.core.impl.utils.executor.SequentialExecutor.QueueWorker.workOnQueue():void");
        }
    }
}
