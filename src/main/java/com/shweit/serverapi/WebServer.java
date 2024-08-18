package com.shweit.serverapi;

import com.shweit.serverapi.utils.Logger;
import com.shweit.serverapi.utils.RouteDefinition;
import fi.iki.elonen.NanoHTTPD;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Logger.debug("Received request for: " + uri + " with method: " + method);

        // Define Map of allowed origins
        List<String> allowedPaths = List.of(
                "/", "/swagger", "/api-docs", "/favicon", "/index.css", "/searchPlugin.js"
        );
        boolean isAllowedPath = allowedPaths.stream().anyMatch(uri::startsWith);

        // Exception for the root path, swagger files and /api-docs from authentication
        if (!isAllowedPath && isAuthenticated) {
            Logger.debug("Checking authentication for: " + uri);
            String authHeader = session.getHeaders().get("authorization");
            if (authHeader == null || !authHeader.equals(authKey)) {
                Logger.debug("Unauthorized request for: " + uri);
                return newFixedLengthResponse(Response.Status.UNAUTHORIZED, MIME_PLAINTEXT, "Unauthorized");
            }
        }

        // Serve OpenAPI spec
        if ("/api-docs".equalsIgnoreCase(uri)) {
            InputStream apiSpecStream = getClass().getResourceAsStream("/api.yaml");
            if (apiSpecStream != null) {
                return newChunkedResponse(Response.Status.OK, "application/yaml", apiSpecStream);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "API documentation not found");
            }
        }

        // Serve Swagger UI files on root path
        if (uri.equalsIgnoreCase("/") || uri.startsWith("/swagger") || isAllowedPath) {
            if ("/".equals(uri)) {
                uri = "/index.html"; // Redirect to the main Swagger UI page
            }
            InputStream resourceStream = getClass().getResourceAsStream("/swagger" + uri);
            if (resourceStream != null) {
                String mimeType = determineMimeType(uri);
                return newChunkedResponse(Response.Status.OK, mimeType, resourceStream);
            } else {
                Logger.debug("Resource not found: " + uri);
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
            }
        }

        // Extract query parameters and add them to the params map
        if (session.getQueryParameterString() != null) {
            session.getParameters().forEach((key, value) -> params.put(key, value.get(0)));
        }

        for (RouteDefinition route : routes) {
            if (route.matches(uri, method, params)) {
                return route.getHandler().apply(params);
            }
        }

        Logger.debug("No route found for: " + uri + " with method: " + method);

        // Default response
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
