package dev.lrxh.blockFlow.utils;

import dev.lrxh.blockFlow.stage.impl.FlowPosition;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

@UtilityClass
public class BoundingBoxUtil {
    public BoundingBox createBlockBB(FlowPosition position, World world) {
        Location blockLoc = position.toLocation(world);
        return BoundingBox.of(
                blockLoc.toVector(),
                blockLoc.clone().add(1, 1, 1).toVector()
        );
    }
}
