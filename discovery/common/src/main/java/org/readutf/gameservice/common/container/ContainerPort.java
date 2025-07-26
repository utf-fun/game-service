package org.readutf.gameservice.common.container;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ContainerPort(@Nullable String name, @NotNull String protocol, int containerPort, @Nullable Integer hostPort) {}
