package com.shweit.serverapi.webhooks.server;

import com.shweit.serverapi.webhooks.WebHook;
import com.shweit.serverapi.webhooks.RegisterWebHooks;
import com.shweit.serverapi.webhooks.WebHookEnum;
import org.json.JSONObject;

public final class ServerStop implements WebHook {

    private final String eventName = WebHookEnum.SERVER_STOP.label;

    @Override
    public void register() {
        if (RegisterWebHooks.doActivateWebhook(eventName)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event", eventName);
            jsonObject.put("message", "Server has stopped");
            jsonObject.put("note", "This event is triggered when the plugin gets disabled, that means the server could still be runnig.");

            RegisterWebHooks.sendToAllUrls(jsonObject);
        }
    }
}
