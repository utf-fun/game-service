package org.readutf.social.party.exception;

import org.readutf.social.party.PartyResultType;

public class PartyException extends Exception {

    private final PartyResultType type;

    public PartyException(PartyResultType type) {
        super(type.name());
        this.type = type;
    }

    public PartyResultType getType() {
        return type;
    }
}
