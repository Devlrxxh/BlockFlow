package dev.lrxh.blockFlow;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.*;

public class FlowStage {
    private final World world;
    private final Location pos1, pos2;
    private final Set<UUID> watchers;
    private Map<FlowPosition, BlockData> blocks;

    public FlowStage(Location pos1, Location pos2) {
        this.world = pos1.getWorld();
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.blocks = capture();
        this.watchers = new HashSet<>();
    }

    private Map<FlowPosition, BlockData> capture() {
        Map<FlowPosition, BlockData> blocks = new HashMap<>();

        this.blocks = new HashMap<>();
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
                    blocks.put(new FlowPosition(x, y, z), block.getBlockData().clone());
                }
            }
        }

        return blocks;
    }

    public void addWatcher(Player player) {
        watchers.add(player.getUniqueId());
    }

    public void removeWatcher(Player player) {
        watchers.remove(player.getUniqueId());
    }

    public void show() {
        for (Player player : getPlayers()) {
            show(player);
        }
    }

    public void show(Player player) {
        for (FlowPosition pos : blocks.keySet()) {
            BlockData blockData = blocks.get(pos);
            show(player, blockData, pos.x(), pos.y(), pos.z());
        }
    }

    public void hide(Player player) {
        for (FlowPosition pos : blocks.keySet()) {
            show(player, Material.AIR.createBlockData(), pos.x(), pos.y(), pos.z());
        }
    }

    public void hide() {
        for (Player player : getPlayers()) {
            hide(player);
        }
    }

    private void show(Player player, BlockData blockData, int x, int y, int z) {
        player.sendBlockChange(new Location(player.getWorld(), x, y, z), blockData);
    }

    private Set<Player> getPlayers() {
        Set<Player> p = new HashSet<>();

        for (UUID uuid : new HashSet<>(watchers)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                watchers.remove(uuid);
                continue;
            }
            p.add(player);
        }

        return p;
    }
}
