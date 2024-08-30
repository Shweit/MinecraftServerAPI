package com.shweit.serverapi.webhooks.block;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.json.JSONObject;

public final class BlockRedstone implements WebHook, Listener {

    private final String eventName = WebHookEnum.BLOCK_REDSTONE.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onBlockRedstone(final BlockRedstoneEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("block", event.getBlock().getType().name());
        jsonObject.put("location", event.getBlock().getLocation().toString());
        jsonObject.put("oldCurrent", event.getOldCurrent());
        jsonObject.put("newCurrent", event.getNewCurrent());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
