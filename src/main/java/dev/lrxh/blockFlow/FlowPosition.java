package dev.lrxh.blockFlow;

import lombok.Getter;

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

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
