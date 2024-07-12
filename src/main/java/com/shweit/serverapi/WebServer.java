package com.shweit.serverapi;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.OpenApiPluginConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerConfiguration;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;

public class WebServer {

    private final Javalin javalin;

    public WebServer() {
        javalin = Javalin.create(this::configureJavalin);
    }

    private void configureJavalin(JavalinConfig config) {
        config.http.defaultContentType = "application/json";
        config.showJavalinBanner = false;

        config.plugins.register(new OpenApiPlugin(getOpenApiConfiguration()));

        SwaggerConfiguration swaggerConfiguration = new SwaggerConfiguration();
        swaggerConfiguration.setDocumentationPath("/swagger-docs");
        config.plugins.register(new SwaggerPlugin(swaggerConfiguration));
    }

    private OpenApiPluginConfiguration getOpenApiConfiguration() {
        return new OpenApiPluginConfiguration()
                .withDocumentationPath("/swagger-docs")
                .withDefinitionConfiguration((version, definition) -> definition
                        .withOpenApiInfo((openApiInfo) -> {
                            openApiInfo.setTitle("Minecraft Server API");
                            openApiInfo.setVersion("1.0.0");
                            openApiInfo.setDescription("API for Minecraft server");
                        }));
    }

    public void start(int port) {
        javalin.start(port);
    }

    public void stop() {
        javalin.stop();
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
