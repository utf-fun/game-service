package org.readutf.lobby;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.readutf.buildformat.common.exception.BuildFormatException;
import org.readutf.buildformat.common.meta.BuildMetaStore;
import org.readutf.buildformat.common.schematic.BuildSchematicStore;
import org.readutf.buildformat.s3.S3BuildSchematicStore;
import org.readutf.buildformat.sql.SQLMetaStore;
import org.readutf.gameservice.client.GameServiceClient;
import org.readutf.gameservice.client.platform.KubernetesResolver;
import org.readutf.lobby.build.LobbyBuild;
import org.readutf.lobby.build.LobbyBuildManager;
import org.readutf.lobby.commands.GamemodeCommand;
import org.readutf.lobby.listeners.AsyncConfigListener;
import org.readutf.lobby.listeners.BuildPrevention;
import org.readutf.lobby.listeners.SpawnListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Properties;

public class Lobby {

    private static final Logger log = LoggerFactory.getLogger(Lobby.class);

    public Lobby() throws BuildFormatException, IOException {

        MinecraftServer server = MinecraftServer.init();

        HikariDataSource dataSource = getDatasource();
        S3AsyncClient s3Client = getAwsClient();

        BuildMetaStore metaStore = SQLMetaStore.createMetaStore(dataSource);
        BuildSchematicStore schematicStore = new S3BuildSchematicStore(s3Client, "utf-builds");

        LobbyBuildManager buildManager = new LobbyBuildManager(metaStore, schematicStore);
        LobbyBuild lobbyBuild = buildManager.loadBuild();

        MinecraftServer.getCommandManager().register(
                new GamemodeCommand()
        );

        server.start("0.0.0.0", 25565);

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, new AsyncConfigListener(lobbyBuild));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, new SpawnListener(lobbyBuild));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockBreakEvent.class, BuildPrevention.breakPrevention());
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockPlaceEvent.class, BuildPrevention.placePrevention());

        GameServiceClient client = new GameServiceClient.Builder(new KubernetesResolver())
                .setCapacitySupplier(() -> MinecraftServer.getConnectionManager().getOnlinePlayers().size() / 500f)
                .setTags(List.of("lobby", "main-lobby"))
                .build();

        new Thread(() -> {
            client.startBlocking(new InetSocketAddress(System.getenv("DISCOVERY_HOST"), 50052));
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                onlinePlayer.sendMessage(Lang.SHUTDOWN_WARNING);
            }

            while (!MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }));
    }

    private static S3AsyncClient getAwsClient() {

        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        String endpoint = System.getenv("AWS_ENDPOINT");
        String region = System.getenv("AWS_REGION");

        boolean pathStyleAccessEnabled = System.getenv("AWS_PATH_STYLE_ACCESS_ENABLED") != null;
        if (accessKey == null || secretKey == null) {
            throw new RuntimeException("AWS credentials are not set in the configuration.");
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        S3Configuration serviceConfiguration =
                S3Configuration.builder().pathStyleAccessEnabled(pathStyleAccessEnabled).build();

        S3AsyncClientBuilder builder = S3AsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(serviceConfiguration)
                .region(Region.US_EAST_1);


        if (endpoint != null) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    private static @NotNull HikariDataSource getDatasource() {
        @NotNull String databaseUrl = System.getenv().getOrDefault("DATABASE_HOST", "");
        @NotNull String databasePort = System.getenv().getOrDefault("DATABASE_PORT", "");
        @NotNull String databaseName = System.getenv().getOrDefault("DATABASE_NAME", "");
        @NotNull String databaseUser = System.getenv().getOrDefault("DATABASE_USER", "");
        @NotNull String databasePassword = System.getenv().getOrDefault("DATABASE_PASSWORD", "");

        HikariConfig hikariConfig = new HikariConfig();
        String uri = "jdbc:postgresql://" + databaseUrl + ":" + databasePort + "/" + databaseName;

        hikariConfig.setJdbcUrl(uri);
        if (!databaseUser.isEmpty() && !databasePassword.isEmpty()) {
            hikariConfig.setUsername(databaseUser);
            hikariConfig.setPassword(databasePassword);
        }
        hikariConfig.setDriverClassName("org.postgresql.Driver");

        return new HikariDataSource(hikariConfig);
    }

    public static void main(String[] args) throws BuildFormatException, IOException {

        Properties properties = new Properties();
        InputStream resourceAsStream = Lobby.class.getClassLoader().getResourceAsStream("version.properties");
        if(resourceAsStream != null) {
            properties.load(resourceAsStream);

            log.info("Version: {}", properties.getProperty("version"));
            log.info("Build Time: {}", properties.getProperty("buildTime"));
        }

        new Lobby();
    }

}
