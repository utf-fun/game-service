package org.readutf.gameservice;

import org.readutf.gameservice.common.Server;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface ServerService {

    @GET("/api/v1/server/")
    Call<List<Server>> getServers();

}
