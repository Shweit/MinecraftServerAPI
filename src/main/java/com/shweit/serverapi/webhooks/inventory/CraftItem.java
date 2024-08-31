package com.shweit.serverapi.webhooks.inventory;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.json.JSONObject;

public final class CraftItem implements WebHook, Listener {

    private final String eventName = WebHookEnum.CRAFT_ITEM.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

     @EventHandler
     public void onCraftItem(final CraftItemEvent event) {
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("event", eventName);
         jsonObject.put("item", event.getRecipe().getResult().getType().name());
         jsonObject.put("location", event.getWhoClicked().getLocation().toString());
         jsonObject.put("recipe", event.getRecipe().toString());

         if (event.getInventory().getResult() != null) {
             jsonObject.put("result", event.getInventory().getResult().getType().name());
         }

         RegisterWebHooks.sendToAllUrls(jsonObject);
     }
}
