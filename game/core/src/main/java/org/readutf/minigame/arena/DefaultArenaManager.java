package org.readutf.minigame.arena;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.readutf.buildformat.common.meta.BuildMetaStore;
import org.readutf.buildformat.common.schematic.BuildSchematicStore;
import org.readutf.buildformat.s3.S3BuildSchematicStore;
import org.readutf.buildformat.sql.SQLMetaStore;
import org.readutf.engine.arena.ArenaManager;
import org.readutf.engine.minestom.arena.MinestomArenaPlatform;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.io.File;
import java.net.URI;

public class DefaultArenaManager {

    private static final BuildMetaStore metaStore = SQLMetaStore.createMetaStore(getDatasource());
    private static final BuildSchematicStore schematicStore = new S3BuildSchematicStore(getAwsClient(), "builds");

    @Contract(" -> new")
    public static @NotNull ArenaManager<Instance> createArenaManager() {
        return new ArenaManager<>(
                new MinestomArenaPlatform(schematicStore, new File("builds")),
                metaStore
        );
    }

    private static S3AsyncClient getAwsClient() {

        String accessKey = System.getenv("AWS_ACCESS_KEY");
        String secretKey = System.getenv("AWS_SECRET_KEY");
        String endpoint = System.getenv("AWS_ENDPOINT");
        String region = System.getenv("AWS_REGION");

        System.out.println("Using AWS credentials: " + accessKey + ", endpoint: " + endpoint + ", region: " + region);

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


        if(endpoint != null) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    private static @NotNull HikariDataSource getDatasource() {
        @NotNull String databaseUrl = System.getenv().getOrDefault("DATABASE_URL", "");
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
}
