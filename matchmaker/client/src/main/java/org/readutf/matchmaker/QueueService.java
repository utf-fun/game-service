package org.readutf.matchmaker;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import org.readutf.matchmaker.data.GetQueueResponse;
import org.readutf.matchmaker.data.ListQueuesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface QueueService {

    @GET("/api/v1/queue")
    Call<List<ListQueuesResponse>> listQueues();

    @GET("/api/v1/queue/{name}")
    Call<GetQueueResponse> getQueue(@Path("name") String name);

}
