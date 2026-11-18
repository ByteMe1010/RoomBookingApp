package com.codehack.nngf.Core.builder;

import com.codehack.nngf.Core.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a Event holder until execution. mainly used on running OnBridgeReady events. but can be used for any other task independently in codebase.
 * behavior on failed events, skips failed events and finish the whole set. at the end failed events will be logged if not empty.
 */
public class EventGroup {
    private final List<Runnable> eventList = new ArrayList<>();

    public void thenRun(Runnable r) {
        synchronized (eventList) {
            eventList.add(r);
        }
    }

    public void complete() {
        List<Runnable> toRun;
        synchronized (eventList) {
            toRun = new ArrayList<>(eventList);
        }
        List<Exception> failures = new ArrayList<>();
        for (Runnable r : toRun) {
            try {
                r.run();
            } catch (Exception e) {
                Logger.Error("EventHolder callback failed: " + e.getMessage(), 2);
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            RuntimeException ex = new RuntimeException("EventGroup execution failed with " + failures.size() + " error(s)");
            for (Exception e : failures) {
                ex.addSuppressed(e);
            }
            throw ex;
        }
    }
}
