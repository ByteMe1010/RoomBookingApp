package com.codehack.nngf.js;

public class SEnd {
    private final Context ct;

    public SEnd(Context c) {
        c.AddEnd();
        ct = c;
    }

    @Override
    public String toString() {
        return ct.getString();
    }
}
