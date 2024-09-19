package net.mangolise.parkourinfinite.palette;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Represents a block with an optional custom hitbox
 *
 * @param block the block
 * @param customShape the custom shape, null if there is no custom shape
 */
public record BlockBox(Block block, @Nullable Collection<BoundingBox> customShape) { }
