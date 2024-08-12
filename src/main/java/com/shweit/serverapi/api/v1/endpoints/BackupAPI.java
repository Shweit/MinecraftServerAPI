// BackupAPI.java
package com.shweit.serverapi.api.v1.endpoints;

import com.shweit.serverapi.WebServer;
import com.shweit.serverapi.utils.BackupManager;
import com.shweit.serverapi.utils.CustomCommandSender;
import com.shweit.serverapi.utils.Logger;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BackupAPI {

    private final FileConfiguration bukkitConfig;
    private final BackupManager backupManager;

    public BackupAPI(FileConfiguration bukkitConfig, JavaPlugin plugin, WebServer app) {
        this.bukkitConfig = bukkitConfig;
        this.backupManager = new BackupManager(plugin, app);
    }

    @OpenApi(
            path = "/v1/backups",
            methods = {HttpMethod.GET},
            summary = "List all available backups",
            description = "This endpoint returns a list of all available backups.",
            tags = {"Backup"},
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = String[].class))
            }
    )
    public final void listBackups(Context ctx) {
        // Logic to list all backups
        String[] backups = this.backupManager.listBackups();
        ctx.json(backups);
    }

    @OpenApi(
            path = "/v1/backups",
            methods = {HttpMethod.POST},
            summary = "Create a new backup",
            description = "This endpoint creates a new backup with the current timestamp.",
            tags = {"Backup"},
            responses = {
                    @OpenApiResponse(status = "201", content = @OpenApiContent(from = String.class))
            }
    )
    public final void createBackup(Context ctx) {
        // Logic to create a new backup
        String backupName = backupManager.createBackup(this.bukkitConfig);
        ctx.status(201).result("Backup created: " + backupName);
    }

    @OpenApi(
            path = "/v1/backups",
            methods = {HttpMethod.DELETE},
            summary = "Delete a backup",
            description = "This endpoint deletes a backup with the given timestamp.",
            tags = {"Backup"},
            requestBody = @OpenApiRequestBody(
                    required = true,
                    content = {
                            @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                                    @OpenApiContentProperty(name = "timestamp", type = "string"),
                            }),
                    }
            ),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = String.class))
            }
    )
    public final void deleteBackup(Context ctx) {
        // Logic to delete a backup
        String timestamp = ctx.formParam("timestamp");
        if (timestamp == null || timestamp.isEmpty()) {
            ctx.status(400).result("Missing timestamp parameter");
            return;
        }
        backupManager.deleteBackup(timestamp);
        ctx.status(200).result("Backup deleted: " + timestamp);
    }
}