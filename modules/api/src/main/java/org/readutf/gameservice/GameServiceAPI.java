package org.readutf.gameservice;

import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.common.Server;
import org.readutf.gameservice.exceptions.GameServiceException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GameServiceAPI {

    private final @NotNull ServerService serverService;

    public GameServiceAPI(String hostname) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(hostname)
                .build();

        this.serverService = retrofit.create(ServerService.class);
    }

    public @NotNull List<Server> getServers() throws  GameServiceException {
        Call<List<Server>> serversRequest = serverService.getServers();

        Response<List<Server>> serversResponse;
        try {
            serversResponse = serversRequest.execute();
        } catch (IOException e) {
            throw new GameServiceException("An IO exception occurred while reaching the game service.", e);
        }
        if (!serversResponse.isSuccessful()) {
            throw new GameServiceException("An error occurred while fetching the servers: " + serversResponse.message());
        }
        List<Server> servers = serversResponse.body();

        return servers != null ? Collections.unmodifiableList(servers) : List.of();
    }

}
