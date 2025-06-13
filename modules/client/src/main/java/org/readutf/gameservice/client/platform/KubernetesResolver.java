package org.readutf.gameservice.client.platform;

public class KubernetesResolver implements ContainerResolver {
    @Override
    public String getContainerId() {
        return System.getenv("HOSTNAME");
    }
}
