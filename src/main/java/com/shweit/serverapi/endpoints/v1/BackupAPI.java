package com.shweit.serverapi.endpoints.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shweit.serverapi.MinecraftServerAPI;
import com.shweit.serverapi.utils.Helper;
import com.shweit.serverapi.utils.Logger;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.util.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class BackupAPI {
    public static List<String> filesToBackup = List.of(
        "world",
        "world_nether",
        "world_the_end",
        "plugins",
        "config",
        "server.properties",
        "banned-ips.json",
        "banned-players.json",
        "ops.json",
        "whitelist.json"
    );

    public NanoHTTPD.Response getBackups(final Map<String, String> ignoredParams) {
        // Get zipped Archived inside the /backups folder in the plugin directory
        File backupFolder = new File("backups");

        if (!backupFolder.exists()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        }

        File[] backups = backupFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
        if (backups == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{}");
        }

        JsonArray backupArray = new JsonArray();
        for (File backup : backups) {
            JsonObject backupObject = new JsonObject();
            backupObject.addProperty("name", backup.getName());
            backupObject.addProperty("size", Helper.formatSize(backup.length()));
            backupObject.addProperty("lastModified", Helper.dateConverter(backup.lastModified()));

            if (backup.getName().endsWith(".loading.zip")) {
                backupObject.addProperty("status", "in_progress"); // Indicate that the backup is still in progress
            } else {
                backupObject.addProperty("status", "completed"); // Indicate that the backup is complete
            }

            backupArray.add(backupObject);
        }

        JsonObject response = new JsonObject();
        response.add("backups", backupArray);

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    public NanoHTTPD.Response createBackup(final Map<String, String> params) {
        String name = params.get("name");
        if (name == null || name.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\": \"Name is required\"}");
        }

        File backupFolder = new File("backups");
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }

        File backupFile = new File(backupFolder, name + ".loading.zip");
        if (backupFile.exists()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\": \"Backup with that name already exists\"}");
        }

        // Get context from array backup_files in config.yml
        FileConfiguration config = MinecraftServerAPI.getInstance().getConfig();
        List<String> backupFiles = config.getStringList("backup_files");

        // Merge FILES_TO_BACKUP with backup_files
        List<String> filesToBackupList = new ArrayList<>(BackupAPI.filesToBackup);
        for (String file : backupFiles) {
            if (!filesToBackupList.contains(file)) {
                filesToBackupList.add(file);
            }
        }

        // Exclude the backups folder from being included in the backup
        filesToBackupList.remove(backupFolder.getAbsolutePath());

        // Create a new thread to create the backup
        Thread backupThread = new Thread(() -> {
            try (FileOutputStream fos = new FileOutputStream(backupFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (String filePath : filesToBackupList) {
                    File fileToZip = new File(filePath);
                    if (fileToZip.exists()) {
                        zipFile(fileToZip, fileToZip.getName(), zos, backupFolder.getAbsolutePath());
                    }
                }

                // Rename file to remove .loading
                File finalBackupFile = new File(backupFolder, name + ".zip");
                if (!(backupFile.renameTo(finalBackupFile))) {
                    Logger.error("Failed to rename backup file: " + backupFile.getName());
                }

                Logger.info("Backup created successfully: " + finalBackupFile.getName());

            } catch (IOException e) {
                Logger.error("Error creating backup: " + e.getMessage());
            }
        });
        backupThread.setName("BackupThread-" + name + "-" + backupThread.getId());

        backupThread.start();

        JsonObject response = new JsonObject();
        response.addProperty("message", "Backup creation started");
        response.addProperty("threadId", backupThread.getId());
        response.addProperty("threadName", backupThread.getName());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    private void zipFile(final File fileToZip, final String fileName, final ZipOutputStream zos, final String backupFolderPath) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                // Recursively zip files, but skip the backup folder itself
                if (!childFile.getAbsolutePath().startsWith(backupFolderPath)) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zos, backupFolderPath);
                }
            }
            return;
        }
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        }
    }

    public NanoHTTPD.Response deleteBackup(final Map<String, String> params) {
        String name = params.get("name");
        if (name == null || name.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\": \"Name is required\"}");
        }

        File backupFolder = new File("backups");
        File backupFile = new File(backupFolder, name + ".zip");
        if (!backupFile.exists()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{\"error\": \"Backup not found\"}");
        }

        if (!backupFile.delete()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{\"error\": \"Failed to delete backup\"}");
        }

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", "{\"message\": \"Backup deleted successfully\"}");
    }

    public NanoHTTPD.Response getStatus(final Map<String, String> params) {
        int threadId = Integer.parseInt(params.get("threadId"));

        if (threadId == 0) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\": \"Thread ID is required\"}");
        }

        Thread thread = Thread.getAllStackTraces().keySet().stream()
            .filter(t -> t.getId() == threadId)
            .findFirst()
            .orElse(null);

        if (thread == null) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{\"error\": \"Thread not found\"}");
        }

        JsonObject response = new JsonObject();
        response.addProperty("name", thread.getName());
        response.addProperty("state", thread.getState().name());
        response.addProperty("alive", thread.isAlive());
        response.addProperty("daemon", thread.isDaemon());
        response.addProperty("interrupted", thread.isInterrupted());
        response.addProperty("priority", thread.getPriority());

        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
    }

    public NanoHTTPD.Response getBackup(final Map<String, String> params) {
        String name = params.get("name");
        if (name == null || name.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\": \"Name is required\"}");
        }

        File backupFolder = new File("backups");
        File backupFile = new File(backupFolder, name + ".zip");
        if (!backupFile.exists()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{\"error\": \"Backup not found\"}");
        }

        try (ZipFile zipFile = new ZipFile(backupFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            JsonObject root = new JsonObject();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                addEntryToTree(root, entry);
            }

            JsonObject response = new JsonObject();
            response.addProperty("name", name);
            response.addProperty("size", Helper.formatSize(backupFile.length()));
            response.addProperty("created", Helper.dateConverter(backupFile.lastModified()));
            response.add("files", root);

            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", response.toString());
        } catch (IOException e) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{\"error\": \"Failed to read backup\"}");
        }
    }

    private void addEntryToTree(final JsonObject root, final ZipEntry entry) {
        String[] pathParts = entry.getName().split("/");
        JsonObject currentDir = root;

        for (int i = 0; i < pathParts.length; i++) {
            String part = pathParts[i];
            if (i == pathParts.length - 1 && !entry.isDirectory()) {
                JsonObject fileObject = new JsonObject();
                fileObject.addProperty("size", Helper.formatSize(entry.getSize()));
                fileObject.addProperty("compressedSize", Helper.formatSize(entry.getCompressedSize()));
                fileObject.addProperty("lastModified", Helper.dateConverter(entry.getLastModifiedTime().toMillis()));
                fileObject.addProperty("crc", entry.getCrc());
                fileObject.addProperty("comment", entry.getComment());
                currentDir.add(part, fileObject);
            } else {
                if (!currentDir.has(part)) {
                    currentDir.add(part, new JsonObject());
                }
                currentDir = currentDir.getAsJsonObject(part);
            }
        }
    }

    public NanoHTTPD.Response downloadBackup(final Map<String, String> params) {
        String name = params.get("name");
        if (name == null || name.isEmpty()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "application/json", "{\"error\": \"Name is required\"}");
        }

        File backupFolder = new File("backups");
        File backupFile = new File(backupFolder, name + ".zip");
        if (!backupFile.exists()) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "application/json", "{\"error\": \"Backup not found\"}");
        }

        try {
            FileInputStream fis = new FileInputStream(backupFile);
            NanoHTTPD.Response response = NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/zip", fis, backupFile.length());
            response.addHeader("Content-Disposition", "attachment; filename=\"" + backupFile.getName() + "\"");
            return response;
        } catch (IOException e) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "application/json", "{\"error\": \"Failed to read backup\"}");
        }
    }

}
