import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ApiTestHelper {

    protected static final DockerComposeContainer<?> compose = SharedDockerComposeContainer.getInstance();
    public static final String BASE_URL = "http://localhost:7001";

    public static String getBaseUrl() {
        return BASE_URL;
    }

    @BeforeAll
    public void setUp() {
        buildPluginWithMaven();
        copyPluginToDockerContainer();
        reloadMinecraftServer();
    }

    @AfterAll
    public void tearDown() {
        // Reset the Minecraft server to its original state
        try {
            File pluginFile = new File("src/test/resources/server-data/server.properties");
            File destDir_1 = new File("src/test/resources/minecraft-test-server/");
            FileUtils.copyFileToDirectory(pluginFile, destDir_1);

            File whitelistFile = new File("src/test/resources/server-data/whitelist.json");
            File destDir_2 = new File("src/test/resources/minecraft-test-server/");
            FileUtils.copyFileToDirectory(whitelistFile, destDir_2);

        } catch (IOException e) {
            throw new RuntimeException("Failed to copy server properties to Docker container directory", e);
        }

        try {
            org.testcontainers.containers.Container.ExecResult result = compose.getContainerByServiceName("mc_1").get().execInContainer("whitelist", "on");
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Docker command to enable Whitelist", e);
        }
    }

    private static void buildPluginWithMaven() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("mvn", "clean", "package", "-DskipTests");
            processBuilder.directory(new File("."));
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Maven build failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build plugin with Maven", e);
        }
    }

    private static void copyPluginToDockerContainer() {
        try {
            // Plugin file
            File pluginFile = new File("target/MinecraftServerAPI-1.0.jar");
            File destDir_1 = new File("src/test/resources/minecraft-test-server/plugins");
            FileUtils.copyFileToDirectory(pluginFile, destDir_1);

            // Config file
            File configFile = new File("src/test/resources/server-data/config.yml");
            File destDir_2 = new File("src/test/resources/minecraft-test-server/plugins/MinecraftServerAPI");
            FileUtils.copyFileToDirectory(configFile, destDir_2);

            // Citizens plugin
            File citizensFile = new File("src/test/resources/server-data/plugins/Citizens-2.0.35-b3535.jar");
            File destDir_3 = new File("src/test/resources/minecraft-test-server/plugins");
            FileUtils.copyFileToDirectory(citizensFile, destDir_3);

            // Maintenance plugin
            File maintenanceFile = new File("src/test/resources/server-data/plugins/Maintenance-4.2.1.jar");
            File destDir_4 = new File("src/test/resources/minecraft-test-server/plugins");
            FileUtils.copyFileToDirectory(maintenanceFile, destDir_4);

            // server.properties file
            File serverPropertiesFile = new File("src/test/resources/server-data/server.properties");
            File destDir_5 = new File("src/test/resources/minecraft-test-server/");
            FileUtils.copyFileToDirectory(serverPropertiesFile, destDir_5);

            //whitelist.json file
            File whitelistFile = new File("src/test/resources/server-data/whitelist.json");
            File destDir_6 = new File("src/test/resources/minecraft-test-server/");
            FileUtils.copyFileToDirectory(whitelistFile, destDir_6);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy plugin to Docker container directory", e);
        }

        try {
            org.testcontainers.containers.Container.ExecResult result = compose.getContainerByServiceName("mc_1").get().execInContainer("whitelist", "on");
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Docker command to enable Whitelist", e);
        }
    }

    private static void reloadMinecraftServer() {
        try {
            org.testcontainers.containers.Container.ExecResult result = compose.getContainerByServiceName("mc_1").get().execInContainer("rcon-cli", "reload confirm");
            int exitCode = result.getExitCode();
            if (exitCode != 0) {
                throw new RuntimeException("Failed to reload Minecraft server using Docker exec");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Docker command to reload Minecraft server", e);
        }

        // Wait for the Minecraft Server to reload
        try {
            boolean isReady = false;
            int attempts = 0;

            Logger logger = Logger.getLogger("org.testcontainers");

            while (!isReady && attempts < 30) { // max 30 attempts * 1 second = 30 seconds
                Thread.sleep(1000);
                try {
                    URL url = new URL(getBaseUrl() + "/v1/ping");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", "TestKey");
                    conn.setRequestProperty("accept", "*/*");
                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        isReady = true;
                    }

                    conn.disconnect(); // Verbindung schließen

                } catch (Exception e) {
                    logger.warning("Exception during server check: " + e.getMessage());
                    // Continue to retry while the server is still starting
                }

                attempts++;
            }

            if (!isReady) {
                throw new RuntimeException("Minecraft server did not reload successfully");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for Minecraft server to reload", e);
        }
    }

    public static HttpURLConnection sendRequest(String endpoint, String method) throws IOException {
        URL url = new URL(getBaseUrl() + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", "TestKey");
        return conn;
    }

    public static String readResponse(HttpURLConnection conn) throws IOException {
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
