package dev.lrxh.blockFlow.events;

import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class FlowItemPickupEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final ItemStack itemStack;
    private final FlowStage stage;
    private boolean cancelled = false;

    public FlowItemPickupEvent(Player player, ItemStack itemStack, FlowStage stage) {
        this.player = player;
        this.itemStack = itemStack;
        this.stage = stage;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}