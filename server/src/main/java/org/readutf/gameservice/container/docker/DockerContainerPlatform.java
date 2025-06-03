package org.readutf.gameservice.container.docker;

import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.NetworkSettings;
import org.readutf.gameservice.container.ContainerPlatform;

public class DockerContainerPlatform implements ContainerPlatform {

    @Override
    public @Nullable NetworkSettings getNetworkSettings(String containerId) {
        return null;
    }
}
