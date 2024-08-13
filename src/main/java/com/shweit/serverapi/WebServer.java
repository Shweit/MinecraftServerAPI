package com.shweit.serverapi;

import fi.iki.elonen.NanoHTTPD;
import java.io.InputStream;

public class WebServer extends NanoHTTPD {
    private final boolean authenticationEnabled;
    private final String authenticationKey;

    public WebServer(int port, boolean authenticationEnabled, String authenticationKey) {
        super(port);
        this.authenticationEnabled = authenticationEnabled;
        this.authenticationKey = authenticationKey;
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();

        // Ausnahme für den Root-Pfad und statische Swagger-Dateien von der Authentifizierung
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
        if (uri.equalsIgnoreCase("/") || uri.startsWith("/")) {
            if ("/".equals(uri)) {
                uri = "/index.html"; // Redirect to the main Swagger UI page
            }
            InputStream resourceStream = getClass().getResourceAsStream("/swagger" + uri);
            if (resourceStream != null) {
                String mimeType = determineMimeType(uri);
                return newChunkedResponse(Response.Status.OK, mimeType, resourceStream);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
            }
        }

        // Default response
        return newFixedLengthResponse("Hello, this is a response from the Minecraft Plugin WebServer!");
    }

    // Custom method to determine MIME type
    private String determineMimeType(String uri) {
        if (uri.endsWith(".html")) return "text/html";
        if (uri.endsWith(".css")) return "text/css";
        if (uri.endsWith(".js")) return "application/javascript";
        if (uri.endsWith(".yaml")) return "application/yaml";
        return "text/plain";
    }
}
