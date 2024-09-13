package com.shweit.serverapi.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.UUID;

public final class Helper {

    private Helper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static UUID usernameToUUID(final String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
                .build();
        HttpResponse<String> response;

        UUID playerUUID;

        try {
            response = java.net.http.HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
            playerUUID = UUID.fromString(jsonObject.get("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        } catch (InterruptedException e) {
            Logger.error("Request was interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException e) {
            Logger.error("Failed to convert username to UUID: " + username + " - " + e.getMessage());
            return null;
        } catch (Exception e) {
            Logger.error("Unexpected error occurred: " + e.getMessage());
            return null;
        }

        return playerUUID;
    }

    public static double calculateTPS() {
        final int ticksSize = 600;
        final double maxTps = 20.0;

        long[] ticks = new long[ticksSize];
        int tickCount = 0;

        ticks[tickCount % ticksSize] = System.currentTimeMillis();
        tickCount++;

        if (tickCount < 100) {
            return maxTps;
        }
        int target = (tickCount - 1 - 100) % ticksSize;
        long elapsed = System.currentTimeMillis() - ticks[target];

        return 100 / (elapsed / 1000.0);
    }

    public static void deleteDirectory(final File directory) throws IOException {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    Files.deleteIfExists(file.toPath());
                }
            }
            Files.deleteIfExists(directory.toPath());
        }
    }

    public static String formatSize(final long size) {
        String[] units = new String[] {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double sizeD = size;

        while (sizeD >= 1024 && unitIndex < units.length - 1) {
            sizeD /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", sizeD, units[unitIndex]);
    }

    public static String dateConverter(final long timestamp) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new java.util.Date(timestamp));
    }
}
