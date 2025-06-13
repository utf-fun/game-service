package org.readutf.gameservice.client.platform;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DockerResolver implements ContainerResolver {
    @Override
    public String getContainerId() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "";
        }
    }
}
