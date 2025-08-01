package org.readutf.gameservice.social.party;

import org.junit.jupiter.api.Test;
import org.readutf.gameservice.social.party.exception.PartyException;
import org.readutf.social.party.PartyErrorType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PartyManager} to ensure complete code coverage.
 */
public class PartyManagerTest {

    PartyManager partyManager = new PartyManager();

    @Test
    public void testCreatePartySuccess() throws PartyException {

        UUID owner = UUID.randomUUID();

        partyManager.createParty(owner);
    }


    @Test
    public void testCreatePartyAlreadyIn() throws PartyException {

        UUID owner = UUID.randomUUID();

        partyManager.createParty(owner);

        assertThrows(PartyException.class, () ->
                partyManager.createParty(owner));
    }

    @Test
    public void joinPartyOpen() throws PartyException {

        UUID owner = UUID.randomUUID();
        Party party = partyManager.createParty(owner);

        partyManager.setPartyOpen(party.id(), true);

        UUID joining = UUID.randomUUID();

        partyManager.joinParty(joining, owner);
    }

    @Test
    public void joinPartyNoInvite() throws PartyException {

        UUID owner = UUID.randomUUID();
        partyManager.createParty(owner);

        UUID joining = UUID.randomUUID();

        PartyException throwable = assertThrows(PartyException.class, () -> partyManager.joinParty(joining, owner));

        assertEquals(PartyErrorType.NOT_INVITED, throwable.getType());
    }

    @Test
    public void joinPartyAlreadyIn() throws PartyException {

        UUID owner = UUID.randomUUID();
        Party party = partyManager.createParty(owner);

        partyManager.setPartyOpen(party.id(), true);

        UUID joining = UUID.randomUUID();

        partyManager.joinParty(joining, owner);
        PartyException exception = assertThrows(PartyException.class, () -> partyManager.joinParty(joining, owner));

        assertEquals(PartyErrorType.ALREADY_IN_PARTY, exception.getType());
    }

    @Test
    public void joinPartyNotFound() throws PartyException {

        UUID owner = UUID.randomUUID();
        partyManager.createParty(owner);

        PartyException exception = assertThrows(PartyException.class, () -> partyManager.joinParty(UUID.randomUUID(), UUID.randomUUID()));

        assertEquals(PartyErrorType.PLAYER_NOT_IN_PARTY, exception.getType());
    }

    @Test
    public void joinInvitedSuccess() throws PartyException {

        UUID owner = UUID.randomUUID();
        partyManager.createParty(owner);

        UUID joining = UUID.randomUUID();

        partyManager.invitePlayer(joining, owner);

        partyManager.joinParty(joining, owner);
    }

    @Test
    public void inviteAlreadyExists() throws PartyException {

        UUID owner = UUID.randomUUID();
        partyManager.createParty(owner);

        UUID joining = UUID.randomUUID();

        partyManager.invitePlayer(joining, owner);
        PartyException exception = assertThrows(PartyException.class, () -> partyManager.invitePlayer(joining, owner));

        assertEquals(PartyErrorType.INVITE_ALREADY_EXISTS, exception.getType());
    }

    @Test
    public void invitePartyOpen() throws PartyException {

        UUID owner = UUID.randomUUID();
        Party party = partyManager.createParty(owner);

        UUID joining = UUID.randomUUID();

        partyManager.setPartyOpen(party.id(), true);

        PartyException exception = assertThrows(PartyException.class, () -> partyManager.invitePlayer(joining, owner));

        assertEquals(PartyErrorType.PARTY_ALREADY_OPEN, exception.getType());
    }

    @Test
    public void invitePlayerNotInParty() throws PartyException {

        UUID owner = UUID.randomUUID();
        Party party = partyManager.createParty(owner);

        UUID joining = UUID.randomUUID();

        partyManager.setPartyOpen(party.id(), true);

        PartyException exception = assertThrows(PartyException.class, () ->
                partyManager.invitePlayer(joining, UUID.randomUUID()));

        assertEquals(PartyErrorType.PLAYER_NOT_IN_PARTY, exception.getType());
    }

    @Test
    public void inviteAlreadyInParty() throws PartyException {

        UUID owner = UUID.randomUUID();
        partyManager.createParty(owner);

        UUID joining = UUID.randomUUID();

        partyManager.invitePlayer(joining, owner);
        partyManager.joinParty(joining, owner);

        PartyException exception = assertThrows(PartyException.class, () ->
                partyManager.invitePlayer(joining, owner));

        assertEquals(PartyErrorType.ALREADY_IN_PARTY, exception.getType());
    }

    @Test
    public void getPartyNotExisting() throws PartyException {

        partyManager.createParty(UUID.randomUUID());
        partyManager.createParty(UUID.randomUUID());
        partyManager.createParty(UUID.randomUUID());

        assertNull(partyManager.getPartyById(UUID.randomUUID()));
    }

    @Test
    public void getParties() {
        partyManager.getParties();
    }


}