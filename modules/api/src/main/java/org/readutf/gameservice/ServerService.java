package org.readutf.gameservice;

import org.readutf.gameservice.common.Server;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface ServerService {

    @GET("/api/v1/server/")
    Call<List<Server>> getServers();

    @GET("/api/v1/server/")
    Call<List<Server>> getServersByTag(@Query("tags") String tags);

}
