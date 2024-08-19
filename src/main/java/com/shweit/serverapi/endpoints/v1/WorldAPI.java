package com.shweit.serverapi.endpoints.v1;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Helper;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.*;
import org.bukkit.entity.SpawnCategory;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WorldAPI {
    public NanoHTTPD.Response getWorlds(final Map<String, String> ignoredParams) {
        JSONObject response = new JSONObject();

        List<World> worlds = Bukkit.getWorlds();
        worlds.forEach(w -> {
            JSONObject worldObject = new JSONObject();
            worldObject.put("name", w.getName());
            worldObject.put("environment", w.getEnvironment().name());
            worldObject.put("seed", w.getSeed());
            worldObject.put("time", w.getTime());
            worldObject.put("fullTime", w.getFullTime());
            worldObject.put("difficulty", w.getDifficulty().name());
            worldObject.put("weatherDuration", w.getWeatherDuration());
            worldObject.put("thunderDuration", w.getThunderDuration());
            worldObject.put("allowAnimals", w.getAllowAnimals());
            worldObject.put("allowMonsters", w.getAllowMonsters());
            worldObject.put("maxHeight", w.getMaxHeight());
            worldObject.put("seaLevel", w.getSeaLevel());
            worldObject.put("generateStructures", w.canGenerateStructures());
            worldObject.put("pvp", w.getPVP());
            worldObject.put("keepSpawnInMemory", w.getKeepSpawnInMemory());
            worldObject.put("ambientSpawnLimit", w.getAmbientSpawnLimit());
            worldObject.put("animalSpawnLimit", w.getAnimalSpawnLimit());
            worldObject.put("monsterSpawnLimit", w.getMonsterSpawnLimit());
            worldObject.put("waterAnimalSpawnLimit", w.getWaterAnimalSpawnLimit());
            worldObject.put("waterAmbientSpawnLimit", w.getWaterAmbientSpawnLimit());
            worldObject.put("lightningDuration", w.getWeatherDuration());
            worldObject.put("ticksPerAnimalSpawns", w.getTicksPerAnimalSpawns());
            worldObject.put("ticksPerMonsterSpawns", w.getTicksPerMonsterSpawns());
            worldObject.put("ticksPerWaterSpawns", w.getTicksPerWaterSpawns());
            worldObject.put("ticksPerAmbientSpawns", w.getTicksPerAmbientSpawns());
            worldObject.put("ticksPerWaterAmbientSpawns", w.getTicksPerWaterAmbientSpawns());
            worldObject.put("worldBorder", w.getWorldBorder().getSize());
            worldObject.put("worldType", w.getWorldType().name());
            worldObject.put("generator", w.getGenerator());

            JSONObject gameRules = new JSONObject();
            for (String gameRule : w.getGameRules()) {
                gameRules.put(gameRule, w.getGameRuleValue(gameRule));
            }
            worldObject.put("gameRules", gameRules);

            response.put(w.getName(), worldObject);
        });
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    public NanoHTTPD.Response createWorld(final Map<String, String> params) {
        String worldName = params.get("world");
        String environment = params.get("environment");
        String seed = params.get("seed");
        String worldType = params.get("worldType");
        String difficulty = params.get("difficulty");
        String animalCap = params.get("animalCap");
        String monsterCap = params.get("monsterCap");
        String hardcore = params.get("hardcore");
        String pvp = params.get("pvp");
        String generateStructures = params.get("generateStructures");
        String spawnX = params.get("spawnX");
        String spawnY = params.get("spawnY");
        String spawnZ = params.get("spawnZ");

        AtomicBoolean success = new AtomicBoolean(true);
        JSONObject error = new JSONObject();

        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
            try {
                // World creation
                WorldCreator worldCreator = new WorldCreator(worldName);

                // Set the environment
                worldCreator.environment(World.Environment.valueOf(environment.toUpperCase()));

                // Set the seed if provided
                if (seed != null && !seed.isEmpty()) {
                    worldCreator.seed(Long.parseLong(seed));
                }

                // Set the world type
                worldCreator.type(WorldType.valueOf(worldType.toUpperCase()));

                // Set whether to generate structures like villages
                worldCreator.generateStructures(Boolean.parseBoolean(generateStructures));

                // Create the world
                World world = Bukkit.createWorld(worldCreator);

                // Set difficulty if provided
                if (difficulty != null && !difficulty.isEmpty()) {
                    world.setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));
                }

                // Set whether animals and monsters are allowed
                if (animalCap != null && !animalCap.isEmpty()) {
                    world.setSpawnLimit(SpawnCategory.ANIMAL, Integer.parseInt(animalCap));
                }
                if (monsterCap != null && !monsterCap.isEmpty()) {
                    world.setSpawnLimit(SpawnCategory.MONSTER, Integer.parseInt(monsterCap));
                }

                // Set PvP
                world.setPVP(Boolean.parseBoolean(pvp));

                // Set spawn location if provided
                if (spawnX != null && spawnY != null && spawnZ != null) {
                    int x = Integer.parseInt(spawnX);
                    int y = Integer.parseInt(spawnY);
                    int z = Integer.parseInt(spawnZ);
                    Location spawnLocation = new Location(world, x, y, z);
                    world.setSpawnLocation(spawnLocation);
                }

                // Set whether the world is hardcore
                if (Boolean.parseBoolean(hardcore)) {
                    world.setHardcore(true);
                }
            } catch (Exception e) {
                success.set(false);
                Logger.error("Error creating world: " + e.getMessage());
                e.getStackTrace();
                error.put("error", e.getMessage());
            }
        });

        if (success.get()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", error.toString());
    }

    public NanoHTTPD.Response deleteWorld(final Map<String, String> params) {
        String worldName = params.get("world");
        if (worldName == null || worldName.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        AtomicBoolean success = new AtomicBoolean(true);
        JSONObject error = new JSONObject();

        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
            try {
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    Bukkit.unloadWorld(world, false);
                    File worldFolder = world.getWorldFolder();
                    Helper.deleteDirectory(worldFolder);
                    success.set(world.getWorldFolder().delete());
                } else {
                    success.set(false);
                    error.put("error", "World not found");
                }
            } catch (Exception e) {
                success.set(false);
                Logger.error("Error deleting world: " + e.getMessage());
                e.getStackTrace();
                error.put("error", e.getMessage());
            }
        });

        if (success.get()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", error.toString());
    }

    public NanoHTTPD.Response getWorld(final Map<String, String> params) {
        String worldName = params.get("world");
        if (worldName == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        JSONObject response = new JSONObject();

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }
        JSONObject worldObject = new JSONObject();
        worldObject.put("name", world.getName());
        worldObject.put("environment", world.getEnvironment().name());
        worldObject.put("seed", world.getSeed());
        worldObject.put("time", world.getTime());
        worldObject.put("fullTime", world.getFullTime());
        worldObject.put("difficulty", world.getDifficulty().name());
        worldObject.put("weatherDuration", world.getWeatherDuration());
        worldObject.put("thunderDuration", world.getThunderDuration());
        worldObject.put("allowAnimals", world.getAllowAnimals());
        worldObject.put("allowMonsters", world.getAllowMonsters());
        worldObject.put("maxHeight", world.getMaxHeight());
        worldObject.put("seaLevel", world.getSeaLevel());
        worldObject.put("generateStructures", world.canGenerateStructures());
        worldObject.put("pvp", world.getPVP());
        worldObject.put("keepSpawnInMemory", world.getKeepSpawnInMemory());
        worldObject.put("ambientSpawnLimit", world.getAmbientSpawnLimit());
        worldObject.put("animalSpawnLimit", world.getAnimalSpawnLimit());
        worldObject.put("monsterSpawnLimit", world.getMonsterSpawnLimit());
        worldObject.put("waterAnimalSpawnLimit", world.getWaterAnimalSpawnLimit());
        worldObject.put("waterAmbientSpawnLimit", world.getWaterAmbientSpawnLimit());
        worldObject.put("lightningDuration", world.getWeatherDuration());
        worldObject.put("ticksPerAnimalSpawns", world.getTicksPerAnimalSpawns());
        worldObject.put("ticksPerMonsterSpawns", world.getTicksPerMonsterSpawns());
        worldObject.put("ticksPerWaterSpawns", world.getTicksPerWaterSpawns());
        worldObject.put("ticksPerAmbientSpawns", world.getTicksPerAmbientSpawns());
        worldObject.put("ticksPerWaterAmbientSpawns", world.getTicksPerWaterAmbientSpawns());
        worldObject.put("worldBorder", world.getWorldBorder().getSize());
        worldObject.put("worldType", world.getWorldType().name());
        worldObject.put("generator", world.getGenerator());

        JSONObject gameRules = new JSONObject();
        for (String gameRule : world.getGameRules()) {
            gameRules.put(gameRule, world.getGameRuleValue(gameRule));
        }
        worldObject.put("gameRules", gameRules);

        response.put(world.getName(), worldObject);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    public NanoHTTPD.Response updateWorld(final Map<String, String> params) {
        String worldName = params.get("world");
        if (worldName == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{}");
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{}");
        }

        String difficulty = params.get("difficulty");
        String animalCap = params.get("animalCap");
        String monsterCap = params.get("monsterCap");
        String pvp = params.get("pvp");
        String hardcore = params.get("hardcore");
        String spawnX = params.get("spawnX");
        String spawnY = params.get("spawnY");
        String spawnZ = params.get("spawnZ");

        AtomicBoolean success = new AtomicBoolean(true);
        JSONObject error = new JSONObject();

        Bukkit.getScheduler().runTask(MinecraftServerAPI.getInstance(), () -> {
            try {
                // Set difficulty if provided
                if (difficulty != null && !difficulty.isEmpty()) {
                    world.setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));
                }

                // Set whether animals and monsters are allowed
                if (animalCap != null && !animalCap.isEmpty()) {
                    world.setSpawnLimit(SpawnCategory.ANIMAL, Integer.parseInt(animalCap));
                }
                if (monsterCap != null && !monsterCap.isEmpty()) {
                    world.setSpawnLimit(SpawnCategory.MONSTER, Integer.parseInt(monsterCap));
                }

                // Set PvP
                if (pvp != null && !pvp.isEmpty()) {
                    world.setPVP(Boolean.parseBoolean(pvp));
                }

                // Set spawn location if provided
                if (spawnX != null && spawnY != null && spawnZ != null) {
                    int x = Integer.parseInt(spawnX);
                    int y = Integer.parseInt(spawnY);
                    int z = Integer.parseInt(spawnZ);
                    Location spawnLocation = new Location(world, x, y, z);
                    world.setSpawnLocation(spawnLocation);
                }

                // Set whether the world is hardcore
                if (hardcore != null && !hardcore.isEmpty()) {
                    world.setHardcore(Boolean.parseBoolean(hardcore));
                }

                world.save();
            } catch (Exception e) {
                success.set(false);
                Logger.error("Error updating world: " + e.getMessage());
                e.getStackTrace();
                error.put("error", e.getMessage());
            }
        });

        if (success.get()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", error.toString());
    }
}
