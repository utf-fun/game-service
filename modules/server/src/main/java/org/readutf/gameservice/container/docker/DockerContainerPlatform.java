package org.readutf.gameservice.container.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.zerodep.ZerodepDockerHttpClient;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.container.ContainerInfo;
import org.readutf.gameservice.common.container.NetworkSettings;
import org.readutf.gameservice.container.ContainerPlatform;

import java.util.List;

public class DockerContainerPlatform implements ContainerPlatform<DockerContainer> {

    private final DockerClient dockerHttpClient = createDockerClient("unix:///var/run/docker.sock");

    @Override
    public @Nullable DockerContainer getContainerInfo(String shortContainerId) {

        InspectContainerResponse inspectResponse = dockerHttpClient.inspectContainerCmd(shortContainerId).exec();

        String containerId = inspectResponse.getId();
        String hostName = inspectResponse.getConfig().getHostName();

//        new ContainerInfo(inspectResponse.getId(), new NetworkSettings(inspectResponse.getNetworkSettings().get))

        return new DockerContainer(containerId, new NetworkSettings(hostName, List.of()));
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
