package org.readutf.gameservice.container.docker;

import org.readutf.gameservice.common.container.ContainerInfo;
import org.readutf.gameservice.common.container.NetworkSettings;

public class DockerContainer extends ContainerInfo {
    public DockerContainer(String containerId, NetworkSettings networkSettings) {
        super(containerId, networkSettings);
    }
}
