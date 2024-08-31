package com.shweit.serverapi.webhooks;

import java.util.List;
import java.util.stream.Stream;

public enum WebHookEnum {
    SERVER_START("server_start", "Triggered when the server starts"),
    SERVER_STOP("server_stop", "Triggered when the server stops"),
    PLUGIN_DISABLE("plugin_disable", "Triggered when a plugin is disabled"),
    PLUGIN_ENABLE("plugin_enable", "Triggered when a plugin is enabled"),
    BLOCK_BREAK("block_break", "Triggered when a block is broken"),
    BLOCK_PLACE("block_place", "Triggered when a block is placed"),
    BLOCK_BURN("block_burn", "Triggered when a block is burned"),
    BLOCK_REDSTONE("block_redstone", "Triggered when a block is powered by redstone or a redstone current changes"),
    NOTE_PLAY("note_play", "Triggered when a note block plays a note"),
    SIGN_CHANGE("sign_change", "Triggered when a sign is changed"),
    ENCHANT_ITEM("enchant_item", "Triggered when an item is enchanted"),
    CREEPER_POWER("creeper_power", "Triggered when a Creeper is hit by lightning and becomes supercharged"),
    CREATURE_SPAWN("creature_spawn", "Triggered when a creature spawns"),
    ENTITY_DEATH("entity_death", "Triggered when an entity dies"),
    ENTITY_EXPLODE("entity_explode", "Triggered when an entity explodes"),
    ENTITY_SHOOT_BOW("entity_shoot_bow", "Triggered when an entity shoots a bow"),
    ENTITY_TAME("entity_tame", "Triggered when an entity is tamed"),
    EXPLOSION_PRIME("explosion_prime", "Triggered when an explosion is primed"),
    PLAYER_DEATH("player_death", "Triggered when a player dies"),
    BREW("brew", "Triggered when a potion is brewed"),
    CRAFT_ITEM("craft_item", "Triggered when an item is crafted"),
    FURNACE_BURN("furnace_burn", "Triggered when a furnace burns an item as fuel"),
    FURNACE_SMELT("furnace_smelt", "Triggered when a furnace smelts an item"),
    PLAYER_CHAT("player_chat", "Triggered when a player sends a chat message"),
    PLAYER_LOGIN("player_login", "Triggered when a player logs in"),
    PLAYER_COMMAND("player_command", "Triggered when a player executes a command"),
    PLAYER_GAMEMODE_CHANGE("player_gamemode_change", "Triggered when a player changes their gamemode"),
    PLAYER_ITEM_BREAK("player_item_break", "Triggered when a player's item breaks"),
    PLAYER_JOIN("player_join", "Triggered when a player joins the server"),
    PLAYER_KICK("player_kick", "Triggered when a player is kicked from the server"),
    PLAYER_QUIT("player_quit", "Triggered when a player quits the server"),
    PLAYER_RESPAWN("player_respawn", "Triggered when a player respawns"),
    LIGHTNING_STRIKE("lightning_strike", "Triggered when lightning strikes"),
    WEATHER_CHANGE("weather_change", "Triggered when the weather changes"),
    THUNDER_CHANGE("thunder_change", "Triggered when the thunder changes"),
    WORLD_LOAD("world_load", "Triggered when a world is loaded"),
    WORLD_SAVE("world_save", "Triggered when a world is saved"),
    WORLD_UNLOAD("world_unload", "Triggered when a world is unloaded");

    public final String label;
    public final String description;

    WebHookEnum(final String enumLabel, final String enumDescription) {
        this.label = enumLabel;
        this.description = enumDescription;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValid(final String webhookName) {
        for (WebHookEnum hook : values()) {
            if (hook.label.equalsIgnoreCase(webhookName)) {
                return true;
            }
        }
        return false;
    }

    public static String getValidHooks() {
        StringBuilder hooks = new StringBuilder();
        for (WebHookEnum hook : values()) {
            if (hooks.length() > 0) {
                hooks.append(", ");
            }
            hooks.append(hook.label.toLowerCase());
        }
        return hooks.toString();
    }

    public static List<String> getValidHookList() {
        return Stream.of(values()).map(hook -> hook.label.toLowerCase()).toList();
    }
}
