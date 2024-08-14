package com.shweit.serverapi.endpoints.v1;

import fi.iki.elonen.NanoHTTPD;

import java.util.Map;

public class ServerAPI {
    public NanoHTTPD.Response ping(Map<String, String> params) {
        return NanoHTTPD.newFixedLengthResponse("pong");
    }
}
