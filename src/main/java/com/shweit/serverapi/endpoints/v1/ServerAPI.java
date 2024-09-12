package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.handlers.LogHandler;
import com.shweit.serverapi.listeners.ChatListener;
import com.shweit.serverapi.utils.Helper;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.*;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.shweit.serverapi.utils.Helper.formatSize;

public final class ServerAPI {
    private final ChatListener chatListener;
    private final LogHandler logHandler;

    public ServerAPI() {
        chatListener = new ChatListener();
        Bukkit.getPluginManager().registerEvents(chatListener, MinecraftServerAPI.getInstance());

        logHandler = new LogHandler();
        Logger.getLogger().addHandler(logHandler);
    }

    public NanoHTTPD.Response ping(final Map<String, String> ignoredParams) {
        return NanoHTTPD.newFixedLengthResponse("pong");
    }

    public NanoHTTPD.Response serverInfo(final Map<String, String> ignoredParams) {
        JSONObject serverInfo = new JSONObject();

        serverInfo.put("name", Bukkit.getServer().getName());
        serverInfo.put("motd", Bukkit.getServer().getMotd());
        serverInfo.put("version", Bukkit.getServer().getVersion());
        serverInfo.put("bukkitVersion", Bukkit.getServer().getBukkitVersion());
        serverInfo.put("ip", Bukkit.getServer().getIp());
        serverInfo.put("port", Bukkit.getServer().getPort());
        serverInfo.put("maxPlayers", Bukkit.getServer().getMaxPlayers());
        serverInfo.put("onlinePlayers", Bukkit.getServer().getOnlinePlayers().size());
        serverInfo.put("whitelisted", Bukkit.getServer().hasWhitelist());
        serverInfo.put("viewDistance", Bukkit.getServer().getViewDistance());
        serverInfo.put("simulationDistance", Bukkit.getServer().getSimulationDistance());
        serverInfo.put("spawnRadius", Bukkit.getServer().getSpawnRadius());
        serverInfo.put("worldType", Bukkit.getServer().getWorldType());

        JSONObject plugins = new JSONObject();
        for (Plugin key : Bukkit.getServer().getPluginManager().getPlugins()) {
            plugins.put(key.getName(), key.getDescription().getVersion());
        }
        serverInfo.put("plugins", plugins);

        JSONObject worlds = new JSONObject();
        for (World world : Bukkit.getServer().getWorlds()) {
            worlds.put(world.getName(), world.getEnvironment().name());
        }
        serverInfo.put("worlds", worlds);

        serverInfo.put("defaultGameMode", Bukkit.getServer().getDefaultGameMode().name());
        serverInfo.put("allowEnd", Bukkit.getServer().getAllowEnd());
        serverInfo.put("allowNether", Bukkit.getServer().getAllowNether());
        serverInfo.put("allowFlight", Bukkit.getServer().getAllowFlight());
        serverInfo.put("generateStructures", Bukkit.getServer().getGenerateStructures());
        serverInfo.put("hardcore", Bukkit.getServer().isHardcore());

        File serverIconFile = new File("server-icon.png");

        try {
            byte[] iconBytes = Files.readAllBytes(serverIconFile.toPath());
            String base64Icon = Base64.getEncoder().encodeToString(iconBytes);

            serverInfo.put("icon", "data:image/png;base64," + base64Icon);
        } catch (IOException e) {
            serverInfo.put("icon", "No server icon found");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", serverInfo.toString());
    }

    public NanoHTTPD.Response getServerHealth(final Map<String, String> ignoredParams) {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();

        JSONObject healthJson = new JSONObject();

        // System Memory Usage
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;

        healthJson.put("totalMemory", formatSize(totalMemory));
        healthJson.put("usedMemory", formatSize(usedMemory));
        healthJson.put("freeMemory", formatSize(freeMemory));

        // JVM Memory Usage
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        healthJson.put("jvmHeapMemoryUsed", formatSize(heapMemoryUsage.getUsed()));
        healthJson.put("jvmHeapMemoryMax", formatSize(heapMemoryUsage.getMax()));
        healthJson.put("jvmNonHeapMemoryUsed", formatSize(nonHeapMemoryUsage.getUsed()));
        healthJson.put("jvmNonHeapMemoryMax", formatSize(nonHeapMemoryUsage.getMax()));

        // CPU Information
        healthJson.put("availableProcessors", osBean.getAvailableProcessors());
        healthJson.put("systemLoadAverage", osBean.getSystemLoadAverage());

        // Disk Usage
        JSONObject diskUsageJson = new JSONObject();
        for (FileStore store : FileSystems.getDefault().getFileStores()) {
            try {
                long totalSpace = store.getTotalSpace();
                long usableSpace = store.getUsableSpace();
                long usedSpace = totalSpace - usableSpace;

                JSONObject diskJson = new JSONObject();
                diskJson.put("total", formatSize(totalSpace));
                diskJson.put("used", formatSize(usedSpace));
                diskJson.put("available", formatSize(usableSpace));

                diskUsageJson.put(store.name(), diskJson);
            } catch (IOException e) {
                Logger.error("Error getting disk usage for " + store.name());
            }
        }
        healthJson.put("diskUsage", diskUsageJson);

        // Thread Information
        healthJson.put("threadCount", threadBean.getThreadCount());
        healthJson.put("peakThreadCount", threadBean.getPeakThreadCount());
        healthJson.put("totalStartedThreadCount", threadBean.getTotalStartedThreadCount());
        healthJson.put("deadlockedThreads", threadBean.findDeadlockedThreads() != null);

        // Uptime
        healthJson.put("uptime", formatUpTime(runtimeBean.getUptime()));
        healthJson.put("tps", Helper.calculateTPS());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", healthJson.toString());
    }

    public NanoHTTPD.Response tps(final Map<String, String> ignoredParams) {
        JSONObject tpsJson = new JSONObject();
        tpsJson.put("tps", Helper.calculateTPS());
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", tpsJson.toString());
    }

    public NanoHTTPD.Response uptime(final Map<String, String> ignoredParams) {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        JSONObject uptimeJson = new JSONObject();
        uptimeJson.put("uptime", formatUpTime(runtimeBean.getUptime()));
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", uptimeJson.toString());
    }

    public NanoHTTPD.Response getServerProperties(final Map<String, String> ignoredParams) {
        Properties properties = new Properties();
        File propertiesFile = new File(Bukkit.getServer().getWorldContainer(), "server.properties");

        try (InputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
            JSONObject jsonResponse = new JSONObject();
            for (String key : properties.stringPropertyNames()) {
                jsonResponse.put(key, properties.getProperty(key));
            }
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonResponse.toString());
        } catch (IOException e) {
            Logger.error(e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"Could not load properties.\"}");
        }
    }

