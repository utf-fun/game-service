package org.readutf.gameservice.social.party.api;

import io.javalin.http.Context;
import org.readutf.social.party.Party;
import org.readutf.gameservice.social.party.PartyManager;
import org.readutf.social.party.exception.PartyException;

import java.util.UUID;

public interface PartyFunction {

    Party apply(PartyManager partyManager, Context ctx) throws PartyException;

    default PartyActionHandler toHandler(PartyManager partyManager) {
        return new PartyActionHandler(partyManager, this);
    }

    PartyFunction joinParty = (partyManager, ctx) -> {
        UUID owner = UUID.fromString(ctx.pathParam("owner"));
        UUID joining = UUID.fromString(ctx.pathParam("joining"));

        return partyManager.joinParty(owner, joining);
    };

    PartyFunction createParty = (partyManager, ctx) ->
            partyManager.createParty(UUID.fromString(ctx.pathParam("owner")));

    PartyFunction getParty = (partyManager, ctx) ->
            partyManager.getPartyByPlayer(UUID.fromString(ctx.pathParam("player")));

    PartyFunction invite = (partyManager, ctx) -> {
        UUID owner = UUID.fromString(ctx.pathParam("owner"));
        UUID joining = UUID.fromString(ctx.pathParam("player"));

        return partyManager.invitePlayer(joining, owner);
    };

    PartyFunction open = (partyManager, ctx) -> partyManager.toggleOpen(UUID.fromString(ctx.pathParam("owner")));

}
