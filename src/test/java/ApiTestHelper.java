import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ApiTestHelper {
    public static final String BASE_URL = "http://localhost:7001";

    @Container
    public static DockerComposeContainer<?> compose =
            new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("mc_1", 25565);

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String readResponse(HttpURLConnection conn) throws IOException {
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    @BeforeAll
    public static void setUp() {
        buildPluginWithMaven();
        compose.start();

        // TODO: Add Check if the this is the first time the server is being started
        copyPluginToDockerContainer();
        reloadMinecraftServer();
    }

    @AfterAll
    public static void tearDown() {
        compose.stop();
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
            File pluginFile = new File("target/MinecraftServerAPI-1.0.jar");

            File destDir_1 = new File("src/test/resources/minecraft-test-server/plugins");

            FileUtils.copyFileToDirectory(pluginFile, destDir_1);


            File configFile = new File("src/test/resources/config.yml");

            File destDir_2 = new File("src/test/resources/minecraft-test-server/plugins/MinecraftServerAPI");

            FileUtils.copyFileToDirectory(configFile, destDir_2);

        } catch (IOException e) {
            throw new RuntimeException("Failed to copy plugin to Docker container directory", e);
        }
    }

    private static void reloadMinecraftServer() {
        try {
//            Logger.getLogger("org.testcontainers").log(Level.INFO, "Reloading Minecraft server");
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
                    logger.info("Response code: " + responseCode);

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

    private static void copyConfigToDockerContainer() {
        try {
            File configFile = new File("src/test/resources/config.yml");

            File destDir = new File("src/test/resources/minecraft-test-server/plugins/MinecraftServerAPI");

            FileUtils.copyFileToDirectory(configFile, destDir);

        } catch (IOException e) {
            throw new RuntimeException("Failed to copy config to Docker container directory", e);
        }
    }
}
