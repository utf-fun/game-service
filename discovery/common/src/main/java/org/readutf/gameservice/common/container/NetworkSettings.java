package org.readutf.gameservice.common.container;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record NetworkSettings(@NotNull String hostname, List<ContainerPort> ports) {}
