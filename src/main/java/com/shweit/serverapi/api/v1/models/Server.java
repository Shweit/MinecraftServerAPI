package com.shweit.serverapi.api.v1.models;

import org.bukkit.util.CachedServerIcon;

import java.util.HashMap;

public class Server {
    private String name;
    private String version;
    private int playerCount;
    private int maxPlayers;
    private String motd;
    private HashMap<String, String> health;
    private CachedServerIcon icon;

    /**
     * Gets the name of the server.
     * @return the name of the server
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the version of the server.
     * @return the version of the server
     */
    public String getVersion() {
        return version;
    }

    /**
     * Gets the number of players currently online.
     * @return the number of players currently online
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * Gets the maximum number of players that can be online.
     * @return the maximum number of players that can be online
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Gets the message of the day for the server.
     * @return the message of the day for the server
     */
    public String getMotd() {
        return motd;
    }

    /**
     * Gets the health of the server.
     * @return the health of the server
     */
    public HashMap<String, String> getHealth() {
        return health;
    }

    /**
     * Gets the icon of the server.
     * @return the icon of the server
     */
    public CachedServerIcon getIcon() {
        return icon;
    }

    /**
     * Sets the name of the server.
     * @param name the name of the server
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the version of the server.
     * @param version the version of the server
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Sets the number of players currently online.
     * @param playerCount the number of players currently online
     */
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    /**
     * Sets the maximum number of players that can be online.
     * @param maxPlayers the maximum number of players that can be online
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Sets the message of the day for the server.
     * @param motd the message of the day for the server
     */
    public void setMotd(String motd) {
        this.motd = motd;
    }

    /**
     * Sets the health of the server.
     * @param health the health of the server
     */
    public void setHealth(HashMap<String, String> health) {
        this.health = health;
    }

    /**
     * Sets the icon of the server.
     * @param icon the icon of the server
     */
    public void setIcon(CachedServerIcon icon) {
        this.icon = icon;
    }
}
