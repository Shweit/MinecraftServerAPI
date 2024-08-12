package com.shweit.serverapi.api.v1;

import com.shweit.serverapi.WebServer;
import com.shweit.serverapi.api.v1.endpoints.*;
import com.shweit.serverapi.utils.ChatListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ApiV1Initializer {
    private final PlayerAPI playerAPI;
    private final WhitelistAPI whitelistAPI;
    private final ServerAPI serverAPI;
    private final GeyserAPI geyserAPI;
    private final MaintenanceAPI maintenanceAPI;
    private final BackupAPI backupAPI;

    public ApiV1Initializer(
            ChatListener chatListener,
            FileConfiguration bukkitConfig,
            JavaPlugin plugin,
            WebServer app
    ) {
        this.playerAPI = new PlayerAPI();
        this.whitelistAPI = new WhitelistAPI();
        this.serverAPI = new ServerAPI(chatListener, plugin);
        this.geyserAPI = new GeyserAPI();
        this.maintenanceAPI = new MaintenanceAPI();
        this.backupAPI = new BackupAPI(bukkitConfig, plugin, app);
    }

    public PlayerAPI getPlayerAPI() {
        return playerAPI;
    }

    public WhitelistAPI getWhitelistAPI() {
        return whitelistAPI;
    }

    public ServerAPI getServerAPI() {
        return serverAPI;
    }

    public GeyserAPI getGeyserAPI() {
        return geyserAPI;
    }

    public MaintenanceAPI getMaintenanceAPI() {
        return maintenanceAPI;
    }

    public BackupAPI getBackupAPI() {
        return backupAPI;
    }
}
