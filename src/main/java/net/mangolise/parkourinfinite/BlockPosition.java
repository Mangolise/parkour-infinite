package net.mangolise.parkourinfinite;

import net.minestom.server.coordinate.Point;

public record BlockPosition(Point pos, int blockType, long passRandom) {}
