import org.junit.Ignore;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;

public class WhitelistAPITest extends ApiTestHelper {

    @Test
    @DisplayName("Test the GET /v1/whitelist endpoint")
    @Disabled
    public void testWhitelistEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/whitelist", "GET");
        Assertions.assertEquals(200, conn.getResponseCode());
    }

    @Test
    @DisplayName("Test the POST /v1/whitelist endpoint")
    public void testWhitelistAddEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/whitelist?username=Shweit", "POST");
        Assertions.assertEquals(200, conn.getResponseCode());
    }

    @Test
    @DisplayName("Test the DELETE /v1/whitelist endpoint")
    public void testWhitelistRemoveEndpoint() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/whitelist?username=Shweit", "DELETE");
        Assertions.assertEquals(200, conn.getResponseCode());
    }

    @Test
    @DisplayName("Test the POST /v1/whitelist/deactivate endpoint")
    public void testDeactivateWhitelist() throws IOException {
        HttpURLConnection conn = sendRequest("/v1/whitelist/deactivate", "POST");
        Assertions.assertEquals(200, conn.getResponseCode());
    }

}
