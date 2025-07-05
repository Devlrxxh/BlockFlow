package dev.lrxh.blockFlow.stage.impl;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FlowBlock {
    private final BlockData blockData;
    private final List<Material> drops;

    public FlowBlock(Block block) {
        this.blockData = block.getBlockData().clone();
        this.drops = new ArrayList<>();

        for (ItemStack itemStack : block.getDrops()) {
            Material material = itemStack.getType();

            if (material.name().toLowerCase().contains("air")) continue;
            drops.add(material);
        }
    }

    public FlowBlock(BlockData blockData) {
        this.blockData = blockData;
        this.drops = new ArrayList<>();
    }

    public boolean canInstantBreak(Player player) {
        return blockData.getDestroySpeed(player.getInventory().getItemInMainHand(), true) >= blockData.getMaterial().getHardness() * 30 || player.getGameMode() == GameMode.CREATIVE;
    }
}
