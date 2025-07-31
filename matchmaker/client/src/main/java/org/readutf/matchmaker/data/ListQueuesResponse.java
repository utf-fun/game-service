package org.readutf.matchmaker.data;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public record ListQueuesResponse(@NotNull String name, @NotNull List<UUID> entries) {}
