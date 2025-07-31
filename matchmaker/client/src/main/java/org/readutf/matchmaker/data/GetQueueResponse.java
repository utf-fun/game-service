package org.readutf.matchmaker.data;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;

public record GetQueueResponse(String name, List<UUID> entries, String matchmaker, JsonNode settings) {}
