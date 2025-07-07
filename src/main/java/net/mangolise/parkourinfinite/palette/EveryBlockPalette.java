package net.mangolise.parkourinfinite.palette;

import net.minestom.server.collision.ShapeImpl;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.registry.StaticProtocolObject;

import java.util.Comparator;

public class EveryBlockPalette implements Palette {
    private static final Block[] blocks;
    private int index;
    private final ItemStack icon;

    static {
        blocks = Block.values().stream()
                .filter(b -> !((ShapeImpl)b.registry().collisionShape()).collisionBoundingBoxes().isEmpty())
                .sorted(Comparator.comparing(StaticProtocolObject::name)).toArray(Block[]::new);
    }

    public EveryBlockPalette(ItemStack icon) {
        index = -1;
        this.icon = icon;
    }

    @Override
    public BlockBox getLargeBlock(long random) {
        index++;
        if (index >= blocks.length) {
            index = 0;
        }

        return new BlockBox(blocks[index], null);
    }

    @Override
    public BlockBox getMediumBlock(long random) {
        return getLargeBlock(random);
    }

    @Override
    public BlockBox getSmallBlock(long random) {
        return getLargeBlock(random);
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }
}
