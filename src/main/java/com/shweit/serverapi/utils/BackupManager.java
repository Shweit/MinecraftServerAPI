// BackupManager.java
package com.shweit.serverapi.utils;

import com.shweit.serverapi.WebServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupManager implements Listener {
    private static final String BACKUP_DIRECTORY = "backups";
    private static final String[] FILES_TO_BACKUP = {
            "world", "world_nether", "world_the_end", "plugins", "config.yml", "server.properties"
    };
    private static boolean preventRejoin = false;

    private final JavaPlugin plugin;
    private final WebServer app;

    public BackupManager(JavaPlugin plugin, WebServer app) {
        this.plugin = plugin;
        this.app = app;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public String[] listBackups() {
        File backupDirectory = new File(BACKUP_DIRECTORY);
        if (!backupDirectory.exists() || !backupDirectory.isDirectory()) {
            return new String[0];
        }

        File[] backupFiles = backupDirectory.listFiles((dir, name) -> name.endsWith(".zip"));
        if (backupFiles == null) {
            return new String[0];
        }

        List<String> backupNames = new ArrayList<>();
        for (File backupFile : backupFiles) {
            backupNames.add(backupFile.getName());
        }

        return backupNames.toArray(new String[0]);
    }

    public String createBackup(FileConfiguration bukkitConfig) {
        saveWorlds();

        File backupDirectory = new File(BACKUP_DIRECTORY);
        if (!backupDirectory.exists()) {
            backupDirectory.mkdirs();
        }

        String backupName = "backup_" + System.currentTimeMillis() + ".zip";
        String backupPath = BACKUP_DIRECTORY + File.separator + backupName;

        List<String> additionalFiles = bukkitConfig.getStringList("backup_files");
        Set<String> filesToBackup = new HashSet<>(Arrays.asList(FILES_TO_BACKUP));
        filesToBackup.addAll(additionalFiles);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupPath))) {
            for (String fileName : filesToBackup) {
                File fileToBackup = new File(fileName);
                if (fileToBackup.exists()) {
                    addToZip(fileToBackup, fileToBackup.getName(), zos);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return backupName;
    }

    public void deleteBackup(String timestamp) {
        String backupName = "backup_" + timestamp + ".zip";
        String backupPath = BACKUP_DIRECTORY + File.separator + backupName;

        File backupFile = new File(backupPath);
        if (backupFile.exists()) {
            backupFile.delete();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (preventRejoin) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Server is restoring a backup. Please try again later.");
        }
    }

    private void saveWorlds() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    world.save();
                }
            }
        }.runTask(plugin);
    }

    private static void addToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    addToZip(childFile, fileName + "/" + childFile.getName(), zos);
                }
            }
        } else {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            }
        }
    }
}