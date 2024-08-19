package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Map;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.shweit.serverapi.utils.Helper.deleteDirectory;

public final class PluginAPI {
    public NanoHTTPD.Response getPlugins(final Map<String, String> ignoredParams) {
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

        JSONObject response = new JSONObject();
        for (Plugin plugin : plugins) {
            response.put(plugin.getName(), plugin.getDescription().getVersion());
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    public NanoHTTPD.Response getPlugin(final Map<String, String> params) {
        String pluginName = params.get("name");
        if (pluginName == null || pluginName.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        JSONObject response = new JSONObject();
        response.put("name", plugin.getName());
        response.put("version", plugin.getDescription().getVersion());
        response.put("description", plugin.getDescription().getDescription());
        response.put("authors", plugin.getDescription().getAuthors());
        response.put("website", plugin.getDescription().getWebsite());
        response.put("contributors", plugin.getDescription().getContributors());
        response.put("dependencies", plugin.getDescription().getDepend());
        response.put("soft-dependencies", plugin.getDescription().getSoftDepend());
        response.put("load-order", plugin.getDescription().getLoad());
        response.put("enabled", plugin.isEnabled());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    public NanoHTTPD.Response postPlugin(final Map<String, String> params) {
        String pluginUrl = params.get("url");
        if (pluginUrl == null || pluginUrl.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\":\"Missing plugin URL.\"}");
        }

        try {
            // Set up connection to the URL
            URL url = new URL(pluginUrl);
            URLConnection connection = url.openConnection();
            connection.connect();

            // Get input stream from the connection
            InputStream inputStream = new BufferedInputStream(url.openStream());

            // Determine the filename from the URL
            String fileName = pluginUrl.substring(pluginUrl.lastIndexOf('/') + 1);

            // Path to the plugins directory (adjust path as needed)
            File pluginDir = new File("plugins");

            // Path to save the plugin file
            File pluginFile = new File(pluginDir, fileName);
            FileOutputStream outputStream = new FileOutputStream(pluginFile);

            // Buffer for data and downloading
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close streams
            outputStream.close();
            inputStream.close();

            if (params.get("reload").equals("true")) {
                final long delay = 20L;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.reload();
                    }
                }.runTaskLater(MinecraftServerAPI.getInstance(), delay);
            }

            // Return a success response
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{}");
        }
    }

    public NanoHTTPD.Response deletePlugin(final Map<String, String> params) {
        String pluginName = params.get("name");
        if (pluginName == null || pluginName.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        try {
            // Deactivate the plugin
            if (plugin.isEnabled()) {
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

            // Get the Plugin .jar file
            File pluginFile = findPluginJar(pluginName);
            if (pluginFile != null && pluginFile.exists()) {
                // Rename the file before deletion to prevent issues
                Path backupPath = Paths.get(pluginFile.getParent(), pluginFile.getName() + ".deleting");
                Files.move(pluginFile.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(backupPath);
            } else {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
            }

            File pluginDir = new File("plugins", plugin.getName());
            if (pluginDir.exists() && pluginDir.isDirectory()) {
                deleteDirectory(pluginDir);
            }


            if (params.get("reload").equals("true")) {
                final long delay = 20L;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.reload();
                    }
                }.runTaskLater(MinecraftServerAPI.getInstance(), delay);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{}");
        }


        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response activatePlugin(final Map<String, String> params) {
        String pluginName = params.get("name");
        if (pluginName == null || pluginName.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        try {
            if (!plugin.isEnabled()) {
                Bukkit.getPluginManager().enablePlugin(plugin);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{}");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    public NanoHTTPD.Response deactivatePlugin(final Map<String, String> params) {
        String pluginName = params.get("name");
        if (pluginName == null || pluginName.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        try {
            if (plugin.isEnabled()) {
                Bukkit.getPluginManager().disablePlugin(plugin);
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{}");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
    }

    private File findPluginJar(final String pluginName) {
        File pluginDir = new File("plugins");
        File[] files = pluginDir.listFiles((dir, name) -> name.toLowerCase().startsWith(pluginName.toLowerCase()) && name.endsWith(".jar"));
        return files != null && files.length > 0 ? files[0] : null;
    }
}
