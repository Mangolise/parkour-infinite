package net.mangolise.parkourinfinite;

import net.mangolise.gamesdk.entity.CollidableDisplayBlock;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class EntityBlock extends CollidableDisplayBlock {
    private final BlockPosition targetPos;
    private final float placeRotation;
    private final double blockHeight;

    private boolean steppedOn = false;

    public EntityBlock(Instance instance, Block block, Point fromPos, BlockPosition targetPos, float placeRotation, @Nullable Collection<BoundingBox> customCollision) {
        super(instance, block, fromPos, 5, Vec.ONE, customCollision, 1);
        this.targetPos = targetPos;
        this.placeRotation = placeRotation;

        if (customCollision == null) {
            blockHeight = block.registry().collisionShape().relativeEnd().y();
        } else {
            double blockHeight = Double.MIN_VALUE;
            for (BoundingBox boundingBox : customCollision) {
                blockHeight = Math.max(boundingBox.maxY(), blockHeight);
            }

            this.blockHeight = blockHeight;
        }

        teleport(Pos.fromPoint(targetPos.pos().sub(0, blockHeight, 0)));
    }

    public boolean wasSteppedOn() {
        return steppedOn;
    }

    public void setSteppedOn(boolean steppedOn) {
        this.steppedOn = steppedOn;
    }

    public BlockPosition getTargetPos() {
        return targetPos;
    }

    public float getPlaceRotation() {
        return placeRotation;
    }

    public double getBlockHeight() {
        return blockHeight;
    }
}
