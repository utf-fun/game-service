package org.readutf.gameservice.container;

import java.util.List;

public record ContainerNetworkSettings(String hostname, List<ContainerPort> ports) {}
