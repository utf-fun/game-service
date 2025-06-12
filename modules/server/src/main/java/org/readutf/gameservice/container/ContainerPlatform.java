package org.readutf.gameservice.container;

import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.container.ContainerInfo;

public interface ContainerPlatform<T extends ContainerInfo> {

    @Nullable T getContainerInfo(String containerId);

}
