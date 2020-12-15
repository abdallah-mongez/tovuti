package com.androidstudy.tovuti;

import android.content.Context;
import android.util.Log;

import com.androidstudy.tovuti.internal.DefaultMonitorFactory;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Juma on 1/5/2018.
 */

public class Tovuti {

    private static final String TAG = Tovuti.class.getSimpleName();

    private static final Object LOCK = new Object();

    private static volatile Tovuti tovuti;
    private WeakReference<Context> contextRef;
    private Set<Monitor> monitors;

    private Tovuti(Context context) {
        contextRef = new WeakReference<>(context);
        monitors = new HashSet<>();
    }

    public static Tovuti from(Context context) {
        if (tovuti == null) {
            synchronized (LOCK) {
                if (tovuti == null) {
                    tovuti = new Tovuti(context);
                }
            }
        }
        return tovuti;
    }

    public Tovuti monitor(int connectionType, Monitor.ConnectivityListener listener) {
        Context context = contextRef.get();
        if (context != null) {
            monitors.add(new DefaultMonitorFactory().create(context, connectionType, listener));
        }

        start();
        return tovuti;
    }

    public Tovuti monitor(Monitor.ConnectivityListener listener) {
        return monitor(-1, listener);
    }

    public void start() {
        for (Monitor monitor : monitors) {
            monitor.onStart();
        }

        if (monitors.size() > 0)
            Log.i(TAG, "started tovuti");
    }

    public void stop() {
        for (Monitor monitor : monitors) {
            monitor.onStop();
        }
    }

    public void release() {
        monitors.clear();
        tovuti = null;
    }
}
