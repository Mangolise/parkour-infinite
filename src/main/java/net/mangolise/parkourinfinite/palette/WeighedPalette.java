package net.mangolise.parkourinfinite.palette;

import net.mangolise.gamesdk.log.Log;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WeighedPalette implements Palette {
    private final float largeBlocksTotal;
    private final float mediumBlocksTotal;
    private final float smallBlocksTotal;
    private final List<WeightedBlockBox> largeBlocks;
    private final List<WeightedBlockBox> mediumBlocks;
    private final List<WeightedBlockBox> smallBlocks;

    public WeighedPalette(List<WeightedBlockBox> largeBlocks, List<WeightedBlockBox> mediumBlocks, List<WeightedBlockBox> smallBlocks) {
        this.largeBlocks = largeBlocks;
        this.mediumBlocks = mediumBlocks;
        this.smallBlocks = smallBlocks;

        largeBlocksTotal = getTotalWeight(largeBlocks);
        mediumBlocksTotal = getTotalWeight(mediumBlocks);
        smallBlocksTotal = getTotalWeight(smallBlocks);
    }

    public static WeighedPalette createFromObjects(List<Object> largeBuilder, List<Object> mediumBuilder, List<Object> smallBuilder) {
        return new WeighedPalette(
            buildBlocksFromBuilder(largeBuilder),
            buildBlocksFromBuilder(mediumBuilder),
            buildBlocksFromBuilder(smallBuilder)
        );
    }

    private static List<WeightedBlockBox> buildBlocksFromBuilder(List<Object> builder) {
        List<WeightedBlockBox> blocks = new ArrayList<>(builder.size() / 3);

        try {
            for (int i = 0; i < builder.size(); i += 3) {
                Object customHitbox = builder.get(i+1);
                //noinspection unchecked
                blocks.add(new WeightedBlockBox(new BlockBox(
                                (Block) builder.get(i),
                                (customHitbox instanceof Collection) ? (Collection<BoundingBox>) customHitbox : null
                        ), (float) builder.get(i+2)));
            }
        } catch (ClassCastException | IndexOutOfBoundsException | NullPointerException e) {
            Log.logger().error("Failed to load WeighedPalette: " + e);
        }

        return Collections.unmodifiableList(blocks);
    }

    private float getTotalWeight(List<WeightedBlockBox> blocks) {
        float total = 0;

        for (WeightedBlockBox blockBox : blocks) {
            total += blockBox.weight();
        }

        return total;
    }

    private BlockBox getRandomBlock(List<WeightedBlockBox> blocks, float totalWeight, long random) {
        float weight = (float) random / 4294967296f * totalWeight;

        for (WeightedBlockBox blockBox : blocks) {
            weight -= blockBox.weight();
            if (weight < 0) {
                return blockBox.blockBox();
            }
        }

        // will only happen if used incorrectly
        Log.logger().warn("getRandomBlock failure in weightedPalette, extra weight: " + weight);

        weight = (float) random / 4294967296f * totalWeight;

        for (WeightedBlockBox blockBox : blocks) {
            weight -= blockBox.weight();
            if (weight < 0) {
                return blockBox.blockBox();
            }
        }

        return blocks.getLast().blockBox();
    }

    @Override
    public BlockBox getLargeBlock(long random) {
        return getRandomBlock(largeBlocks, largeBlocksTotal, random);
    }

    @Override
    public BlockBox getMediumBlock(long random) {
        return getRandomBlock(mediumBlocks, mediumBlocksTotal, random);
    }

    @Override
    public BlockBox getSmallBlock(long random) {
        return getRandomBlock(smallBlocks, smallBlocksTotal, random);
    }

    public record WeightedBlockBox(BlockBox blockBox, float weight) {}
}
