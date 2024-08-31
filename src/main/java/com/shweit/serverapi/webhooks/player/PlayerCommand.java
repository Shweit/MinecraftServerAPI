package com.shweit.serverapi.webhooks.player;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.json.JSONObject;

public final class PlayerCommand implements WebHook, Listener {

    private final String eventName = WebHookEnum.PLAYER_COMMAND.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

     @EventHandler
     public void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("event", eventName);
         jsonObject.put("player", event.getPlayer().getName());
         jsonObject.put("command", event.getMessage());
         jsonObject.put("location", event.getPlayer().getLocation().toString());

         if (event.getPlayer().getAddress() != null) {
             jsonObject.put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());
         }

         RegisterWebHooks.sendToAllUrls(jsonObject);
     }
}
