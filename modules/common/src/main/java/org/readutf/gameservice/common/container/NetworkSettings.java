package org.readutf.gameservice.common.container;

import java.util.List;

public record NetworkSettings(String hostname, List<ContainerPort> ports) {}
