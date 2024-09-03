package net.mangolise.parkourinfinite;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;

public class EntityBlock extends Entity {
    private final Entity shulkerVehicle;
    private final LivingEntity shulker;
    private final Vec targetPos;
    private final float placeRotation;

    private boolean steppedOn = false;

    public EntityBlock(Instance instance, Block block, Point fromPos, Point targetPos, float placeRotation) {
        super(EntityType.BLOCK_DISPLAY);
        this.targetPos = Vec.fromPoint(targetPos);
        this.placeRotation = placeRotation;

        editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setHasNoGravity(true);
        });

        setInstance(instance);
        teleport(Pos.fromPoint(fromPos));

        shulkerVehicle = new Entity(EntityType.BLOCK_DISPLAY);
        shulkerVehicle.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(Block.AIR);
            meta.setInvisible(true);
            meta.setHasNoGravity(true);
        });

        shulker = new LivingEntity(EntityType.SHULKER);
        shulker.setInvisible(true);

        // create hitbox after move is finished
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            shulkerVehicle.setInstance(instance);
            shulker.setInstance(instance);
            shulkerVehicle.addPassenger(shulker);

            shulkerVehicle.teleport(getPosition().add(0.5, 0.0, 0.5));
            return TaskSchedule.stop();
        }, TaskSchedule.tick(8));

        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setTransformationInterpolationDuration(5);
                meta.setPosRotInterpolationDuration(5);
            });
            teleport(Pos.fromPoint(targetPos));
        });
    }

    public boolean wasSteppedOn() {
        return steppedOn;
    }

    public void setSteppedOn(boolean steppedOn) {
        this.steppedOn = steppedOn;
    }

    public Vec getTargetPos() {
        return targetPos;
    }

    public float getPlaceRotation() {
        return placeRotation;
    }
}
