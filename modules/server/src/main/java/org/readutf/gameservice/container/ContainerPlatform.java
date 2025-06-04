package org.readutf.gameservice.container;

import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.container.ContainerInfo;
import org.readutf.gameservice.common.container.NetworkSettings;

public interface ContainerPlatform {

    @Nullable ContainerInfo getContainerInfo(String containerId);

}
