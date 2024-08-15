package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.BitSet;
import java.util.Map;

public class ServerAPI {
    public NanoHTTPD.Response ping(Map<String, String> params) {
        return NanoHTTPD.newFixedLengthResponse("pong");
    }

    public NanoHTTPD.Response serverInfo(Map<String, String> params) {
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
}
