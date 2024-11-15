package org.pzks.utils;

public class GlobalSettings {
    public static Configuration CONFIGURATION;
    public static long NUMBER_OF_GENERATED_EXCEPTIONS_LIMIT = 1000;

    public static void configure(Configuration CONFIGURATION) {
        GlobalSettings.CONFIGURATION = CONFIGURATION;
    }
}