    public NanoHTTPD.Response updateServerProperties(final Map<String, String> params) {
        String key = params.get("key");
        String value = params.get("value");

        if (key == null || value == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\":\"Missing key or value.\"}");
        }

        Properties properties = new Properties();
        File propertiesFile = new File(Bukkit.getServer().getWorldContainer(), "server.properties");

        try (InputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            Logger.error(e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"Could not load properties.\"}");
        }

        try (FileOutputStream output = new FileOutputStream(propertiesFile)) {
            properties.setProperty(key, value);
            properties.store(output, null);
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{\"message\":\"Property updated successfully.\"}");
        } catch (IOException e) {
            Logger.error(e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{\"error\":\"Could not save properties.\"}");
        }
    }

    public NanoHTTPD.Response execCommand(final Map<String, String> params) {
        String command = params.get("command");

        if (command == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\":\"Invalid Command.\"}");
        }

        AtomicBoolean success = new AtomicBoolean(false);

        BukkitTask t1 = Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
            success.set(Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command));
        });

        while (Bukkit.getScheduler().isCurrentlyRunning(t1.getTaskId()) || Bukkit.getScheduler().isQueued(t1.getTaskId())) {
            try {
                Logger.debug("Waiting for command to finish...");
                final int sleepTime = 100;
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Logger.error(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }

        // TODO: Catch command output and return it in the response

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", success.get());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", jsonResponse.toString());
    }

    public NanoHTTPD.Response reload(final Map<String, String> ignoredParams) {
        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");

        final long delay = 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.reload();
            }
        }.runTaskLater(MinecraftServerAPI.getInstance(), delay);

        return response;
    }

    public NanoHTTPD.Response reboot(final Map<String, String> ignoredParams) {
        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");

        final long delay = 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.spigot().restart();
            }
        }.runTaskLater(MinecraftServerAPI.getInstance(), delay);

        return response;
    }

    public NanoHTTPD.Response shutdown(final Map<String, String> ignoredParams) {
        NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");

        final long delay = 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.shutdown();
            }
        }.runTaskLater(MinecraftServerAPI.getInstance(), delay);

        return response;
    }

    public NanoHTTPD.Response broadcast(final Map<String, String> params) {
        String message = params.get("message");

        if (message == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\":\"Invalid Message.\"}");
        }

        Bukkit.broadcastMessage(message);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response getChat(final Map<String, String> ignoredParams) {
        JSONObject chatJson = new JSONObject();
        chatJson.put("messages", chatListener.getMessages());
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", chatJson.toString());
    }

    public NanoHTTPD.Response getLog(final Map<String, String> ignoredParams) {
        JSONObject logJson = new JSONObject();
        logJson.put("log", logHandler.getLog());
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", logJson.toString());
    }

    private String formatUpTime(final long uptime) {
        long uptimeSeconds = uptime / 1000;
        long uptimeMinutes = uptimeSeconds / 60;
        long uptimeHours = uptimeMinutes / 60;
        long uptimeDays = uptimeHours / 24;

        return String.format("%d days, %d hours, %d minutes, %d seconds", uptimeDays, uptimeHours % 24, uptimeMinutes % 60, uptimeSeconds % 60);
    }
}
