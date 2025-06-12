package org.readutf.gameservice.container.kubernetes;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.credentials.AccessTokenAuthentication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.readutf.gameservice.common.container.ContainerInfo;
import org.readutf.gameservice.common.container.ContainerPort;
import org.readutf.gameservice.common.container.NetworkSettings;
import org.readutf.gameservice.container.ContainerPlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class KubernetesPlatform implements ContainerPlatform {

    private static final Logger log = LoggerFactory.getLogger(KubernetesPlatform.class);
    private final ApiClient client;
    private final CoreV1Api api;

    public KubernetesPlatform(
            @NotNull String basePath,
            @NotNull String accessToken
    ) {
        this.client = new ClientBuilder()
                .setBasePath(basePath)
                .setAuthentication(new AccessTokenAuthentication(accessToken))  // Use proper authentication
                .setVerifyingSsl(false)
                .build();

        Configuration.setDefaultApiClient(client);
        this.api = new CoreV1Api(client);
    }

    @Override
    public @Nullable ContainerInfo getContainerInfo(String containerId) {

        V1PodList podInfo;
        try {
            podInfo = api.listPodForAllNamespaces().execute();
        } catch (ApiException e) {
            log.error("An error occurred while fetching container info", e);
            return null;
        }

        for (V1Pod item : podInfo.getItems()) {
            V1ObjectMeta metadata = item.getMetadata();
            if (metadata == null) continue;
            if (metadata.getName() == null) continue;

            if (metadata.getName().equalsIgnoreCase(containerId)) {

                NetworkSettings networkSettings = getNetworkSettings(item);
                if(networkSettings == null) {
                    log.warn("No network settings found for container: {}", containerId);
                    return null;
                }

                return new KubernetesContainer(containerId, metadata.getNamespace(), networkSettings);
            }
        }
        return null;
    }

    private static @Nullable NetworkSettings getNetworkSettings(V1Pod item) {
        if (item.getStatus() == null) return null;
        if(item.getSpec() == null) return null;

        String podIP = item.getStatus().getPodIP();
        if(podIP == null) return null;

        List<ContainerPort> containerPort = new ArrayList<>();
        for (V1Container container : item.getSpec().getContainers()) {
            if(container.getPorts() == null) continue;
            for (V1ContainerPort port : container.getPorts()) {
                String protocol = port.getProtocol() != null ? port.getProtocol() : "";
                containerPort.add(new ContainerPort(port.getName(), protocol, port.getContainerPort(), port.getHostPort()));
            }
        }

        NetworkSettings networkSettings = new NetworkSettings(podIP, containerPort);
        return networkSettings;
    }

}
