package org.alexdev.kepler.util.schedule;

import java.util.concurrent.Future;

public abstract class FutureRunnable implements Runnable {
    private Future<?> future;

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public void cancelFuture() {
        if (this.future != null) {
            this.future.cancel(true);
            this.future = null;
        }
    }
}