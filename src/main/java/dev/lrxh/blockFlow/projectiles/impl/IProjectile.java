package dev.lrxh.blockFlow.projectiles.impl;

import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Set;
import java.util.UUID;

public interface IProjectile {
    WrapperEntity handle(Material material, Location location, Set<UUID> viewers);
}
