package com.shweit.serverapi.webhooks.player;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.json.JSONObject;

public final class PlayerRespawn implements WebHook, Listener {

    private final String eventName = WebHookEnum.PLAYER_RESPAWN.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

     @EventHandler
     public void onPlayerRespawn(final PlayerRespawnEvent event) {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("event", eventName);
         jsonObject.put("player", event.getPlayer().getName());
         jsonObject.put("location", event.getPlayer().getLocation().toString());
         jsonObject.put("respawnLocation", event.getRespawnLocation().toString());
         jsonObject.put("isBedSpawn", event.isBedSpawn());
         jsonObject.put("isAnchorSpawn", event.isAnchorSpawn());
         jsonObject.put("respawnReason", event.getRespawnReason().name());

         RegisterWebHooks.sendToAllUrls(jsonObject);
     }
}
