package org.readutf.social.status;

import java.util.Collection;
import java.util.UUID;

public record SessionUpdatePayload(UUID serverId, Collection<UUID> joined, Collection<UUID> disconnected) {


}
