package com.shweit.serverapi.utils;

import com.shweit.serverapi.MinecraftServerAPI;
import org.bukkit.Bukkit;

import java.util.logging.Level;

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
        boolean debugMode = MinecraftServerAPI.config.getBoolean("debug", false);

        if (debugMode) {
            Bukkit.getLogger().log(Level.INFO, "[DEBUG] " + PREFIX + message);
        }
    }

    public static java.util.logging.Logger getLogger() {
        return Bukkit.getLogger();
    }
}
