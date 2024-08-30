package com.shweit.serverapi.webhooks.block;

import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.json.JSONObject;

public final class NotePlay implements WebHook, Listener {

    private final String eventName = WebHookEnum.NOTE_PLAY.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            MinecraftServerAPI plugin = MinecraftServerAPI.getInstance();
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler
    public void onNotePlay(final NotePlayEvent event) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", eventName);
        jsonObject.put("instrument", event.getInstrument().name());
        jsonObject.put("tone", event.getNote().getTone().name());
        jsonObject.put("octave", event.getNote().getOctave());
        jsonObject.put("pitch", event.getNote().getPitch());
        jsonObject.put("location", event.getBlock().getLocation().toString());

        RegisterWebHooks.sendToAllUrls(jsonObject);
    }
}
