package com.shweit.serverapi.webhooks.enchantment;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.json.JSONObject;

public final class EnchantItem implements WebHook, Listener {

    private final String eventName = WebHookEnum.ENCHANT_ITEM.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onEnchantItem(final EnchantItemEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("item", event.getItem().getType().name());
        jsonObject.put("player", event.getEnchanter().getName());
        jsonObject.put("enchantments", event.getEnchantsToAdd().toString());
        jsonObject.put("location", event.getEnchantBlock().getLocation().toString());
        jsonObject.put("expLevelCost", event.getExpLevelCost());
        jsonObject.put("levelHint", event.getLevelHint());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
