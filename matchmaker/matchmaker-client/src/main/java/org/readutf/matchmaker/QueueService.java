package org.readutf.matchmaker;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface QueueService {

    @GET
    Call<List<JsonNode>> getQueues();

}
