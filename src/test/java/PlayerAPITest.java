import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerAPITest extends ApiTestHelper {

    @Test
    public void testPlayersEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/players", "GET");
        String response = ApiTestHelper.readResponse(conn);

        assertEquals(200, conn.getResponseCode());
        assertNotNull(response);

        JSONObject jsonResponse = new JSONObject(response);

        assertTrue(jsonResponse.has("onlinePlayers"));
        assertEquals(0, jsonResponse.getJSONArray("onlinePlayers").length());
    }

    @Test
    @Disabled
    public void testPlayerEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/player/Shweit", "GET");
        assertEquals(404, conn.getResponseCode());
    }
}
