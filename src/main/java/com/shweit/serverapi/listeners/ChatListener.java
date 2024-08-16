package com.shweit.serverapi.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.SimpleDateFormat;
import java.util.*;

public final class ChatListener implements Listener {
    private final List<HashMap<String, String>> messages = new ArrayList<>();

    @EventHandler
    public void onPlayerChat(final org.bukkit.event.player.AsyncPlayerChatEvent event) {
        HashMap<String, String> message = new HashMap<>();
        message.put("player", event.getPlayer().getName());
        message.put("message", event.getMessage());

        String readableTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());
        message.put("time", readableTime);
        messages.add(message);
    }

    public List<HashMap<String, String>> getMessages() {
        return messages;
    }
}
