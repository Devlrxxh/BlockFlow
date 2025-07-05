package dev.lrxh.blockFlow.stage.impl;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

@Getter
public class FlowPosition {
    private final int x, y, z;

    public FlowPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlowPosition that)) return false;
        return x == that.x && y == that.y && z == that.z;
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
