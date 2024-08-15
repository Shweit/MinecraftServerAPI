package com.shweit.serverapi.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class Helper {
    public static UUID usernameToUUID(String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .build();
        HttpResponse<String> response;

        UUID playerUUID = null;

        try {
            response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            playerUUID = UUID.fromString(jsonObject.get("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (Exception e) {
            Logger.warning("Failed to convert username to UUID: " + username);
            return null;
        }

        return playerUUID;
    }

    public static double calculateTPS() {
        final int TICKS_SIZE = 600;
        final double MAX_TPS = 20.0;

        long[] ticks = new long[TICKS_SIZE];
        int tickCount = 0;

        ticks[tickCount % TICKS_SIZE] = System.currentTimeMillis();
        tickCount++;

        if (tickCount < 100) {
            return MAX_TPS;
        }
        int target = (tickCount - 1 - 100) % TICKS_SIZE;
        long elapsed = System.currentTimeMillis() - ticks[target];

        return 100 / (elapsed / 1000.0);
    }
}
