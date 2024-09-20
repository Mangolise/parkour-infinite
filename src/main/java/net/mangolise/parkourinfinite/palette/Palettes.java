package net.mangolise.parkourinfinite.palette;

import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

import java.util.Collection;
import java.util.List;

public final class Palettes {
    public static Collection<BoundingBox> NOT_TALL_WALL = List.of(new BoundingBox(0.5, 1.0, 0.5, new Vec(0.25, 0.0, 0.25)));

    private static WeighedPalette ORES = null;
    public static WeighedPalette ores() {
        if (ORES != null) {
            return ORES;
        }

        ORES = WeighedPalette.createFromObjects(
            List.of( // large
                Block.STONE, -1, 10f,
                Block.DEEPSLATE, -1, 8f,
                Block.ANDESITE, -1, 6f,
                Block.GRANITE, -1, 6f,
                Block.DIORITE, -1, 6f,
                Block.COAL_ORE, -1, 5f*0.75f,
                Block.DEEPSLATE_COAL_ORE, -1, 5f*0.25f,
                Block.IRON_ORE, -1, 4f*0.75f,
                Block.DEEPSLATE_IRON_ORE, -1, 4f*0.25f,
                Block.GOLD_ORE, -1, 0.75f,
                Block.DEEPSLATE_GOLD_ORE, -1, 0.25f,
                Block.DIAMOND_ORE, -1, 0.5f*0.75f,
                Block.DEEPSLATE_DIAMOND_ORE, -1, 0.5f*0.25f,
                Block.ANCIENT_DEBRIS, -1, 0.05f
            ),
            List.of( // medium
                Block.COBBLESTONE_WALL, NOT_TALL_WALL, 3f,
                Block.COBBLED_DEEPSLATE_WALL, NOT_TALL_WALL, 1.5f,
                Block.DEEPSLATE_BRICK_WALL, NOT_TALL_WALL, 0.75f,
                Block.DEEPSLATE_TILE_WALL, NOT_TALL_WALL, 0.75f,
                Block.ANDESITE_WALL, NOT_TALL_WALL, 1f,
                Block.DIORITE_WALL, NOT_TALL_WALL, 1f,
                Block.GRANITE_WALL, NOT_TALL_WALL, 1f
            ),
            List.of( // small
                Block.IRON_BARS, -1, 1f
            )
        );

        return ORES;
    }

    private static WeighedPalette OVERWORLD = null;
    public static WeighedPalette overworld() {
        if (OVERWORLD != null) {
            return OVERWORLD;
        }

        OVERWORLD = WeighedPalette.createFromObjects(
                List.of( // large
                    Block.GRASS_BLOCK, -1, 10f,
                    Block.DIRT, -1, 8f,
                    Block.OAK_LEAVES, -1, 4f,
                    Block.OAK_LOG, -1, 3f,
                    Block.STRIPPED_OAK_LOG, -1, 2.5f,
                    Block.OAK_PLANKS, -1, 3f
                ),
                List.of( // medium
                    Block.OAK_FENCE, NOT_TALL_WALL, 3f,
                    Block.SPRUCE_FENCE, NOT_TALL_WALL, 2f,
                    Block.MUD_BRICK_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                    Block.BROWN_STAINED_GLASS_PANE, -1, 1f,
                    Block.GREEN_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return OVERWORLD;
    }

    private static WeighedPalette TEST = null;
    public static WeighedPalette test() {
        if (TEST != null) {
            return TEST;
        }

        TEST = WeighedPalette.createFromObjects(
                List.of( // large
                        Block.GRAY_CARPET, -1, 1f,
                        Block.CAULDRON, -1, 1f
                ),
                List.of( // medium
                        Block.CANDLE_CAKE, -1, 1f
                ),
                List.of( // small
                        Block.ORANGE_CANDLE, -1, 1f
                )
        );

        return TEST;
    }
}
