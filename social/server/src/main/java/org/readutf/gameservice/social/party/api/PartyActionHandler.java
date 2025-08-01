package org.readutf.gameservice.social.party.api;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.readutf.gameservice.social.party.PartyManager;
import org.readutf.social.party.exception.PartyException;
import org.readutf.social.party.*;

public class PartyActionHandler implements Handler {

    private final @NotNull PartyManager partyManager;
    private final @NotNull PartyFunction partyFunction;

    public PartyActionHandler(@NotNull PartyManager partyManager, @NotNull PartyFunction partyFunction) {
        this.partyManager = partyManager;
        this.partyFunction = partyFunction;
    }

    @Override
    public void handle(@NotNull Context ctx) throws PartyException {

        try {
            Party party = partyFunction.apply(partyManager, ctx);
            ctx.status(HttpStatus.OK);
            ctx.json(new PartyApiResponse(PartyResultType.SUCCESS, null));
        } catch (PartyException e) {
            ctx.status(500);
            ctx.json(new PartyApiResponse(e.getType(), null));
        }
    }

}
