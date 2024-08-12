package com.shweit.serverapi.utils;

import org.bukkit.Bukkit;

public class Logger {
    public static void info(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.INFO, "[MinecraftServerAPI] " + message);
    }

    public static void warning(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.WARNING, "[MinecraftServerAPI] " + message);
    }

    public static void error(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "[MinecraftServerAPI] " + message);
    }

    public static void debug(String message) {
        Bukkit.getLogger().log(java.util.logging.Level.FINE, "[MinecraftServerAPI] " + message);
    }
}
