package org.readutf.matchmaker.data;

import java.util.List;
import java.util.UUID;

public record QueueJoinRequest(UUID id, List<UUID> players) {}
