package net.mangolise.parkourinfinite.palette;

public interface Palette {
    /**
     * Gets a random block with a top size of 75% or larger (12px/16px-16px/16px)
     * This is usually full blocks, slabs, or carpets
     *
     * @param random a random number between 0-4294967296 (unsigned int)
     * @return the block
     */
    BlockBox getLargeBlock(long random);

    /**
     * Gets a random block with a top size of 37.5%-75% (6px/16px-12/16px)
     * This is usually walls
     *
     * @param random a random number between 0-4294967296 (unsigned int)
     * @return the block
     */
    BlockBox getMediumBlock(long random);

    /**
     * Gets a random block with a top size of 6.25%-37.5% (1px/16px-6px/16px)
     * This is usually glass panes or iron bars
     *
     * @param random a random number between 0-4294967296 (unsigned int)
     * @return the block
     */
    BlockBox getSmallBlock(long random);
}
