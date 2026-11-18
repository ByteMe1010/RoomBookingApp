package com.codehack.nngf.Core;

public class Logger {
    private static boolean active = false;
    private static int lowLevel = 0;

    public static void setLogging(boolean act, int level) {
        active = act;
        lowLevel = level;
    }

    public static void Log(String msg, int level) {
        if (active && lowLevel <= level)
            System.out.println("[  Log] " + msg);
    }

    public static void Error(String msg, int level) {
        if (active && lowLevel <= level)
            System.err.println("[Error] " + msg);
    }
}
