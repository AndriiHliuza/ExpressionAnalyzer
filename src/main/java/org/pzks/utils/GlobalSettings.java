package org.pzks.utils;

public class GlobalSettings {
    public static Configuration CONFIGURATION;

    public static void configure(Configuration CONFIGURATION) {
        GlobalSettings.CONFIGURATION = CONFIGURATION;
    }
}
