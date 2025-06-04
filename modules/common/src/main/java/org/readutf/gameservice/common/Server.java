package org.readutf.gameservice.common;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.common.container.ContainerInfo;

 @Getter @Setter
public class Server {

     public Server(@NotNull UUID serverId, @NotNull ContainerInfo containerInfo, @NotNull Heartbeat heartbeat, @NotNull List<Game> games) {
         this.serverId = serverId;
         this.containerInfo = containerInfo;
         this.heartbeat = heartbeat;
         this.games = games;
     }

     @NotNull
    private final UUID serverId;

    @NotNull
    private final ContainerInfo containerInfo;

    @NotNull
    private Heartbeat heartbeat;

    @NotNull
    private List<Game> games;
}
