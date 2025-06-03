package org.readutf.gameservice.common;

import java.util.List;

public record NetworkSettings(String hostname, List<ContainerPort> ports) {}
