import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerAPITest extends ApiTestHelper {

    @Test
    public void testPingEndpoint() throws IOException {
        String urlString = ApiTestHelper.getBaseUrl() + "/v1/ping";
        URL url = new URL(urlString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        assertEquals(200, responseCode, "Expected response code to be 200");

        String response = readResponse(conn);
        assertNotNull(response, "Response should not be null");
        assertEquals("pong", response.trim(), "Expected response to be 'pong'");
    }
}
