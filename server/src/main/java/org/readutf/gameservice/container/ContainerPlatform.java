package org.readutf.gameservice.container;

import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.NetworkSettings;

public interface ContainerPlatform {

    @Nullable NetworkSettings getNetworkSettings(String containerId);

}
