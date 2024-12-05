package org.pzks.settings;

public abstract class GlobalSettings {
    public static Configuration CONFIGURATION;

    public static void configure(Configuration CONFIGURATION) {
        GlobalSettings.CONFIGURATION = CONFIGURATION;
    }

    public static abstract class Property {
        public static long NUMBER_OF_GENERATED_EXPRESSIONS_LIMIT = 1000;
    }
}
