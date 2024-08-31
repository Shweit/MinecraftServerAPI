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
    ENTITY_EXPLODE("entity_explode", "Triggered when an entity explodes"),;

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
