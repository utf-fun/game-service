package org.readutf.gameservice.social.party;

import org.jetbrains.annotations.Nullable;
import org.readutf.social.party.exception.PartyException;
import org.readutf.social.party.Party;
import org.readutf.social.party.PartyInvite;
import org.readutf.social.party.PartyResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages the lifecycle and operations of {@link Party} objects,
 * including creating parties, joining parties, inviting players,
 * and querying party membership.
 */
public class PartyManager {

    private static final Logger log = LoggerFactory.getLogger(PartyManager.class);
    private final List<Party> parties;

    /**
     * Constructs a new PartyManager with an empty party list.
     */
    public PartyManager() {
        this.parties = new ArrayList<>();
    }

    /**
     * Creates a new party with the given player as the owner.
     * If the player is already in a party, throws a {@link PartyException}.
     *
     * @param playerId the unique identifier of the player creating the party
     * @return a new {@link Party} instance
     * @throws PartyException if the player is already in a party
     */
    public Party createParty(UUID playerId) throws PartyException {
        if (getPartyByPlayer(playerId) != null) {
            throw new PartyException(PartyResultType.ALREADY_IN_PARTY);
        }

        Party party = new Party(UUID.randomUUID(), playerId, Collections.emptyList(), Collections.emptyList(), new AtomicBoolean(false));
        this.parties.add(party);
        return party;
    }

    /**
     * Allows a player to join the party of another player (target).
     * The joining player cannot already be in a party.
     * If the party is open, the player is added directly.
     * If not, the player must have a valid invite.
     *
     * @param joiningId the player joining the party
     * @param targetId  a member of the target party
     * @throws PartyException if joining fails due to party status or missing invite
     */
    public Party joinParty(UUID joiningId, UUID targetId) throws PartyException {
        Party partyByPlayer = getPartyByPlayer(joiningId);
        if (partyByPlayer != null) {
            throw new PartyException(PartyResultType.ALREADY_IN_PARTY);
        }

        Party party = getPartyByPlayer(targetId);
        if (party == null) {
            throw new PartyException(PartyResultType.PLAYER_NOT_IN_PARTY);
        }

        if (party.isOpen()) {
            log.info("Player {} has joined party {}", joiningId, targetId);
            party.members().add(joiningId);
            return party;
        }

        @Nullable PartyInvite invite = party.invites().stream()
                .filter(partyInvite -> partyInvite.target().equals(joiningId))
                .findFirst().orElse(null);
        if (invite == null) {
            throw new PartyException(PartyResultType.NOT_INVITED);
        }

        party.invites().remove(invite);
        party.members().add(joiningId);
        return party;
    }

    /**
     * Invites a target player to the party of the joining.
     * The joining must not already be in a party, and the target must not have a pending invite.
     *
     * @param joining    the player issuing the invite
     * @param partyOwner the player to invite
     * @throws PartyException if inviting fails due to party status or existing invite
     */
    public Party invitePlayer(UUID joining, UUID partyOwner) throws PartyException {
        Party partyByPlayer = getPartyByPlayer(joining);
        if (partyByPlayer != null) {
            throw new PartyException(PartyResultType.ALREADY_IN_PARTY);
        }
        Party party = getPartyByPlayer(partyOwner);
        if (party == null) {
            throw new PartyException(PartyResultType.PLAYER_NOT_IN_PARTY);
        }

        if (party.getInvite(joining) != null) {
            throw new PartyException(PartyResultType.INVITE_ALREADY_EXISTS);
        }
        if (party.isOpen()) {
            throw new PartyException(PartyResultType.PARTY_ALREADY_OPEN);
        }

        party.invites().add(new PartyInvite(joining, System.currentTimeMillis()));
        return party;
    }

    public Party setPartyOpen(UUID ownerId, boolean open) throws PartyException {
        Party partyById = getPartyByPlayer(ownerId);
        if (partyById == null) throw new PartyException(PartyResultType.PARTY_NOT_FOUND);
        if (partyById.owner() == ownerId) throw new PartyException(PartyResultType.NO_PERMISSION);
        partyById.setOpen(open);
        return partyById;
    }

    public Party toggleOpen(UUID ownerId) throws PartyException {
        Party partyById = getPartyByPlayer(ownerId);
        if (partyById == null) throw new PartyException(PartyResultType.PARTY_NOT_FOUND);
        if (partyById.owner() == ownerId) throw new PartyException(PartyResultType.NO_PERMISSION);
        partyById.setOpen(!partyById.isOpen());
        return partyById;
    }

    /**
     * Gets a party by its unique identifier.
     *
     * @param partyId the unique party identifier
     * @return the corresponding {@link Party}, or null if not found
     */
    public @Nullable Party getPartyById(UUID partyId) {
        for (Party party : parties) {
            if (party.id().equals(partyId)) return party;
        }
        return null;
    }

    /**
     * Gets a party by a player's unique identifier.
     * Returns the party if the player is a member or the owner of a party.
     *
     * @param playerId the unique player identifier
     * @return the {@link Party} the player belongs to, or null if not found
     */
    public @Nullable Party getPartyByPlayer(UUID playerId) {
        for (Party party : parties) {
            if (party.members().contains(playerId) || party.owner() == playerId) {
                return party;
            }
        }
        return null;
    }

    /**
     * Returns a copy of the current list of parties managed by this PartyManager.
     *
     * @return a list of {@link Party} objects
     */
    public List<Party> getParties() {
        return new ArrayList<>(parties);
    }
}