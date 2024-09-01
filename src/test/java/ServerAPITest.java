import io.swagger.v3.core.util.Json;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerAPITest extends ApiTestHelper {
    @Test
    @DisplayName("Test the GET /v1/ping endpoint")
    public void testPingEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/ping", "GET");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        assertEquals("pong", response);
    }

    @Test
    @DisplayName("Test the GET /v1/server endpoint")
    public void testServerEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server", "GET");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertEquals("A Minecraft Server", jsonResponse.getString("motd"));
        assertEquals("minecraft:normal", jsonResponse.getString("worldType"));
        assertEquals(20, jsonResponse.getInt("maxPlayers"));
        assertEquals("SURVIVAL", jsonResponse.getString("defaultGameMode"));
    }

    @Test
    @DisplayName("Test the GET /v1/server/health endpoint")
    public void testServerHealthEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/health", "GET");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertTrue(jsonResponse.has("usedMemory"));
        assertTrue(jsonResponse.has("availableProcessors"));
        assertTrue(jsonResponse.has("threadCount"));
        assertTrue(jsonResponse.has("uptime"));
        assertTrue(jsonResponse.has("tps"));
    }

    @Test
    @DisplayName("Test the GET /v1/server/tps endpoint")
    public void testServerTPSEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/tps", "GET");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertTrue(jsonResponse.has("tps"));
    }

    @Test
    @DisplayName("Test the GET /v1/server/uptime endpoint")
    public void testServerUptimeEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/uptime", "GET");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertTrue(jsonResponse.has("uptime"));
    }

    @Test
    @DisplayName("Test the GET /v1/server/properties endpoint")
    public void testServerPropertiesEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/properties", "GET");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertEquals("survival", jsonResponse.getString("gamemode"));
        assertEquals("A Minecraft Server", jsonResponse.getString("motd"));
        assertEquals("25565", jsonResponse.getString("server-port"));
    }

    @Test
    @DisplayName("Test the POST /v1/server/properties endpoint")
    public void testUpdateServerPropertiesEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/properties?key=motd&value=Test", "POST");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertEquals("Property updated successfully.", jsonResponse.getString("message"));

        // Check if the property was updated
        conn = sendRequest("/v1/server/properties", "GET");
        assertEquals(200, conn.getResponseCode());

        response = ApiTestHelper.readResponse(conn);
        jsonResponse = new JSONObject(response);

        assertEquals("Test", jsonResponse.getString("motd"));
    }

    @Test
    @DisplayName("Test the POST /v1/server/exec endpoint")
    public void testServerExecEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/exec?command=help", "POST");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertTrue(jsonResponse.getBoolean("success"));
    }

    @Test
    @DisplayName("Test the POST /v1/server/reload endpoint")
    public void testServerReloadEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/reload", "POST");
        assertEquals(200, conn.getResponseCode());
    }

    @Test
    @DisplayName("Test the POST /v1/server/broadcast endpoint")
    public void testServerBroadcastEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/broadcast?message=Test", "POST");
        assertEquals(200, conn.getResponseCode());
    }

    @Test
    @DisplayName("Test the GET /v1/server/chat endpoint")
    public void testServerChatEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/chat", "GET");
        assertEquals(200, conn.getResponseCode());
    }

    @Test
    @DisplayName("Test the GET /v1/server/log endpoint")
    public void testServerLogEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/server/log", "GET");
        assertEquals(200, conn.getResponseCode());

        String response = ApiTestHelper.readResponse(conn);
        JSONObject jsonResponse = new JSONObject(response);

        assertTrue(jsonResponse.has("log"));
    }
}
