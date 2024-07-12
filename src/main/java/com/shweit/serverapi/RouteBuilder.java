package com.shweit.serverapi;

import io.javalin.http.Handler;

public class RouteBuilder {
    private final String basePath;
    private final WebServer app;

    public RouteBuilder(String basePath, WebServer app) {
        this.basePath = basePath;
        this.app = app;
    }

    public void get(String path, Handler handler) {
        app.get(basePath + "/" + path, handler);
    }

    public void post(String path, Handler handler) {
        app.post(basePath + "/" + path, handler);
    }

    public void put(String path, Handler handler) {
        app.put(basePath + "/" + path, handler);
    }

    public void delete(String path, Handler handler) {
        app.delete(basePath + "/" + path, handler);
    }
}
