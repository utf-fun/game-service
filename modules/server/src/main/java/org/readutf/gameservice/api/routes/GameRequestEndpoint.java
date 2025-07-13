package org.readutf.gameservice.api.routes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.game.GameManager;
import org.readutf.gameservice.game.GameRequest;
import org.readutf.gameservice.game.GameResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameRequestEndpoint implements Handler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @NotNull
    private final GameManager gameManager;

    public GameRequestEndpoint(@NotNull GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String playlist = context.pathParam("playlist");
        String body = context.body();

        List<List<String>> teamsStrings;
        try {
            teamsStrings = objectMapper.readValue(body, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            context.status(HttpStatus.BAD_REQUEST);
            context.json("Invalid JSON format for teams. Expected a list of lists of UUID strings.");
            return;
        }
        List<List<UUID>> teams = teamsStrings.stream()
                .map(strings -> strings.stream().map(UUID::fromString).toList())
                .toList();

        GameResult result = gameManager.findGame(playlist, teams);
        if (result == null) {
            context.status(HttpStatus.NOT_FOUND);
            context.json("No suitable game found for the requested playlist and teams.");
        } else {
            context.status(200);
            context.json(result);
        }
    }
}
