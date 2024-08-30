package com.shweit.serverapi.webhooks.block;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.json.JSONObject;

public final class SignChange implements WebHook, Listener {

    private final String eventName = WebHookEnum.SIGN_CHANGE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onSignChange(final SignChangeEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("block", event.getBlock().getType().name());
        jsonObject.put("location", event.getBlock().getLocation().toString());
        jsonObject.put("line1", event.getLine(0));
        jsonObject.put("line2", event.getLine(1));
        jsonObject.put("line3", event.getLine(2));
        jsonObject.put("line4", event.getLine(3));
        jsonObject.put("player", event.getPlayer().getName());
        jsonObject.put("side", event.getSide().name());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
