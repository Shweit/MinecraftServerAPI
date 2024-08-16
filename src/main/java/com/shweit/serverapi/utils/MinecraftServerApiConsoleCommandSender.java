package com.shweit.serverapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

public class MinecraftServerApiConsoleCommandSender implements ConsoleCommandSender {
    public StringBuilder output = new StringBuilder();

    @Override
    public void sendMessage(String s) {
        output.append(s).append("\n");
    }

    @Override
    public void sendMessage(String... strings) {
        for (String s : strings) {
            output.append(s).append("\n");
        }
    }

    @Override
    public void sendMessage(UUID uuid, String s) {
        output.append(s).append("\n");
    }

    @Override
    public void sendMessage(UUID uuid, String... strings) {
        for (String s : strings) {
            output.append(s).append("\n");
        }
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return "MinecraftServerApi";
    }

    @Override
    public Spigot spigot() {
        return Bukkit.getConsoleSender().spigot();
    }

    @Override
    public boolean isConversing() {
        return false;
    }

    @Override
    public void acceptConversationInput(String s) {

    }

    @Override
    public boolean beginConversation(Conversation conversation) {
        return false;
    }

    @Override
    public void abandonConversation(Conversation conversation) {

    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent) {

    }

    @Override
    public void sendRawMessage(String s) {

    }

    @Override
    public void sendRawMessage(UUID uuid, String s) {

    }

    @Override
    public boolean isPermissionSet(String s) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return false;
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return false;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return Set.of();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean b) {

    }
}
