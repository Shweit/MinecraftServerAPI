package com.shweit.serverapi.utils;

import com.shweit.serverapi.MinecraftServerAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CheckForUpdate implements Listener {

    private static final String GITHUB_API_URL = "https://api.github.com/repos/Shweit/MinecraftServerAPI/releases/latest";
    private static final String USER_AGENT = "Mozilla/5.0";
    public String latestVersion;

    public boolean checkForPluginUpdate() {
        latestVersion = fetchLatestVersion();

        if (latestVersion != null) {
            String currentVersion = MinecraftServerAPI.getInstance().getDescription().getVersion();

            return !latestVersion.equals(currentVersion);
        } else {
            Logger.warning("Failed to fetch the latest version from GitHub API.");
        }

        return false;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("mcapi.version")) {
            Logger.debug("Checking for plugin update...");
            if (checkForPluginUpdate()) {
                event.getPlayer().sendMessage("§aA new version of the MinecraftServerAPI plugin is available");
                event.getPlayer().sendMessage("§aCurrent version: §f" + MinecraftServerAPI.getInstance().getDescription().getVersion());
                event.getPlayer().sendMessage("§aLatest version: §f" + latestVersion);
            }
        }
    }

    /**
     * Fetches the latest version from the GitHub API.
     *
     * @return the latest version as a String, or null if there was an error.
     */
    private String fetchLatestVersion() {
        try {
            URL url = new URL(GITHUB_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            connection.disconnect();

            String jsonResponse = content.toString();
            Pattern pattern = Pattern.compile("\"tag_name\":\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(jsonResponse);

            if (matcher.find()) {
                return matcher.group(1);
            } else {
                Logger.warning("Tag name not found in the GitHub API response.");
                return null;
            }
        } catch (Exception e) {
            Logger.error("An error occurred while fetching the latest version: " + e.getMessage());
            return null;
        }
    }
}
