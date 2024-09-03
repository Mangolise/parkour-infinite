package net.mangolise.parkourinfinite;

import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ParkourInfPlayerProvider {
    @NotNull ParkourInfPlayer provide(@NotNull Player player);
}
