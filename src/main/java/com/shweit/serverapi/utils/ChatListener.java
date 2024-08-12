package com.shweit.serverapi.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ChatListener implements Listener {

    private final List<String> chatMessages = new ArrayList<>();

    public ChatListener(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        chatMessages.add(event.getPlayer().getDisplayName() + ": " + event.getMessage());
    }

    public List<String> getChatMessages() {
        return chatMessages;
    }
}