package com.shweit.serverapi.endpoints.v1;

import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

public class PlayerAPI {
    public NanoHTTPD.Response getPlayers() {
        JSONArray playersArray = new JSONArray();

        for (Player player : Bukkit.getOnlinePlayers()) {
            JSONObject playerJson = new JSONObject();
            playerJson.put("name", player.getName());
            playerJson.put("uuid", player.getUniqueId().toString());
            playersArray.put(playerJson);
        }

        JSONObject responseJson = new JSONObject();
        responseJson.put("onlinePlayers", playersArray);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", responseJson.toString());
    }
}
