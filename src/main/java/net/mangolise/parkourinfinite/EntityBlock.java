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

    private boolean steppedOn = false;

    public static EntityBlock createBlock(Instance instance, Block block, Point fromPos, BlockPosition targetPos, float placeRotation, @Nullable Collection<BoundingBox> customCollision) {
        double blockHeight;
        if (customCollision == null) {
            blockHeight = block.registry().collisionShape().relativeEnd().y();
        } else {
            blockHeight = Double.MIN_VALUE;
            for (BoundingBox boundingBox : customCollision) {
                blockHeight = Math.max(boundingBox.maxY(), blockHeight);
            }
        }

        return new EntityBlock(instance, block, fromPos.sub(0, blockHeight, 0), targetPos, blockHeight, placeRotation, customCollision);
    }

    private EntityBlock(Instance instance, Block block, Point fromPos, BlockPosition targetPos, double blockHeight, float placeRotation, @Nullable Collection<BoundingBox> customCollision) {
        super(instance, block, fromPos, 5, Vec.ONE, customCollision, 1);
        this.targetPos = targetPos;
        this.placeRotation = placeRotation;

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
}
