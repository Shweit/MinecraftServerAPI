package com.shweit.serverapi;

import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.utils.RouteDefinition;
import fi.iki.elonen.NanoHTTPD;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class WebServer extends NanoHTTPD {
    private final boolean isAuthenticated;
    private final String authKey;
    private final List<RouteDefinition> routes = new ArrayList<>();

    public WebServer(final int port, final boolean authenticationEnabled, final String authenticationKey) {
        super(port);
        this.isAuthenticated = authenticationEnabled;
        this.authKey = authenticationKey;
    }

    @Override
    public Response serve(final IHTTPSession session) {
        String uri = session.getUri();
        NanoHTTPD.Method method = session.getMethod();
        Map<String, String> params = new HashMap<>();
        boolean swaggerDocumentation = MinecraftServerAPI.config.getBoolean("swagger", true);

        Logger.debug("Received request for: " + uri + " with method: " + method);

        // Handle Swagger documentation and static files
        Response swaggerResponse = handleSwaggerDocumentation(uri, session, swaggerDocumentation);
        if (swaggerResponse != null) {
            return swaggerResponse;
        }

        // Handle authentication
        if (isAuthenticated && !isAllowedPath(uri, swaggerDocumentation)) {
            Response authResponse = handleAuthentication(session, uri);
            if (authResponse != null) {
                return authResponse;
            }
        }

        // Extract query parameters
        extractQueryParams(session, params);

        // Match routes and return appropriate response
        return handleRouteMatching(uri, method, params);
    }

    // Method to handle Swagger documentation and static files
    private Response handleSwaggerDocumentation(String uri, IHTTPSession session, boolean swaggerDocumentation) {
        if (swaggerDocumentation) {
            if ("/api-docs".equalsIgnoreCase(uri)) {
                InputStream apiSpecStream = getClass().getResourceAsStream("/api.yaml");
                if (apiSpecStream != null) {
                    return newChunkedResponse(Response.Status.OK, "application/yaml", apiSpecStream);
                } else {
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "API documentation not found");
                }
            }

            if (uri.equalsIgnoreCase("/") || uri.startsWith("/swagger")) {
                if ("/".equals(uri)) {
                    uri = "/index.html"; // Redirect to the main Swagger UI page
                }
                InputStream resourceStream = getClass().getResourceAsStream("/swagger" + uri);
                if (resourceStream != null) {
                    String mimeType = determineMimeType(uri);
                    return newChunkedResponse(Response.Status.OK, mimeType, resourceStream);
                } else {
                    Logger.debug("Resource not found: " + uri);
                    return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Route not Found");
                }
            }
        }
        return null;
    }

    // Method to handle authentication
    private Response handleAuthentication(IHTTPSession session, String uri) {
        String authHeader = session.getHeaders().get("authorization");
        if (authHeader == null || !authHeader.equals(authKey)) {
            Logger.debug("Unauthorized request for: " + uri);
            return newFixedLengthResponse(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Unauthorized");
        }
        return null;
    }

    // Method to check if a path is allowed without authentication
    private boolean isAllowedPath(String uri, boolean swaggerDocumentation) {
        List<String> allowedPaths = swaggerDocumentation ? List.of(
                "/", "/swagger-ui-bundle.js", "/swagger-ui.css", "/api-docs", "/index.css",
                "/searchPlugin.js", "/swagger-ui-standalone-preset.js", "/swagger-initializer.js",
                "/favicon-32x32.png", "/swagger-ui.css.map", "/favicon-16x16.png"
        ) : List.of();

        return allowedPaths.stream().anyMatch(path -> uri.equals(path) || uri.startsWith("/swagger"));
    }

    // Method to extract query parameters
    private void extractQueryParams(IHTTPSession session, Map<String, String> params) {
        if (session.getQueryParameterString() != null) {
            session.getParameters().forEach((key, value) -> params.put(key, value.get(0)));
        }
    }

    // Method to handle route matching
    private Response handleRouteMatching(String uri, NanoHTTPD.Method method, Map<String, String> params) {
        for (RouteDefinition route : routes) {
            if (route.matches(uri, method, params)) {
                return route.getHandler().apply(params);
            }
        }
        Logger.debug("No route found for: " + uri + " with method: " + method);
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }


    // Method to determine MIME type
    private String determineMimeType(final String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".yaml")) return "application/yaml";
        return "text/plain";
    }

    public void addRoute(final NanoHTTPD.Method method, final String routePattern, final Function<Map<String, String>, Response> handler) {
        routes.add(new RouteDefinition(method, routePattern, handler));
    }
}
