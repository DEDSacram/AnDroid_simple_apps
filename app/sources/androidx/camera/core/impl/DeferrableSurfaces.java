package androidx.camera.core.impl;

import android.view.Surface;
import androidx.camera.core.impl.DeferrableSurface;
import androidx.camera.core.impl.utils.futures.FutureCallback;
import androidx.camera.core.impl.utils.futures.Futures;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class DeferrableSurfaces {
    private DeferrableSurfaces() {
    }

    public static ListenableFuture<List<Surface>> surfaceListWithTimeout(Collection<DeferrableSurface> deferrableSurfaces, boolean removeNullSurfaces, long timeout, Executor executor, ScheduledExecutorService scheduledExecutorService) {
        List<ListenableFuture<Surface>> listenableFutureSurfaces = new ArrayList<>();
        for (DeferrableSurface deferrableSurface : deferrableSurfaces) {
            listenableFutureSurfaces.add(Futures.nonCancellationPropagating(deferrableSurface.getSurface()));
        }
        return CallbackToFutureAdapter.getFuture(new DeferrableSurfaces$$ExternalSyntheticLambda0(listenableFutureSurfaces, scheduledExecutorService, executor, timeout, removeNullSurfaces));
    }

    static /* synthetic */ Object lambda$surfaceListWithTimeout$3(List listenableFutureSurfaces, ScheduledExecutorService scheduledExecutorService, Executor executor, long timeout, final boolean removeNullSurfaces, final CallbackToFutureAdapter.Completer completer) throws Exception {
        ListenableFuture<List<Surface>> listenableFuture = Futures.successfulAsList(listenableFutureSurfaces);
        final ScheduledFuture<?> scheduledFuture = scheduledExecutorService.schedule(new DeferrableSurfaces$$ExternalSyntheticLambda3(executor, listenableFuture, completer, timeout), timeout, TimeUnit.MILLISECONDS);
        completer.addCancellationListener(new DeferrableSurfaces$$ExternalSyntheticLambda1(listenableFuture), executor);
        Futures.addCallback(listenableFuture, new FutureCallback<List<Surface>>() {
            public void onSuccess(List<Surface> result) {
                List surfaces = new ArrayList(result);
                if (removeNullSurfaces) {
                    surfaces.removeAll(Collections.singleton((Object) null));
                }
                completer.set(surfaces);
                scheduledFuture.cancel(true);
            }

            public void onFailure(Throwable t) {
                completer.set(Collections.unmodifiableList(Collections.emptyList()));
                scheduledFuture.cancel(true);
            }
        }, executor);
        return "surfaceList";
    }

    static /* synthetic */ void lambda$surfaceListWithTimeout$0(ListenableFuture listenableFuture, CallbackToFutureAdapter.Completer completer, long timeout) {
        if (!listenableFuture.isDone()) {
            completer.setException(new TimeoutException("Cannot complete surfaceList within " + timeout));
            listenableFuture.cancel(true);
        }
    }

    public static boolean tryIncrementAll(List<DeferrableSurface> surfaceList) {
        try {
            incrementAll(surfaceList);
            return true;
        } catch (DeferrableSurface.SurfaceClosedException e) {
            return false;
        }
    }

    public static void incrementAll(List<DeferrableSurface> surfaceList) throws DeferrableSurface.SurfaceClosedException {
        if (!surfaceList.isEmpty()) {
            int i = 0;
            do {
                try {
                    surfaceList.get(i).incrementUseCount();
                    i++;
                } catch (DeferrableSurface.SurfaceClosedException e) {
                    for (int i2 = i - 1; i2 >= 0; i2--) {
                        surfaceList.get(i2).decrementUseCount();
                    }
                    throw e;
                }
            } while (i < surfaceList.size());
        }
    }

    public static void decrementAll(List<DeferrableSurface> surfaceList) {
        for (DeferrableSurface surface : surfaceList) {
            surface.decrementUseCount();
        }
    }
}
