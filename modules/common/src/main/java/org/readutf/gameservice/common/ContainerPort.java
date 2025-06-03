package org.readutf.gameservice.common;

public record ContainerPort(String name, String protocol, int containerPort, int hostPort) {}
