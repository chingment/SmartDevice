package com.lumos.smartdevice.utils.tinytask;

import android.util.Log;

import com.lumos.smartdevice.BuildConfig;



public abstract class Task<T> extends SimpleTask {
    public Task() {
        super();
    }

    public Task(Priority priority) {
        super(priority);
    }

    public abstract T doInBackground();

    public abstract void onSuccess(T t);

    public abstract void onFail(Throwable throwable);

    @Override
    public void run() {
        if (BuildConfig.DEBUG) {
            Log.i("TinyTask", "[Task] compare: priority ="+priority+"  taskName = " + Thread.currentThread().getName());
        }
        try {
            final T t = doInBackground();
            TinyTaskExecutor.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(t);
                }
            });
        } catch (final Throwable throwable) {
            TinyTaskExecutor.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    onFail(throwable);
                }
            });
        }
    }
}
