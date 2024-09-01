import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

public class SharedDockerComposeContainer {

    private static DockerComposeContainer<?> instance;

    private SharedDockerComposeContainer() {
        // Private constructor to prevent instantiation
    }

    public static DockerComposeContainer<?> getInstance() {
        if (instance == null) {
            instance = new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("mc_1", 25565)
                    .withExposedService("mc_1", 7001);
            instance.start();
        }
        return instance;
    }
}
