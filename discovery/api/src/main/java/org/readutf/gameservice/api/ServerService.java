package org.readutf.gameservice.api;

import org.readutf.gameservice.common.Server;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ServerService {

    @GET("/api/v1/server/")
    Call<List<Server>> getServers();

    @GET("/api/v1/server/")
    Call<List<Server>> getServersByTag(@Query("tags") String tags);

    @GET("/api/v1/server/{name}")
    Call<Server> getServerByName(@Path("name") String name);

}
