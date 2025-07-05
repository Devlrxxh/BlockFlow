package dev.lrxh.blockFlow.entities.task;

import dev.lrxh.blockFlow.BlockFlow;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Entity;

import java.util.Iterator;
import java.util.Map;

@AllArgsConstructor
public class EntityCacheTask implements Runnable {
    private final BlockFlow blockFlow;

    @Override
    public void run() {
        Iterator<Map.Entry<Integer, Entity>> iterator = blockFlow.getEntityCache().getEntities().entrySet().iterator();
        while (iterator.hasNext()) {

            Map.Entry<Integer, Entity> entry = iterator.next();
            Entity entity = entry.getValue();
            if (entity == null || !entity.isValid() || entity.isDead()) {
                iterator.remove();
            }
        }
    }
}