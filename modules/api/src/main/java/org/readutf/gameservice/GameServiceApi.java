package org.readutf.gameservice;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.exceptions.GameServiceException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GameServiceApi {

    private final @NotNull ServerService serverService;

    public GameServiceApi(String baseUrl) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                // use gson converter for JSON parsing
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();

        this.serverService = retrofit.create(ServerService.class);
    }

    public @NotNull List<Server> getServers() throws GameServiceException {
        Call<List<Server>> serversRequest = serverService.getServers();

        Response<List<Server>> serversResponse;
        try {
            serversResponse = serversRequest.execute();
        } catch (IOException e) {
            throw new GameServiceException("An IO exception occurred while reaching the game service.", e);
        }
        if (!serversResponse.isSuccessful()) {
            throw new GameServiceException(
                    "An error occurred while fetching the servers: " + serversResponse.message());
        }
        List<Server> servers = serversResponse.body();

        return servers != null ? Collections.unmodifiableList(servers) : List.of();
    }

    public @NotNull List<Server> getServersByTag(@NotNull String tag) throws GameServiceException {
        Call<List<Server>> serversRequest = serverService.getServersByTag(tag);

        Response<List<Server>> serversResponse;
        try {
            serversResponse = serversRequest.execute();
        } catch (IOException e) {
            throw new GameServiceException("An IO exception occurred while reaching the game service.", e);
        }
        if (!serversResponse.isSuccessful()) {
            throw new GameServiceException(
                    "An error occurred while fetching the servers: " + serversResponse.message());
        }
        List<Server> servers = serversResponse.body();

        return servers != null ? Collections.unmodifiableList(servers) : List.of();
    }
}
