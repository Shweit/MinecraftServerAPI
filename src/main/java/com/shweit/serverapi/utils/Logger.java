package com.shweit.serverapi.utils;

import org.bukkit.Bukkit;

public class Logger {
    private static final String PREFIX = "[MinecraftServerAPI] ";

    public static void info(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.INFO, PREFIX + message);
    }

    public static void warning(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.WARNING, PREFIX + message);
    }

    public static void error(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.SEVERE, PREFIX + message);
    }

    public static void debug(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.FINE, PREFIX + message);
    }
}
