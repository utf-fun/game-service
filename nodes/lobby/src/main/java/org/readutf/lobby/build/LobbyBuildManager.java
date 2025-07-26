package org.readutf.lobby.build;

import net.hollowcube.schem.BlockEntityData;
import net.hollowcube.schem.Schematic;
import net.hollowcube.schem.reader.SpongeSchematicReader;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.kyori.adventure.nbt.StringBinaryTag;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.readutf.buildformat.common.exception.BuildFormatException;
import org.readutf.buildformat.common.format.BuildFormatChecksum;
import org.readutf.buildformat.common.format.BuildFormatManager;
import org.readutf.buildformat.common.format.requirements.RequirementData;
import org.readutf.buildformat.common.markers.Marker;
import org.readutf.buildformat.common.markers.Position;
import org.readutf.buildformat.common.meta.BuildMeta;
import org.readutf.buildformat.common.meta.BuildMetaStore;
import org.readutf.buildformat.common.schematic.BuildSchematic;
import org.readutf.buildformat.common.schematic.BuildSchematicStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class LobbyBuildManager {

    private @NotNull
    final BuildMetaStore metaStore;
    private @NotNull
    final BuildSchematicStore schematicStore;

    public LobbyBuildManager(@NotNull BuildMetaStore metaStore, @NotNull BuildSchematicStore schematicStore) {
        this.metaStore = metaStore;
        this.schematicStore = schematicStore;
    }

    public LobbyBuild loadBuild() throws BuildFormatException, IOException {

        @NotNull List<RequirementData> validators = BuildFormatManager.getValidators(LobbyPositions.class);
        byte @NotNull [] checksum = BuildFormatManager.generateChecksum(validators);

        Map<String, BuildFormatChecksum> buildsByFormat = metaStore.getBuildsByFormat("main-lobby");

        List<String> validBuilds = buildsByFormat.entrySet().stream().filter(entry -> Arrays.equals(entry.getValue().checksum(), checksum)).map(Map.Entry::getKey).toList();

        if (validBuilds.isEmpty()) {
            throw new BuildFormatException("No valid builds found for format 'main-lobby' with checksum: " + Arrays.toString(checksum));
        }

        String name = validBuilds.get(ThreadLocalRandom.current().nextInt(validBuilds.size()));

        BuildSchematic schematicData = schematicStore.load(name);

        if (schematicData == null) {
            throw new BuildFormatException("Build schematic not found for build ID: " + name);
        }

        if(new File(name + ".schem").exists()) {
            System.out.println("Loading schematic from file: " + name + ".schem");
            schematicData = new BuildSchematic(name, Files.readAllBytes(new File(name + ".schem").toPath()));
        } else {
            System.out.println("Writing schematic to file: " + name + ".schem");
            Files.write(new File(name + ".schem").toPath(), schematicData.buildData());
        }

        BuildMeta buildMeta = metaStore.getByName(name);
        if (buildMeta == null) {
            throw new BuildFormatException("Build meta not found for build ID: " + name);
        }

        Schematic schematic;
        try {
            schematic = new SpongeSchematicReader().read(schematicData.buildData());
        } catch (Exception e) {
            throw new BuildFormatException("Failed to read schematic for build ID: " + name, e);
        }

        List<Marker> markers = extractMarkers(schematic);

        LobbyPositions lobbyPositions = BuildFormatManager.constructBuildFormat(markers, LobbyPositions.class);

        Instance instance = placeBuild(schematic, markers);


        for (Marker marker : markers) {
            Position origin = marker.origin();

            instance.setBlock(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ(), Block.AIR);
        }

        return new LobbyBuild(instance, lobbyPositions);
    }

    public Instance placeBuild(@NotNull Schematic schematic, List<Marker> markers) {
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setChunkSupplier(LightingChunk::new);

        Point size = schematic.size();
        Point offset = schematic.offset();

        ArrayList<CompletableFuture<Chunk>> futures = new ArrayList<>();
        for (int i = 0; i < (size.blockX() >> 4) + 1; i++) {
            for (int j = 0; j < (size.blockZ() >> 4) + 1; j++) {
                futures.add(instance.loadChunk(i, j));
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<Point> markerPositions = new ArrayList<>();
        for (Marker marker : markers) {
            Position origin = marker.origin();
            markerPositions.add(new Vec(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ()));
        }


        Point inv = offset.mul(-1);
        schematic.createBatch((point, block) -> markerPositions.contains(Vec.fromPoint(point.add(inv))) ? Block.AIR : block).apply(instance, new Pos(inv), () -> {
        });

        return instance;
    }

    public List<Marker> extractMarkers(@NotNull Schematic schematic) {

        List<Marker> markers = new ArrayList<>();
        for (BlockEntityData entity : schematic.blockEntities()) {
            CompoundBinaryTag data = entity.data();
            String id = data.getString("id");
            if (!"minecraft:sign".equals(id)) continue;

            List<String> markerLines = extractMarkerLines(data);
            Point point = entity.position();

            if (markerLines.isEmpty() || !markerLines.get(0).equalsIgnoreCase("#marker")) {
                continue;
            }

            if (markerLines.size() < 2) continue;
            String markerName = markerLines.get(1);
            if (markerName.isEmpty()) continue;

            int[] offset = new int[]{0, 0, 0};
            if (markerLines.size() > 2 && !markerLines.get(2).isEmpty()) {
                String[] parts = markerLines.get(2).split("[,\\- ]");
                List<Integer> offsets = new ArrayList<>();
                for (String part : parts) {
                    try {
                        offsets.add(Integer.parseInt(part));
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (offsets.size() == 3) {
                    offset = new int[]{offsets.get(0), offsets.get(1), offsets.get(2)};
                } else {
                    continue;
                }
            }

            markers.add(new Marker(markerName, new Position(point.x() + offset[0], point.y() + offset[1], point.z() + offset[2]), new Position(offset[0], offset[1], offset[2])));
        }
        return markers;
    }

    private List<String> extractMarkerLines(CompoundBinaryTag compoundBinaryTag) {
        CompoundBinaryTag frontText = compoundBinaryTag.getCompound("front_text");
        ListBinaryTag messages = frontText.getList("messages");

        List<String> result = new ArrayList<>();
        for (BinaryTag tag : messages) {
            if (tag instanceof StringBinaryTag stringTag) {
                String value = stringTag.value();
                if (value.length() < 2) continue;
                // logger.debug("Extracted marker line: {}", value);
                result.add(value);
            }
        }
        return result;
    }

}
