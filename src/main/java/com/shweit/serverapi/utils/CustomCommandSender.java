// CustomCommandSender.java
package com.shweit.serverapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class CustomCommandSender implements CommandSender {
    private final ConsoleCommandSender console;

    public CustomCommandSender() {
        this.console = Bukkit.getServer().getConsoleSender();
    }

    @Override
    public void sendMessage(String message) {
        console.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        console.sendMessage(messages);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
        console.sendMessage(uuid, s);
    }

    @Override
    public void sendMessage(@Nullable UUID uuid, @NotNull String... strings) {
        console.sendMessage(uuid, strings);
    }

    @Override
    public org.bukkit.@NotNull Server getServer() {
        return console.getServer();
    }

    @Override
    public @NotNull String getName() {
        return console.getName();
    }

    @Override
    public @NotNull Spigot spigot() {
        return console.spigot();
    }

    @Override
    public boolean isPermissionSet(String name) {
        return console.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return console.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(String name) {
        return console.hasPermission(name);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return console.hasPermission(perm);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return console.addAttachment(plugin, name, value);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(Plugin plugin) {
        return console.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return console.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return console.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        console.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        console.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return console.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return console.isOp();
    }

    @Override
    public void setOp(boolean value) {
        console.setOp(value);
    }
}