package org.readutf.social.party;

/**
 * Enum representing all possible party-related error types for API responses.
 */
public enum PartyErrorType {
    PARTY_NOT_FOUND,
    ALREADY_IN_PARTY,
    PLAYER_NOT_IN_PARTY,
    NOT_INVITED,
    INVITE_ALREADY_EXISTS,
    PARTY_ALREADY_OPEN,
    UNKNOWN_ERROR
}