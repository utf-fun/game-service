package org.readutf.social.party;

/**
 * Enum representing all possible party-related error types for API responses.
 */
public enum PartyResultType {
    SUCCESS,
    PARTY_NOT_FOUND,
    ALREADY_IN_PARTY,
    PLAYER_NOT_IN_PARTY,
    NOT_INVITED,
    INVITE_ALREADY_EXISTS,
    PARTY_ALREADY_OPEN,
    NO_PERMISSION,
    UNKNOWN_ERROR
}