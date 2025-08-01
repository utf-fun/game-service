package org.readutf.social.party;

import java.util.UUID;

public record PartyInvite(UUID target, long issued) {
}
