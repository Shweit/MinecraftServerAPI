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
import java.util.function.Supplier;

public class WebServer extends NanoHTTPD {
    private final boolean authenticationEnabled;
    private final String authenticationKey;
    private final List<RouteDefinition> routes = new ArrayList<>();

    public WebServer(int port, boolean authenticationEnabled, String authenticationKey) {
        super(port);
        this.authenticationEnabled = authenticationEnabled;
        this.authenticationKey = authenticationKey;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        NanoHTTPD.Method method = session.getMethod();
        Map<String, String> params = new HashMap<>();

        Logger.debug("Received request for: " + uri + " with method: " + method);

        // Ausnahme für den Root-Pfad, Swagger-Dateien und /api-docs von der Authentifizierung
        if (!uri.equals("/") && !uri.startsWith("/swagger") && !uri.startsWith("/api-docs") && authenticationEnabled) {
            String authHeader = session.getHeaders().get("authorization");
            if (authHeader == null || !authHeader.equals(authenticationKey)) {
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
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
            }
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
    private String determineMimeType(String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".yaml")) return "application/yaml";
        return "text/plain";
    }

    public void addRoute(NanoHTTPD.Method method, String routePattern, Function<Map<String, String>, Response> handler) {
        routes.add(new RouteDefinition(method, routePattern, handler));
    }
}