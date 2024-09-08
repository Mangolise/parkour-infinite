package net.mangolise.parkourinfinite;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.mangolise.gamesdk.log.Log;
import net.mangolise.gamesdk.util.GameSdkUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerEatEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.tag.Tag;
import net.minestom.server.timer.TaskSchedule;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkourInfPlayer {
    public static final Tag<ParkourInfPlayer> PLAYER_INF_TAG = Tag.Transient("player_inf_tag");

    private static final ItemStack FAILSAFE_ITEM = ItemStack.of(Material.CHICKEN).withCustomName(Component.text("Skip this jump").decoration(TextDecoration.ITALIC, false).color(TextColor.color(143, 176, 79)));
    private static final int FAILSAFE_COUNT = 8;
    private static final Vec START_POSITION = new Vec(0, 128, 0);
    private static final int LOAD_PLACE_AMOUNT = 32;

    // the blocks deque is an array of blocks ordered newest to oldest
    private final Player player;
    private final Instance instance;
    private final Deque<EntityBlock> blocks;
    private final Deque<Long> stepTimes;
    private final Random random;

    private double spawnRotation = 0d;
    private int stepCount;
    private double lowestBlockY = 30000d;
    private int jumpDeathCount = 0;

    public ParkourInfPlayer(Player player, int stepCount, long seed) {
        player.setTag(PLAYER_INF_TAG, this);

        this.player = player;
        this.instance = player.getInstance();
        this.blocks = new ArrayDeque<>(stepCount + 16);
        this.stepTimes = new ArrayDeque<>();
        this.stepCount = stepCount;
        this.random = new Random(seed);

        // Create blocks player has already stepped on
        Point prevPosition = START_POSITION;
        if (stepCount <= LOAD_PLACE_AMOUNT) {
            EntityBlock initialBlock = new EntityBlock(instance, Block.STONE, START_POSITION, START_POSITION, 0);
            initialBlock.setSteppedOn(stepCount != 0);
            blocks.add(initialBlock);
        }

        int skipCount = Math.max(0, stepCount-LOAD_PLACE_AMOUNT);
        for (int i = 0; i < skipCount; i++) {
            prevPosition = getNextPosition(prevPosition);
        }

        for (int i = 0; i < stepCount - skipCount - 1; i++) {
            Point nextPos = getNextPosition(prevPosition);
            EntityBlock block = createBlock(prevPosition, nextPos);
            block.setSteppedOn(true);
            blocks.addFirst(block);
            prevPosition = nextPos;
        }

        Vec lastSteppedPos = blocks.getFirst().getTargetPos();

        // Create blocks player hasn't stepped on
        EntityBlock firstUnsteppedBlock = addBlock(blocks.getFirst().getTargetPos());
        blocks.addFirst(firstUnsteppedBlock);

        Pos spawnPos = lastSteppedPos.add(0.5, 3, 0.5).asPosition()
                .withYaw((float) Math.toDegrees(-firstUnsteppedBlock.getPlaceRotation()));

        for (int i = 0; i < 2; i++) {
            blocks.addFirst(addBlock(blocks.getFirst().getTargetPos()));
        }

        // Re teleport the player to the spawn, so they don't fall off immediately
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            player.teleport(spawnPos);
            return TaskSchedule.stop();
        }, TaskSchedule.tick(5));

        EventNode<EntityEvent> events = player.eventNode();
        events.addListener(PlayerMoveEvent.class, this::onMove);
        events.addListener(PlayerDisconnectEvent.class, this::onDisconnect);
        events.addListener(PlayerEatEvent.class, this::onEat);

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            updateActionBar();
            return TaskSchedule.tick(5);
        }, TaskSchedule.immediate());
    }

    public int getStepCount() {
        return stepCount;
    }

    private void updateActionBar() {
        long currentTime = System.currentTimeMillis();
        AtomicInteger count = new AtomicInteger();

        stepTimes.removeIf(time -> {
            if (time < currentTime-5000) {
                return true;
            }

            count.getAndIncrement();
            return false;
        });

        float stepsPerSecond = (float) count.get() / 5f;

        player.sendActionBar(Component.text(String.format("%d steps (%.2f steps/s)", stepCount, stepsPerSecond)).color(NamedTextColor.DARK_GREEN));
    }

    private void onMove(PlayerMoveEvent e) {
        Pos newPos = e.getNewPosition();

        // we only care if the player moved, not if they turn their head
        if (newPos.distanceSquared(player.getPosition()) < Vec.EPSILON) {
            return;
        }

        if (newPos.y() < lowestBlockY - 6d) {
            EntityBlock previous = null;
            for (EntityBlock block : blocks) {
                if (block.wasSteppedOn()) {
                    double rot = previous != null ? previous.getPlaceRotation() : block.getPlaceRotation();

                    player.teleport(block.getTargetPos().add(0.5, 1, 0.5).asPosition()
                            .withYaw((float) Math.toDegrees(-rot)));
                    jumpDeathCount++;

                    if (jumpDeathCount == FAILSAFE_COUNT) {
                        player.getInventory().addItemStack(FAILSAFE_ITEM);
                    }
                    return;
                }

                previous = block;
            }
            return;
        }

        BoundingBox box = player.getBoundingBox();
        List<EntityBlock> newBlocks = new ArrayList<>();
        boolean stepped = false;
        Vec steppedPos = null;

        synchronized (blocks) {
            for (EntityBlock block : blocks) {
                if (block.wasSteppedOn()) {
                    break;
                }

                // when you step on a block, go through every block before it that hasn't been stepped on and
                // add do the step logic for it
                if (stepped) {
                    newBlocks.add(addBlock(newBlocks.getLast().getTargetPos()));
                    block.setSteppedOn(true);
                    stepCount++;
                    stepTimes.addLast(System.currentTimeMillis());
                    continue;
                }

                if (GameSdkUtils.collidesWithBoundingBox(box, newPos, block.getPosition().add(0, 1, 0))) {
                    steppedPos = blocks.getFirst().getTargetPos();
                    newBlocks.add(addBlock(steppedPos));
                    block.setSteppedOn(true);
                    stepped = true;
                    stepCount++;
                    stepTimes.addLast(System.currentTimeMillis());

                    player.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 0.1f, 0.3f));
                }
            }

            if (stepped) {
                updateActionBar();

                for (EntityBlock newBlock : newBlocks) {
                    blocks.addFirst(newBlock);
                }

                EventDispatcher.call(new PlayerStepEvent(player, stepCount, steppedPos, false));
            }
        }
    }

    private Point getNextPosition(Point previousPos) {
        // generate block position
        Vec offset = new Vec(random.nextGaussian()*0.8d, random.nextGaussian(0d, 0.75d), 0);
        if (offset.y() < 0) offset = offset.withY(y -> y*4);
        else if (offset.y() > 1d) offset = offset.withY(1d);

        offset = offset.withZ((offset.y() + random.nextGaussian()*0.2d) * -0.4d + 4d);

        spawnRotation += random.nextGaussian() / 2d;
        offset = offset.rotateAroundY(spawnRotation);

        Point position = previousPos.add(offset);

        // update lowest block position and reset jumpDeathCount
        if (position.y() < lowestBlockY) {
            lowestBlockY = position.y();
        }

        return position;
    }

    private EntityBlock createBlock(Point previousPos, Point position) {
        // if they activated failsafe or beat a jump after obtaining failsafe, remove failsafe
        if (jumpDeathCount >= FAILSAFE_COUNT) {
            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItemStack(i).material().equals(Material.CHICKEN)) {
                    inventory.setItemStack(i, ItemStack.AIR);
                    break;
                }
            }
        }

        jumpDeathCount = 0;

        return new EntityBlock(instance, Block.EMERALD_ORE, previousPos, position, (float) spawnRotation);
    }

    private EntityBlock addBlock(Point previousPos) {
        Point position = getNextPosition(previousPos);
        return createBlock(previousPos, position);
    }

    private void onEat(PlayerEatEvent e) {
        // failsafe
        EntityBlock block = null;

        for (EntityBlock iter : blocks) {
            if (iter.wasSteppedOn()) {
                break;
            }

            block = iter;
        }

        if (block == null) {
            Log.logger().error("Impossible case: failsafe activated with nowhere to go");
            return;
        }

        player.teleport(block.getTargetPos().add(0.5, 1, 0.5).asPosition().withView(player.getPosition()));

        blocks.addFirst(addBlock(blocks.getFirst().getTargetPos()));
        block.setSteppedOn(true);
        stepCount++;

        player.playSound(Sound.sound(SoundEvent.BLOCK_ANVIL_LAND, Sound.Source.PLAYER, 0.4f, 0.5f));

        EventDispatcher.call(new PlayerStepEvent(player, stepCount, block.getTargetPos(), true));
    }

    private void onDisconnect(PlayerDisconnectEvent e) {
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            MinecraftServer.getInstanceManager().unregisterInstance(instance);
        });
    }
}
