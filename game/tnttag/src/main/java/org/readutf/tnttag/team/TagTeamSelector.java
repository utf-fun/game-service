package org.readutf.tnttag.team;

import java.util.UUID;
import org.readutf.engine.team.GameTeam;
import org.readutf.engine.team.TeamSelector;

public class TagTeamSelector implements TeamSelector<GameTeam> {
    @Override
    public GameTeam getTeam(UUID playerId) {
        return new GameTeam(playerId.toString());
    }
}
