package com.shweit.serverapi.webhooks;

import java.util.List;
import java.util.stream.Stream;

public enum WebHookEnum {
    SERVER_START("server_start", "Triggered when the server starts"),
    SERVER_STOP("server_stop", "Triggered when the server stops"),
    PLUGIN_DISABLE("plugin_disable", "Triggered when a plugin is disabled"),
    PLUGIN_ENABLE("plugin_enable", "Triggered when a plugin is enabled"),;

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
