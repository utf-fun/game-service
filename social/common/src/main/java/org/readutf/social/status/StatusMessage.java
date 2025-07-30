package org.readutf.social.status;

import java.util.List;
import java.util.UUID;

public record StatusMessage(UUID serverId, List<UUID> joined, List<UUID> disconnected) {



}
