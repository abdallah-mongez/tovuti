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
    private Set<WeakReference<Monitor>> monitorsRefs;

    private Tovuti(Context context) {
        contextRef = new WeakReference<>(context);
        monitorsRefs = new HashSet<>();
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
            monitorsRefs.add(new WeakReference<>(
                    new DefaultMonitorFactory().create(context, connectionType, listener))
            );
        }

        start();
        return tovuti;
    }

    public Tovuti monitor(Monitor.ConnectivityListener listener) {
        return monitor(-1, listener);
    }

    public void start() {
        for (WeakReference<Monitor> monitor : monitorsRefs) {
            if (monitor.get() != null) monitor.get().onStart();
        }

        if (monitorsRefs.size() > 0)
            Log.i(TAG, "started tovuti");
    }

    public void stop() {
        for (WeakReference<Monitor> monitorRef : monitorsRefs) {
            if (monitorRef.get() != null) monitorRef.get().onStop();
        }
    }
}
