package org.readutf.gameservice.common;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor @Getter @Setter
public class Server {

    @NotNull
    private final UUID serverId;

    @NotNull
    private final String containerId;

    @NotNull
    private final NetworkSettings networkSettings;

    @NotNull
    private Heartbeat heartbeat;

    @NotNull
    private List<Game> games;
}
