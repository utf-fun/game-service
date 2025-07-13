package org.readutf.gameservice.container.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.container.ContainerInfo;
import org.readutf.gameservice.common.container.ContainerPort;
import org.readutf.gameservice.common.container.NetworkSettings;
import org.readutf.gameservice.container.ContainerPlatform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DockerContainerPlatform implements ContainerPlatform<DockerContainer> {


    private final DockerClient dockerHttpClient;

    public DockerContainerPlatform(String dockerUrl) {
        if (dockerUrl == null || dockerUrl.isEmpty()) {
            throw new IllegalArgumentException("Docker URL must not be null or empty");
        }
        this.dockerHttpClient = createDockerClient(dockerUrl);
    }


    @Override
    public @Nullable DockerContainer getContainerInfo(String shortContainerId) {

        InspectContainerResponse inspectResponse = dockerHttpClient.inspectContainerCmd(shortContainerId).exec();

        String containerId = inspectResponse.getId();
        String hostName = inspectResponse.getConfig().getHostName();

        Map<ExposedPort, Ports.Binding[]> ports = inspectResponse.getNetworkSettings().getPorts().getBindings();
        List<ContainerPort> containerPorts = new ArrayList<>();
        ports.forEach((exposedPort, bindings) -> {
            containerPorts.add(new ContainerPort("", exposedPort.getProtocol().name(),
                    exposedPort.getPort(), bindings.length > 0 ? Integer.valueOf(bindings[0].getHostPortSpec()) : null));
        });

        return new DockerContainer(containerId, new NetworkSettings(hostName == null ? "" : hostName, containerPorts));
    }

    private DockerClient createDockerClient(String dockerHost) {
        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();

        ZerodepDockerHttpClient httpClient = new ZerodepDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }
}
