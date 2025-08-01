package org.readutf.gameservice.social.party.exception;

import org.readutf.social.party.PartyErrorType;

public class PartyException extends Exception {

    private final PartyErrorType type;

    public PartyException(PartyErrorType type) {
        super(type.name());
        this.type = type;
    }

    public PartyErrorType getType() {
        return type;
    }
}
