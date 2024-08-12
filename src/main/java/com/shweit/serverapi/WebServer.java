package com.shweit.serverapi;

import com.shweit.serverapi.utils.Logger;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.openapi.OpenApiContact;
import io.javalin.openapi.OpenApiLicense;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class WebServer {

    private final Javalin javalin;

    private final FileConfiguration bukkitConfig;

    public WebServer(FileConfiguration bukkitConfig) {
        this.bukkitConfig = bukkitConfig;
        javalin = Javalin.create(this::configureJavalin);
        Logger.info("Web server started!");
    }

    private void configureJavalin(JavalinConfig config) {
        if (bukkitConfig.getBoolean("authentication")) {
            if (Objects.equals(bukkitConfig.getString("authentication_key"), "CHANGE_ME")) {
                Logger.error("Please change the authentication key in the config.yml file!");
                Logger.warning("As long as the authentication key is not changed, the server will not start!");
                javalin.stop();
            }

            // Set the access manager to check for the authentication key only for the endpoints, not for the swagger docs
            config.accessManager((handler, ctx, permittedRoles) -> {
                String path = ctx.path();

                // Allow access to Swagger UI and related resources
                if (path.startsWith("/swagger") || path.startsWith("/webjars")){
                    handler.handle(ctx);
                    return;
                }

                // Authorization check
                String key = ctx.header("auth");
                if (key == null || !key.equals(bukkitConfig.getString("authentication_key"))) {
                    ctx.status(401).result("Unauthorized");
                    return;
                }

                handler.handle(ctx);
            });
        } else {
            Logger.warning("Authentication is disabled. It is recommended to enable authentication in the config.yml file!");
        }

        config.http.defaultContentType = "application/json";
        config.showJavalinBanner = false;

        config.plugins.register(new OpenApiPlugin(getOpenApiConfiguration()));

        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
        swaggerConfiguration.setDocumentationPath("/swagger-docs");
        config.plugins.register(new SwaggerPlugin(swaggerConfiguration));
    }

    private OpenApiPluginConfiguration getOpenApiConfiguration() {
        OpenApiLicense license = new OpenApiLicense();
        license.setName("MIT License");
        license.setUrl("https://opensource.org/licenses/MIT");

        OpenApiContact contact = new OpenApiContact();
        contact.setName("Dennis van den Brock aka Shweit");
        contact.setEmail("dennisvandenbrock54@gmail.com");

        return new OpenApiPluginConfiguration()
            .withDocumentationPath("/swagger-docs")
            .withDefinitionConfiguration((version, definition) -> {
                definition.withOpenApiInfo((openApiInfo) -> {
                    openApiInfo.setTitle("Minecraft Server API");
                    openApiInfo.setVersion("1.0.0");
                    openApiInfo.setDescription("This API provides a comprehensive interface for managing and retrieving information from your Minecraft server. It allows you to ping the server, gather detailed server data, and manage players. You can easily view all players, fetch individual player details, and manage both Java and Bedrock players on the whitelist. \nFor advanced functionalities, the API supports checking the installation status of essential plugins like Floodgate and Geyser. It also provides endpoints to manage the Floodgate whitelist, ensuring seamless integration of Bedrock players. \n\nTo utilize the maintenance features, ensure the [Maintenance plugin](https://www.spigotmc.org/resources/maintenance-bungee-and-spigot-support.40699/) is installed. For Floodgate-related operations, both [Floodgate](https://geysermc.org/download#floodgate) and [GeyserMC](https://geysermc.org/download#geyser) plugins are required. Designed to enhance server management efficiency, this API offers all necessary tools to keep your server running smoothly and provide an optimal gaming experience.");
                    openApiInfo.setLicense(license);
                    openApiInfo.setSummary("Comprehensive API for Minecraft Server Management");
                    openApiInfo.setContact(contact);
                });
            });

    }

    public void start(int port) {
        javalin.start(port);
    }

    public Javalin getJavalin() {
        return javalin;
    }

    public void stop() {
        javalin.stop();
    }

    public void stopAndWait() {
        javalin.stop();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopAndWait(int milliseconds) {
        javalin.stop();
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void get(String path, Handler handler) {
        javalin.addHandler(HandlerType.GET, path, handler);
    }

    public void post(String path, Handler handler) {
        javalin.addHandler(HandlerType.POST, path, handler);
    }

    public void put(String path, Handler handler) {
        javalin.addHandler(HandlerType.PUT, path, handler);
    }

    public void delete(String path, Handler handler) {
        javalin.addHandler(HandlerType.DELETE, path, handler);
    }
}
