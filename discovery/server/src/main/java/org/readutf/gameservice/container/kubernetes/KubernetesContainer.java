package org.readutf.gameservice.container.kubernetes;

import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.container.ContainerInfo;
import org.readutf.gameservice.common.container.NetworkSettings;

public class KubernetesContainer extends ContainerInfo {

    @Nullable
    private final String namespace;

    public KubernetesContainer(String containerId, @Nullable String namespace, NetworkSettings networkSettings) {
        super(containerId, networkSettings);
        this.namespace = namespace;
    }

    @Override
    public String toString() {
        return "KubernetesContainer{" +
                "namespace='" + namespace + '\'' +
                "} " + super.toString();
    }
}
