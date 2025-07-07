package net.mangolise.parkourinfinite.palette;

import net.kyori.adventure.text.Component;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Collection;
import java.util.List;

public final class Palettes {
    public static Collection<BoundingBox> NOT_TALL_WALL = List.of(new BoundingBox(0.5, 1.0, 0.5, new Vec(0.25, 0.0, 0.25)));

    public static List<Palette> allPalettes() {
        return List.of(
                ores(),
                overworld(),
                underGround(),
                nether(),
                end(),
                grayScale(),
                cyanOrange(),
                greenPurple(),
                rainbow(),
                lush(),
                wooden(),
                deepDark(),
                redstone()
        );
    }

    private static WeighedPalette ORES = null;
    public static WeighedPalette ores() {
        if (ORES != null) {
            return ORES;
        }

        ORES = WeighedPalette.createFromObjects(
            ItemStack.of(Material.IRON_ORE),
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
                ItemStack.of(Material.GRASS_BLOCK),
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

    // Underground Palette
    private static WeighedPalette UNDERGROUND = null;
    public static WeighedPalette underGround() {
        if (UNDERGROUND != null) {
            return UNDERGROUND;
        }

        UNDERGROUND = WeighedPalette.createFromObjects(
                ItemStack.of(Material.STONE).withCustomName(Component.text("Under Ground")),
                List.of( // large
                        Block.STONE, -1, 10f,
                        Block.ANDESITE, -1, 8f,
                        Block.GRAVEL, -1, 6f,
                        Block.TUFF, -1, 6f,
                        Block.DEEPSLATE, -1, 4f,
                        Block.SMOOTH_BASALT, -1, 2f
                ),
                List.of( // medium
                        Block.ANDESITE_WALL, NOT_TALL_WALL, 3f,
                        Block.TUFF, NOT_TALL_WALL, 2f,
                        Block.COBBLED_DEEPSLATE_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.LIGHT_GRAY_STAINED_GLASS_PANE, -1, 1f,
                        Block.GRAY_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return UNDERGROUND;
    }

    // Nether Palette
    private static WeighedPalette NETHER = null;
    public static WeighedPalette nether() {
        if (NETHER != null) {
            return NETHER;
        }

        NETHER = WeighedPalette.createFromObjects(
                ItemStack.of(Material.NETHERRACK).withCustomName(Component.text("Nether")),
                List.of( // large
                        Block.CRIMSON_NYLIUM, -1, 10f,
                        Block.NETHERRACK, -1, 8f,
                        Block.MAGMA_BLOCK, -1, 6f,
                        Block.SOUL_SOIL, -1, 6f,
                        Block.BASALT, -1, 4f,
                        Block.OBSIDIAN, -1, 2f
                ),
                List.of( // medium
                        Block.NETHER_BRICK_WALL, NOT_TALL_WALL, 3f,
                        Block.RED_NETHER_BRICK_WALL, NOT_TALL_WALL, 2f,
                        Block.BLACKSTONE_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.RED_STAINED_GLASS_PANE, -1, 1f,
                        Block.BLACK_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return NETHER;
    }

    // End Palette
    private static WeighedPalette END = null;
    public static WeighedPalette end() {
        if (END != null) {
            return END;
        }

        END = WeighedPalette.createFromObjects(
                ItemStack.of(Material.END_STONE).withCustomName(Component.text("End")),
                List.of( // large
                        Block.END_STONE, -1, 10f,
                        Block.END_STONE_BRICKS, -1, 8f,
                        Block.OBSIDIAN, -1, 6f,
                        Block.CRYING_OBSIDIAN, -1, 6f,
                        Block.CHORUS_FLOWER, -1, 4f,
                        Block.PURPUR_BLOCK, -1, 2f
                ),
                List.of( // medium
                        Block.END_STONE_BRICK_WALL, NOT_TALL_WALL, 3f,
                        Block.BLACKSTONE_WALL, NOT_TALL_WALL, 2f,
                        Block.CHORUS_PLANT, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.END_ROD, -1, 1f,
                        Block.PURPLE_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return END;
    }

    // Gray Scale Palette
    private static WeighedPalette GRAYSCALE = null;
    public static WeighedPalette grayScale() {
        if (GRAYSCALE != null) {
            return GRAYSCALE;
        }

        GRAYSCALE = WeighedPalette.createFromObjects(
                ItemStack.of(Material.GRAY_CONCRETE_POWDER).withCustomName(Component.text("Grayscale")),
                List.of( // large
                        Block.WHITE_CONCRETE_POWDER, -1, 10f,
                        Block.LIGHT_GRAY_CONCRETE_POWDER, -1, 8f,
                        Block.LIGHT_GRAY_CONCRETE, -1, 6f,
                        Block.GRAY_CONCRETE_POWDER, -1, 6f,
                        Block.GRAY_CONCRETE, -1, 4f,
                        Block.BLACK_CONCRETE_POWDER, -1, 2f
                ),
                List.of( // medium
                        Block.DIORITE_WALL, NOT_TALL_WALL, 3f,
                        Block.ANDESITE_WALL, NOT_TALL_WALL, 2f,
                        Block.COBBLED_DEEPSLATE_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.WHITE_STAINED_GLASS_PANE, -1, 1f,
                        Block.BLACK_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return GRAYSCALE;
    }

    // Cyan and Orange Palette
    private static WeighedPalette CYANORANGE = null;
    public static WeighedPalette cyanOrange() {
        if (CYANORANGE != null) {
            return CYANORANGE;
        }

        CYANORANGE = WeighedPalette.createFromObjects(
                ItemStack.of(Material.ORANGE_GLAZED_TERRACOTTA).withCustomName(Component.text("Cyan and Orange")),
                List.of( // large
                        Block.CYAN_CONCRETE, -1, 10f,
                        Block.CYAN_WOOL, -1, 8f,
                        Block.CYAN_CONCRETE_POWDER, -1, 6f,
                        Block.ORANGE_CONCRETE_POWDER, -1, 6f,
                        Block.ORANGE_WOOL, -1, 8f,
                        Block.ORANGE_CONCRETE, -1, 10f
                ),
                List.of( // medium
                        Block.PRISMARINE_WALL, NOT_TALL_WALL, 3f,
                        Block.WARPED_FENCE, NOT_TALL_WALL, 2f,
                        Block.RED_SANDSTONE_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.CYAN_STAINED_GLASS_PANE, -1, 1f,
                        Block.ORANGE_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return CYANORANGE;
    }

    // Rainbow Palette
    private static WeighedPalette RAINBOW = null;
    public static WeighedPalette rainbow() {
        if (RAINBOW != null) {
            return RAINBOW;
        }

        RAINBOW = WeighedPalette.createFromObjects(
                ItemStack.of(Material.RED_STAINED_GLASS).withCustomName(Component.text("Rainbow")),
                List.of( // large
                        Block.RED_CONCRETE_POWDER, -1, 10f,
                        Block.ORANGE_CONCRETE_POWDER, -1, 8f,
                        Block.YELLOW_CONCRETE_POWDER, -1, 6f,
                        Block.LIME_CONCRETE_POWDER, -1, 6f,
                        Block.CYAN_CONCRETE_POWDER, -1, 4f,
                        Block.PURPLE_CONCRETE_POWDER, -1, 2f
                ),
                List.of( // medium
                        Block.MANGROVE_FENCE, NOT_TALL_WALL, 3f,
                        Block.ACACIA_FENCE, NOT_TALL_WALL, 2f,
                        Block.WARPED_FENCE, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.LIME_STAINED_GLASS_PANE, -1, 1f,
                        Block.PURPLE_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return RAINBOW;
    }

    // Green and Purple Palette
    private static WeighedPalette GREENPURPLE = null;
    public static WeighedPalette greenPurple() {
        if (GREENPURPLE != null) {
            return GREENPURPLE;
        }

        GREENPURPLE = WeighedPalette.createFromObjects(
                ItemStack.of(Material.PURPLE_GLAZED_TERRACOTTA).withCustomName(Component.text("Green and Purple")),
                List.of( // large
                        Block.LIME_CONCRETE, -1, 10f,
                        Block.LIME_WOOL, -1, 8f,
                        Block.LIME_CONCRETE_POWDER, -1, 6f,
                        Block.PURPLE_CONCRETE_POWDER, -1, 6f,
                        Block.PURPLE_WOOL, -1, 8f,
                        Block.PURPLE_CONCRETE, -1, 10f
                ),
                List.of( // medium
                        Block.WARPED_FENCE, NOT_TALL_WALL, 3f,
                        Block.PRISMARINE_WALL, NOT_TALL_WALL, 2f,
                        Block.CRIMSON_FENCE, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.LIME_STAINED_GLASS_PANE, -1, 1f,
                        Block.PURPLE_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return GREENPURPLE;
    }

    // Lush Palette
    private static WeighedPalette LUSH = null;
    public static WeighedPalette lush() {
        if (LUSH != null) {
            return LUSH;
        }

        LUSH = WeighedPalette.createFromObjects(
                ItemStack.of(Material.MOSS_BLOCK).withCustomName(Component.text("Lush")),
                List.of( // large
                        Block.MOSS_BLOCK, -1, 10f,
                        Block.OAK_LEAVES, -1, 8f,
                        Block.AZALEA_LEAVES, -1, 6f,
                        Block.FLOWERING_AZALEA_LEAVES, -1, 6f,
                        Block.FLOWERING_AZALEA, -1, 4f,
                        Block.AZALEA, -1, 2f
                ),
                List.of( // medium
                        Block.MOSSY_COBBLESTONE_WALL, NOT_TALL_WALL, 3f,
                        Block.TUFF_WALL, NOT_TALL_WALL, 2f,
                        Block.MOSSY_STONE_BRICK_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.LIME_STAINED_GLASS_PANE, -1, 1f,
                        Block.GREEN_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return LUSH;
    }

    // Wooden Palette
    private static WeighedPalette WOODEN = null;
    public static WeighedPalette wooden() {
        if (WOODEN != null) {
            return WOODEN;
        }

        WOODEN = WeighedPalette.createFromObjects(
                ItemStack.of(Material.STRIPPED_OAK_WOOD).withCustomName(Component.text("Wooden")),
                List.of( // large
                        Block.STRIPPED_BIRCH_WOOD, -1, 10f,
                        Block.STRIPPED_OAK_WOOD, -1, 8f,
                        Block.STRIPPED_JUNGLE_WOOD, -1, 6f,
                        Block.BROWN_MUSHROOM_BLOCK, -1, 6f,
                        Block.STRIPPED_SPRUCE_WOOD, -1, 4f,
                        Block.STRIPPED_DARK_OAK_WOOD, -1, 2f
                ),
                List.of( // medium
                        Block.BIRCH_FENCE, NOT_TALL_WALL, 3f,
                        Block.OAK_FENCE, NOT_TALL_WALL, 2f,
                        Block.SPRUCE_FENCE, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.YELLOW_STAINED_GLASS_PANE, -1, 1f,
                        Block.BROWN_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return WOODEN;
    }

    // Deep Dark Palette
    private static WeighedPalette DEEPDARK = null;
    public static WeighedPalette deepDark() {
        if (DEEPDARK != null) {
            return DEEPDARK;
        }

        DEEPDARK = WeighedPalette.createFromObjects(
                ItemStack.of(Material.SCULK).withCustomName(Component.text("Deep Dark")),
                List.of( // large
                        Block.SCULK, -1, 10f,
                        Block.SCULK_CATALYST, -1, 8f,
                        Block.REINFORCED_DEEPSLATE, -1, 6f,
                        Block.DEEPSLATE, -1, 6f,
                        Block.COBBLED_DEEPSLATE, -1, 4f,
                        Block.DEEPSLATE_BRICKS, -1, 2f
                ),
                List.of( // medium
                        Block.COBBLED_DEEPSLATE_WALL, NOT_TALL_WALL, 3f,
                        Block.DEEPSLATE_BRICK_WALL, NOT_TALL_WALL, 2f,
                        Block.DEEPSLATE_TILE_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.GRAY_STAINED_GLASS_PANE, -1, 1f,
                        Block.BLACK_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return DEEPDARK;
    }

    // Redstone Palette
    private static WeighedPalette REDSTONE = null;
    public static WeighedPalette redstone() {
        if (REDSTONE != null) {
            return REDSTONE;
        }

        REDSTONE = WeighedPalette.createFromObjects(
                ItemStack.of(Material.REDSTONE_LAMP).withCustomName(Component.text("Redstone")),
                List.of( // large
                        Block.REDSTONE_BLOCK, -1, 10f,
                        Block.REDSTONE_LAMP.withProperty("lit", "true"), -1, 8f,
                        Block.NOTE_BLOCK, -1, 6f,
                        Block.HOPPER, -1, 6f,
                        Block.PISTON.withProperty("facing", "up"), -1, 4f,
                        Block.DISPENSER, -1, 2f
                ),
                List.of( // medium
                        Block.COBBLESTONE_WALL, NOT_TALL_WALL, 3f,
                        Block.MANGROVE_FENCE, NOT_TALL_WALL, 2f,
                        Block.BRICK_WALL, NOT_TALL_WALL, 1f
                ),
                List.of( // small
                        Block.RED_STAINED_GLASS_PANE, -1, 1f,
                        Block.GRAY_STAINED_GLASS_PANE, -1, 1f
                )
        );

        return REDSTONE;
    }

    private static WeighedPalette TEST = null;
    public static WeighedPalette test() {
        if (TEST != null) {
            return TEST;
        }

        TEST = WeighedPalette.createFromObjects(
                ItemStack.of(Material.BEDROCK),
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

    private static EveryBlockPalette EVERYTHING = null;
    public static EveryBlockPalette everything() {
        if (EVERYTHING != null) {
            return EVERYTHING;
        }

        EVERYTHING = new EveryBlockPalette(ItemStack.of(Material.WAXED_WEATHERED_CUT_COPPER_STAIRS));
        return EVERYTHING;
    }
}
