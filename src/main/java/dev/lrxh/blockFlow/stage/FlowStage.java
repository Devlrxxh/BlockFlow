package dev.lrxh.blockFlow.stage;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.LightData;
import com.github.retrooper.packetevents.protocol.world.chunk.impl.v_1_18.Chunk_v1_18;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import dev.lrxh.blockFlow.stage.impl.FlowBlock;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FlowStage {
    @Getter
    private final World world;
    private final Location pos1, pos2;
    @Getter
    private final Set<UUID> watchers;
    @Getter
    private final UUID uuid;
    private Map<FlowPosition, FlowBlock> blocks;

    public FlowStage(Location pos1, Location pos2) {
        this.world = pos1.getWorld();
        this.pos1 = pos1.clone();
        this.pos2 = pos2.clone();
        this.blocks = capture();
        this.watchers = new HashSet<>();
        this.uuid = UUID.randomUUID();
    }

    private Map<FlowPosition, FlowBlock> capture() {
        Map<FlowPosition, FlowBlock> blocks = new HashMap<>();

        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());

        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType().toString().toLowerCase().contains("air")) continue;
                    blocks.put(new FlowPosition(x, y, z), new FlowBlock(block));
                }
            }
        }

        return blocks;
    }

    public void offset(int x, int y, int z) {
        Map<FlowPosition, FlowBlock> newBlocks = new HashMap<>();
        for (FlowPosition pos : blocks.keySet()) {
            newBlocks.put(new FlowPosition(pos.getX() + x, pos.getY() + y, pos.getZ() + z), blocks.get(pos));
        }
        this.blocks = newBlocks;

        this.pos1.add(x, y, z);
        this.pos2.add(x, y, z);
    }

    public void addViewer(Player player) {
        watchers.add(player.getUniqueId());

        for (PacketWrapper<?> packet : buildAllChunkPackets(player)) {
            User packetUser = PacketEvents.getAPI().getPlayerManager().getUser(player);
            packetUser.sendPacketSilently(packet);
        }
    }

    public void removeViewer(Player player) {
        watchers.remove(player.getUniqueId());
    }

    public void sendChunkToPlayer(Player player, int chunkX, int chunkZ) {
        User packetUser = PacketEvents.getAPI().getPlayerManager().getUser(player);
        int minHeight = world.getMinHeight();
        int ySections = packetUser.getTotalWorldHeight() >> 4;

        Map<Integer, Chunk_v1_18> sections = createChunkSections(chunkX, chunkZ, minHeight, ySections);

        WrapperPlayServerChunkData packet = createChunkPacket(chunkX, chunkZ, sections, ySections);

        packetUser.sendPacketSilently(packet);
    }

    private List<WrapperPlayServerChunkData> buildAllChunkPackets(Player player) {
        User packetUser = PacketEvents.getAPI().getPlayerManager().getUser(player);
        int minHeight = world.getMinHeight();
        int ySections = packetUser.getTotalWorldHeight() >> 4;

        Map<String, Map<Integer, Chunk_v1_18>> chunkMap = createChunkSections(minHeight, ySections);

        List<WrapperPlayServerChunkData> packets = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, Chunk_v1_18>> entry : chunkMap.entrySet()) {
            String[] split = entry.getKey().split(",");
            int chunkX = Integer.parseInt(split[0]);
            int chunkZ = Integer.parseInt(split[1]);
            Map<Integer, Chunk_v1_18> sections = entry.getValue();

            WrapperPlayServerChunkData packet = createChunkPacket(chunkX, chunkZ, sections, ySections);
            packets.add(packet);
        }
        return packets;
    }

    private Map<Integer, Chunk_v1_18> createChunkSections(int chunkX, int chunkZ, int minHeight, int ySections) {
        Map<Integer, Chunk_v1_18> sections = new HashMap<>();
        Map<BlockData, WrappedBlockState> blockDataToState = new HashMap<>();
        WrappedBlockState airState = SpigotConversionUtil.fromBukkitBlockData(Bukkit.createBlockData(Material.AIR));

        for (FlowPosition pos : blocks.keySet()) {

            if ((pos.getX() >> 4) != chunkX || (pos.getZ() >> 4) != chunkZ) continue;

            int sectionY = (pos.getY() >> 4) - (minHeight >> 4);
            if (sectionY < 0 || sectionY >= ySections) continue;

            Chunk_v1_18 section = sections.computeIfAbsent(sectionY, k -> {
                Chunk_v1_18 newSection = new Chunk_v1_18();
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            newSection.set(x, y, z, airState);
                        }
                    }
                }
                return newSection;
            });

            FlowBlock block = getBlockDataAt(pos);
            if (block.getBlockData().getMaterial().toString().toLowerCase().contains("air")) continue;

            WrappedBlockState state = blockDataToState.computeIfAbsent(block.getBlockData(), SpigotConversionUtil::fromBukkitBlockData);
            section.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
        }

        return sections;
    }

    private WrapperPlayServerChunkData createChunkPacket(int chunkX, int chunkZ, Map<Integer, Chunk_v1_18> sections, int ySections) {
        List<BaseChunk> chunkList = new ArrayList<>();
        BitSet blockMask = new BitSet(ySections);
        BitSet skyMask = new BitSet(ySections);
        byte[][] blockLight = new byte[ySections][];
        byte[][] skyLight = new byte[ySections][];
        byte[] fullLightSection = new byte[2048];
        Arrays.fill(fullLightSection, (byte) 0xFF);

        for (int i = 0; i < ySections; i++) {
            Chunk_v1_18 section = sections.getOrDefault(i, new Chunk_v1_18());

            int biomeId = section.getBiomeData().palette.stateToId(1);
            int storageSize = section.getBiomeData().storage.getData().length;
            for (int index = 0; index < storageSize; index++) {
                section.getBiomeData().storage.set(index, biomeId);
            }

            chunkList.add(section);
            blockMask.set(i);
            skyMask.set(i);
            blockLight[i] = fullLightSection;
            skyLight[i] = fullLightSection;
        }

        LightData lightData = new LightData();
        lightData.setBlockLightArray(blockLight);
        lightData.setSkyLightArray(skyLight);
        lightData.setBlockLightCount(ySections);
        lightData.setSkyLightCount(ySections);
        lightData.setBlockLightMask(blockMask);
        lightData.setSkyLightMask(skyMask);
        lightData.setEmptyBlockLightMask(new BitSet(ySections));
        lightData.setEmptySkyLightMask(new BitSet(ySections));

        Column column = new Column(chunkX, chunkZ, true, chunkList.toArray(new BaseChunk[0]), null);
        return new WrapperPlayServerChunkData(column, lightData);
    }

    private @NotNull Map<String, Map<Integer, Chunk_v1_18>> createChunkSections(int minHeight, int ySections) {
        Map<String, Map<Integer, Chunk_v1_18>> chunkMap = new HashMap<>();
        Map<BlockData, WrappedBlockState> blockDataToState = new HashMap<>();
        WrappedBlockState airState = SpigotConversionUtil.fromBukkitBlockData(Bukkit.createBlockData(Material.AIR));

        for (FlowPosition pos : blocks.keySet()) {
            int sectionY = (pos.getY() >> 4) - (minHeight >> 4);

            if (sectionY < 0 || sectionY >= ySections) continue;

            String key = (pos.getX() >> 4) + "," + (pos.getZ() >> 4);
            Map<Integer, Chunk_v1_18> sections = chunkMap.computeIfAbsent(key, k -> new HashMap<>());
            Chunk_v1_18 section = sections.computeIfAbsent(sectionY, k -> {
                Chunk_v1_18 newSection = new Chunk_v1_18();
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            newSection.set(x, y, z, airState);
                        }
                    }
                }
                return newSection;
            });

            FlowBlock block = getBlockDataAt(pos);
            if (block.getBlockData().getMaterial().toString().toLowerCase().contains("air")) continue;

            WrappedBlockState state = blockDataToState.computeIfAbsent(block.getBlockData(), SpigotConversionUtil::fromBukkitBlockData);
            section.set(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
        }

        return chunkMap;
    }

    public boolean isChunkInBounds(int chunkX, int chunkZ) {
        int minChunkX = Math.min(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int maxChunkX = Math.max(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int minChunkZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;
        int maxChunkZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;

        return chunkX >= minChunkX && chunkX <= maxChunkX
                && chunkZ >= minChunkZ && chunkZ <= maxChunkZ;
    }

    public boolean isPositionInBounds(FlowPosition pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    public FlowBlock getBlockDataAt(FlowPosition pos) {
        FlowBlock block = blocks.get(pos);
        if (block == null) {
            return new FlowBlock(Bukkit.createBlockData(Material.AIR));
        }
        return block;
    }

    public void setBlockDataAt(FlowPosition pos, BlockData blockData) {
        if (blockData == null || blockData.getMaterial() == Material.AIR) {
            blocks.remove(pos);
        } else {
            blocks.put(pos, new FlowBlock(blockData.clone()));
        }
    }
}