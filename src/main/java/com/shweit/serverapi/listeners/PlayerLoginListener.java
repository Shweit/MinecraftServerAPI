package com.shweit.serverapi.listeners;

import com.shweit.serverapi.MinecraftServerAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class PlayerLoginListener implements Listener {
    @EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        if (MinecraftServerAPI.isBlockNewConnections()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, MinecraftServerAPI.getBlockNewConnectionsMessage());
        }
    }
}
