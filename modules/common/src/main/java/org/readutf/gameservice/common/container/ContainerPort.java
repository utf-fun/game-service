package org.readutf.gameservice.common.container;

public record ContainerPort(String name, String protocol, int containerPort, int hostPort) {}
