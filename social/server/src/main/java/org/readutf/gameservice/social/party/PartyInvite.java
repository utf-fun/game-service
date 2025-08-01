package org.readutf.gameservice.social.party;

import java.util.UUID;

public record PartyInvite(UUID target, long issued) {
}
