package org.readutf.gameservice.common.container;

import org.jetbrains.annotations.NotNull;

public abstract class ContainerInfo {
    private @NotNull final String containerId;
    private @NotNull final NetworkSettings networkSettings;

    public ContainerInfo(@NotNull String containerId, @NotNull NetworkSettings networkSettings) {
        this.containerId = containerId;
        this.networkSettings = networkSettings;
    }

    public @NotNull String getContainerId() {
        return containerId;
    }

    public @NotNull NetworkSettings getNetworkSettings() {
        return networkSettings;
    }

    @Override
    public String toString() {
        return "ContainerInfo{" +
                "containerId='" + containerId + '\'' +
                ", networkSettings=" + networkSettings +
                '}';
    }
}
