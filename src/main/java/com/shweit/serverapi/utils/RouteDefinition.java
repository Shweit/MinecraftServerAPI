package com.shweit.serverapi.utils;

import fi.iki.elonen.NanoHTTPD;

import java.util.Map;
import java.util.function.Function;

public final class RouteDefinition {
    private final String routePattern;
    private final NanoHTTPD.Method httpMethod;
    private final Function<Map<String, String>, NanoHTTPD.Response> handler;

    public RouteDefinition(final NanoHTTPD.Method method, final String pattern, final Function<Map<String, String>, NanoHTTPD.Response> routeHandler) {
        this.routePattern = pattern;
        this.httpMethod = method;
        this.handler = routeHandler;
    }

    public String getRoutePattern() {
        return routePattern;
    }

    public NanoHTTPD.Method getHttpMethod() {
        return httpMethod;
    }

    public Function<Map<String, String>, NanoHTTPD.Response> getHandler() {
        return handler;
    }

    public boolean matches(final String uri, final NanoHTTPD.Method method, final Map<String, String> params) {
        if (!this.httpMethod.equals(method)) {
            return false;
        }

        String[] patternParts = routePattern.split("/");
        String[] uriParts = uri.split("/");

        if (patternParts.length != uriParts.length) {
            return false;
        }

        for (int i = 0; i < patternParts.length; i++) {
            String patternPart = patternParts[i];
            String uriPart = uriParts[i];

            if (patternPart.startsWith("{") && patternPart.endsWith("}")) {
                String paramName = patternPart.substring(1, patternPart.length() - 1); // Entferne die geschweiften Klammern
                params.put(paramName, uriPart);
            } else if (!patternPart.equals(uriPart)) {
                return false;
            }
        }

        return true;
    }
}
