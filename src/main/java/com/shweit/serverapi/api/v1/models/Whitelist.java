package com.shweit.serverapi.api.v1.models;

import com.google.gson.annotations.Expose;

public class Whitelist {
    @Expose
    private String uuid = null;

    @Expose
    private String username = null;

    /**
     * The Player's UUID
     *
     * @return uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * The Player's username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
