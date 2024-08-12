package com.shweit.serverapi.api.v1.endpoints;

import com.shweit.serverapi.api.v1.models.Server;
import com.shweit.serverapi.utils.ChatListener;
import com.shweit.serverapi.utils.Logger;
import io.javalin.http.Context;
import io.javalin.openapi.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The Server class provides endpoints to retrieve server information.
 *
 * @version 1.0
 * @since 1.0
 * @author Shweit
 */
public class ServerAPI {
    private final ChatListener chatListener;
    private final JavaPlugin plugin;

    public ServerAPI(ChatListener chatListener, JavaPlugin plugin) {
        this.chatListener = chatListener;
        this.plugin = plugin;
    }

    @OpenApi(
        path = "/v1/ping",
        methods = {HttpMethod.GET},
        summary = "Ping the server",
        description = "This endpoint returns a pong response.",
        tags = {"Server"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = String.class))
        }
    )
    public final void pingServer(Context ctx) {
        ctx.result("pong");
    }

    @OpenApi(
        path = "/v1/server",
        methods = {HttpMethod.GET},
        summary = "Get server information",
        description = "This endpoint returns information about the server.",
        tags = {"Server"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = Server.class))
        }
    )
    public final void getServerInfo(Context ctx) {
        Server server = new Server();
        server.setName(Bukkit.getServer().getName());
        server.setVersion(Bukkit.getServer().getVersion());
        server.setPlayerCount(Bukkit.getOnlinePlayers().size());
        server.setMaxPlayers(Bukkit.getServer().getMaxPlayers());
        server.setMotd(Bukkit.getServer().getMotd());
        server.setHealth(getHealth());
        server.setIcon(Bukkit.getServer().getServerIcon());

        ctx.json(server);
    }

    @OpenApi(
            path = "/v1/server/tps",
            methods = {HttpMethod.GET},
            summary = "Get server TPS",
            description = "This endpoint returns the server TPS.",
            tags = {"Server"},
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Double.class))
            }
    )
    public final void getServerTPS(Context ctx) {
        ctx.result(String.valueOf(calculateTPS()));
    }

    @OpenApi(
            path = "/v1/server/exec",
            methods = {HttpMethod.POST},
            summary = "Execute a command",
            description = "This endpoint executes a command on the server.",
            tags = {"Server"},
            requestBody = @OpenApiRequestBody(
                    required = true,
                    content = {
                            @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                                    @OpenApiContentProperty(name = "command", type = "string"),
                            }),
                    }),
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Double.class))
            }
    )
    public final void executeCommand(Context ctx) {
        String command = ctx.formParam("command");
        if (command == null || command.isEmpty()) {
            ctx.status(400).result("Missing command parameter");
            return;
        }

        Logger.info("Executing command: " + command);

        new BukkitRunnable() {
            @Override
            public void run() {
                // Capture the console output
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream oldOut = System.out;
                PrintStream oldErr = System.err;
                System.setOut(new PrintStream(new ConsoleInterceptor(outputStream)));
                System.setErr(new PrintStream(new ConsoleInterceptor(outputStream)));

                CommandSender sender = Bukkit.getConsoleSender();
                boolean success = Bukkit.dispatchCommand(sender, command);

                // Restore the original output streams
                System.setOut(oldOut);
                System.setErr(oldErr);

                if (success) {
                    ctx.status(200).result(outputStream.toString());
                } else {
                    ctx.status(500).result("Failed to execute command");
                }
            }
        }.runTask(plugin);
    }

    private static class CommandResult implements CommandSender {
        private final StringBuilder output = new StringBuilder();

        @Override
        public void sendMessage(String message) {
            output.append(message).append("\n");
        }

        @Override
        public void sendMessage(String[] messages) {
            for (String message : messages) {
                output.append(message).append("\n");
            }
        }

        public String getOutput() {
            return output.toString();
        }

        @Override
        public boolean isOp() {
            return true; // Ensure this sender has all permissions
        }

        @Override
        public void setOp(boolean value) {
            // Not used
        }

        // The rest of the required methods for CommandSender can be no-ops or simple implementations
        @Override
        public Spigot spigot() {
            return null;
        }

        @Override
        public org.bukkit.Server getServer() {
            return Bukkit.getServer();
        }

        @Override
        public String getName() {
            return "CommandResult";
        }

        @Override
        public boolean isPermissionSet(@NotNull String s) {
            return true;
        }

        @Override
        public boolean isPermissionSet(@NotNull Permission permission) {
            return false;
        }

        @Override
        public boolean hasPermission(String name) {
            return true;
        }

        @Override
        public boolean hasPermission(Permission perm) {
            return true;
        }

        @Override
        public void sendMessage(UUID uuid, String message) {
            sendMessage(message);
        }

        @Override
        public void sendMessage(UUID uuid, String[] messages) {
            sendMessage(messages);
        }

        @Override
        public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, int ticks) {
            return null;
        }

        @Override
        public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value) {
            return null;
        }

        @Override
        public void removeAttachment(PermissionAttachment attachment) {
            // Not used
        }

        @Override
        public void recalculatePermissions() {
            // Not used
        }

        @NotNull
        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            return Set.of();
        }

        @Override
        public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value, int ticks) {
            return null;
        }
    }

    @OpenApi(
        path = "/v1/server/chat",
        methods = {HttpMethod.GET},
        summary = "Get server chat",
        description = "This endpoint returns the server chat.",
        tags = {"Server"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = String.class))
        }
    )
    public final void getServerChat(Context ctx) {
        List<String> chatMessages = chatListener.getChatMessages();
        ctx.json(chatMessages);
    }

    @OpenApi(
        path = "/v1/server/logs",
        methods = {HttpMethod.GET},
        summary = "Get server logs",
        description = "This endpoint returns the server logs.",
        tags = {"Server"},
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = String.class))
        }
    )
    public final void getServerLogs(Context ctx) {
        // Get the server logs
        String logs = Bukkit.getServer().getLogger().toString();

        // Return the server logs
        ctx.result(logs);
    }

    @OpenApi(
        path = "/v1/server/broadcast",
        methods = {HttpMethod.POST},
        summary = "Broadcast a message",
        description = "This endpoint broadcasts a message to all players on the server.",
        tags = {"Server"},
        requestBody = @OpenApiRequestBody(
            required = true,
            content = {
                @OpenApiContent(mimeType = "application/x-www-form-urlencoded", properties = {
                    @OpenApiContentProperty(name = "message", type = "string"),
                }),
            }
        ),
        responses = {
            @OpenApiResponse(status = "200", content = @OpenApiContent(from = String.class))
        }
    )
    public final void broadcastMessage(Context ctx) {
        String message = ctx.formParam("message");
        if (message == null || message.isEmpty()) {
            ctx.status(400).result("Missing message parameter");
            return;
        }

        Bukkit.broadcastMessage(message);
        ctx.status(200).result("Message broadcasted");
    }

    private HashMap<String, String> getHealth() {
        HashMap<String, String> health = new HashMap<>();
        health.put("cpu", String.valueOf(Runtime.getRuntime().availableProcessors()));
        health.put("freeMemory", String.valueOf(Runtime.getRuntime().freeMemory()));
        health.put("maxMemory", String.valueOf(Runtime.getRuntime().maxMemory()));
        health.put("totalMemory", String.valueOf(Runtime.getRuntime().totalMemory()));
        health.put("uptime", String.valueOf(ManagementFactory.getRuntimeMXBean().getUptime() / 1000L));
        health.put("tps", String.valueOf(calculateTPS()));
        return health;
    }

    public double calculateTPS() {
        final int TICKS_SIZE = 600;
        final double MAX_TPS = 20.0;

        long[] ticks = new long[TICKS_SIZE];
        int tickCount = 0;

        ticks[tickCount % TICKS_SIZE] = System.currentTimeMillis();
        tickCount++;

        if (tickCount < 100) {
            return MAX_TPS;
        }
        int target = (tickCount - 1 - 100) % TICKS_SIZE;
        long elapsed = System.currentTimeMillis() - ticks[target];

        return 100 / (elapsed / 1000.0);
    }
}
