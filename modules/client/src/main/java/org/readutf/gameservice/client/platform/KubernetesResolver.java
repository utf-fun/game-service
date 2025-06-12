package org.readutf.gameservice.client.platform;

public class KubernetesResolver implements ContainerPlatform {
    @Override
    public String getContainerId() {
        return System.getenv("HOSTNAME");
    }
}
