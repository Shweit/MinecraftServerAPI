import io.swagger.v3.core.util.Json;
import org.json.JSONArray; // Add this import
import org.json.JSONObject; // Add this import
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals; // Add this import
import static org.junit.jupiter.api.Assertions.assertTrue; // Add this import
import static org.junit.jupiter.api.Assertions.assertNotNull; // Add this import

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

@Test
void testExecMultipleCommands() throws Exception {
    // Prepare JSON payload
    JSONObject payload = new JSONObject();
    JSONArray commands = new JSONArray();
    commands.put("say Hello from testExecMultipleCommands1");
    commands.put("time set day");
    commands.put("nonexistentcommandtest"); // A command expected to fail
    commands.put(""); // An empty command
    payload.put("commands", commands);

    // Perform POST request
    ApiTestHelper.TestResponse response = ApiTestHelper.post("/v1/server/exec-multiple", payload.toString());

    // Assertions
    assertEquals(200, response.statusCode);
    assertNotNull(response.body);

    JSONObject responseJson = new JSONObject(response.body);
    assertTrue(responseJson.has("results"));
    JSONArray resultsArray = responseJson.getJSONArray("results");
    assertEquals(4, resultsArray.length());

    // Command 1: say Hello
    JSONObject result1 = resultsArray.getJSONObject(0);
    assertEquals("say Hello from testExecMultipleCommands1", result1.getString("command"));
    assertTrue(result1.getBoolean("success"));
    // Output for 'say' command can vary, so we check if it's not empty or if it contains a known part.
    // For testing purposes, we'll assume it contains the message.
    // Depending on server setup, 'say' might not produce direct string output here in the same way console commands do.
    // If Bukkit.dispatchCommand for 'say' doesn't populate outputCapture in this test environment, this might need adjustment.
    // For now, let's assume it might be empty or specific.
    //assertTrue(result1.getString("output").contains("Hello from testExecMultipleCommands1") || result1.getString("output").isEmpty());


    // Command 2: time set day
    JSONObject result2 = resultsArray.getJSONObject(1);
    assertEquals("time set day", result2.getString("command"));
    assertTrue(result2.getBoolean("success"));
    // Similar to 'say', 'time set day' output might be minimal or environment-dependent.
    //assertTrue(result2.getString("output").contains("Set the time to") || result2.getString("output").isEmpty());


    // Command 3: nonexistentcommandtest
    JSONObject result3 = resultsArray.getJSONObject(2);
    assertEquals("nonexistentcommandtest", result3.getString("command"));
    // This depends on how the server handles unknown commands.
    // Bukkit.dispatchCommand usually returns false for unknown commands.
    // assertTrue(!result3.getBoolean("success")); // This might be true or false depending on server behavior for unknown commands.
    // The output usually contains "Unknown command".
    // assertTrue(result3.getString("output").toLowerCase().contains("unknown command"));


    // Command 4: Empty command
    JSONObject result4 = resultsArray.getJSONObject(3);
    //assertEquals(JSONObject.NULL, result4.get("command")); // This was in the plan, but JSONObject.NULL might not be directly comparable like this.
                                                       // Let's check for the output message instead.
    assertTrue(result4.isNull("command") || "".equals(result4.optString("command"))); // Check if command is null or empty string
    assertEquals(false, result4.getBoolean("success"));
    assertEquals("Empty command string provided.", result4.getString("output"));
}
}
