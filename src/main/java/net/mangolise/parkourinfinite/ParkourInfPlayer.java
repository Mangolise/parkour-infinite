package net.mangolise.parkourinfinite;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.mangolise.gamesdk.log.Log;
import net.mangolise.gamesdk.menu.TimeSwitcherMenu;
import net.mangolise.parkourinfinite.palette.BlockBox;
import net.mangolise.parkourinfinite.palette.Palette;
import net.mangolise.parkourinfinite.palette.Palettes;
import net.minestom.server.MinecraftServer;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerEatEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
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

    private EntityBlock lastNextBlock = null;
    private double spawnRotation = 0d;
    private int stepCount;
    private int jumpDeathCount = 0;

    private Palette palette = Palettes.ores();

    public ParkourInfPlayer(Player player, int stepCount, long seed) {
        player.setTag(PLAYER_INF_TAG, this);

        this.player = player;
        this.instance = player.getInstance();
        this.blocks = new ArrayDeque<>(stepCount + 16);
        this.stepTimes = new ArrayDeque<>();
        this.stepCount = stepCount;
        this.random = new Random(seed);

        // Create blocks player has already stepped on
        BlockPosition prevPosition = new BlockPosition(START_POSITION, 0, 0);
        if (stepCount <= LOAD_PLACE_AMOUNT) {
            EntityBlock initialBlock = new EntityBlock(instance, Block.STONE, START_POSITION, prevPosition, 0, null);
            initialBlock.setSteppedOn(stepCount != 0);
            blocks.add(initialBlock);
        }

        int skipCount = Math.max(0, stepCount-LOAD_PLACE_AMOUNT);
        for (int i = 0; i < skipCount; i++) {
            prevPosition = getNextPosition(prevPosition);
        }

        for (int i = 0; i < stepCount - skipCount - 1; i++) {
            BlockPosition nextPos = getNextPosition(prevPosition);
            EntityBlock block = createBlock(prevPosition.pos(), nextPos);
            block.setSteppedOn(true);
            blocks.addFirst(block);
            prevPosition = nextPos;
        }

        Point lastSteppedPos = blocks.getFirst().getTargetPos().pos();

        // Create blocks player hasn't stepped on
        EntityBlock firstUnsteppedBlock = addBlock(blocks.getFirst().getTargetPos());
        blocks.addFirst(firstUnsteppedBlock);

        Pos spawnPos = Pos.fromPoint(lastSteppedPos.add(0.5, 3, 0.5))
                .withYaw((float) Math.toDegrees(-firstUnsteppedBlock.getPlaceRotation()));

        for (int i = 0; i < 2; i++) {
            blocks.addFirst(addBlock(blocks.getFirst().getTargetPos()));
        }

        player.getInventory().setItemStack(8, ItemStack.builder(Material.CLOCK)
                .customName(Component.text("Time Switcher").decoration(TextDecoration.ITALIC, false))
                .build());

        // Re teleport the player to the spawn, so they don't fall off immediately
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            player.teleport(spawnPos);
            return TaskSchedule.stop();
        }, TaskSchedule.tick(5));

        EventNode<EntityEvent> events = player.eventNode();
        events.addListener(PlayerMoveEvent.class, this::onMove);
        events.addListener(PlayerDisconnectEvent.class, this::onDisconnect);
        events.addListener(PlayerEatEvent.class, this::onEat);
        events.addListener(PlayerUseItemEvent.class, this::onItemUse);
        events.addListener(ItemDropEvent.class, e -> e.setCancelled(true));

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

        // Void death
        if (newPos.y() < blocks.getFirst().getTargetPos().pos().y() - 6d) {
            EntityBlock previous = null;
            for (EntityBlock block : blocks) {
                if (block.wasSteppedOn()) {
                    double rot = previous != null ? previous.getPlaceRotation() : block.getPlaceRotation();

                    player.teleport(Pos.fromPoint(block.getTargetPos().pos().add(0.5, 1, 0.5))
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
        BlockPosition steppedPos = null;
        EntityBlock nextBlock = null;

        synchronized (blocks) {
            for (EntityBlock block : blocks) {
                if (block.wasSteppedOn()) {
                    break;
                }

                nextBlock = block;

                // when you step on a block, go through every block before it that hasn't been stepped on and
                // add do the step logic for it
                if (stepped) {
                    newBlocks.add(addBlock(newBlocks.getLast().getTargetPos()));
                    block.setSteppedOn(true);
                    stepCount++;
                    stepTimes.addLast(System.currentTimeMillis());
                    continue;
                }

                if (isInBlock(newPos, box, block.getTargetPos().pos(), block.getCollision())) {
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

                EventDispatcher.call(new PlayerStepEvent(player, stepCount, Vec.fromPoint(steppedPos.pos()), false));
            }

            if (nextBlock != null) {
                if (lastNextBlock != null) {
                    lastNextBlock.setGlowing(false);
                }

                nextBlock.setGlowing(true);
                lastNextBlock = nextBlock;
            }
        }
    }

    private boolean isInBlock(Point playerPos, BoundingBox playerBox, Point blockPos, Collection<BoundingBox> collision) {
        for (BoundingBox box : collision) {
            if (boundingBoxesCollide(box, playerBox, blockPos, playerPos)) {
                return true;
            }
        }

        return false;
    }

    private boolean boundingBoxesCollide(BoundingBox box1, BoundingBox box2, Point box1Pos, Point box2Pos) {
        Point box1Start = box1.relativeStart().add(box1Pos);
        Point box1End = box1.relativeEnd().add(box1Pos);
        Point box2Start = box2.relativeStart().add(box2Pos);
        Point box2End = box2.relativeEnd().add(box2Pos);

        return box2End.x() > box1Start.x() && box2Start.x() < box1End.x() &&
                box2End.y() > box1Start.y() && box2Start.y() < box1End.y() &&
                box2End.z() > box1Start.z() && box2Start.z() < box1End.z();
    }

    private BlockPosition getNextPosition(BlockPosition previousPos) {
        // generate block type
        long blockRand = random.nextLong();
        int blockType = (int) (blockRand & 0xFFL);
        long passRandom = (blockRand >> 8) & 0xFFFFFFFFL;
        float distanceAdditional;

        final float[] DISTANCE_MODIFIERS = new float[] {0.3f, -0.1f, -0.2f};

        if (blockType < 180) { // ~70% chance
            distanceAdditional = 4.0f + DISTANCE_MODIFIERS[0];
            blockType = 0;
        } else if (blockType < 230) { // ~20% chance
            distanceAdditional = 4.0f + DISTANCE_MODIFIERS[1];
            blockType = 1;
        } else { // ~10% chance
            distanceAdditional = 4.0f + DISTANCE_MODIFIERS[2];
            blockType = 2;
        }

        distanceAdditional += DISTANCE_MODIFIERS[previousPos.blockType()];

        // generate block position
        Vec offset = new Vec(random.nextGaussian()*0.6d,
                random.nextFloat() > 0.650225 ? random.nextGaussian(0.0, 0.75d) : random.nextFloat()
                , 0);
        double distanceMultiplier = -0.38d;

        if (offset.y() < 0) offset = offset.withY(y -> y*4);
        else {
            if (offset.y() > 1d) {
                offset = offset.withY(1d);
            }

            distanceMultiplier = -1.0d;
        }

        offset = offset.withZ(offset.y() * distanceMultiplier + distanceAdditional);

        spawnRotation += random.nextGaussian() / 2d;
        offset = offset.rotateAroundY(spawnRotation);

        Point position = previousPos.pos().add(offset);

        return new BlockPosition(position, blockType, passRandom);
    }

    private EntityBlock createBlock(Point previousPos, BlockPosition position) {
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

        BlockBox block;
        if (position.blockType() == 0) {
            block = palette.getLargeBlock(position.passRandom());
        } else if (position.blockType() == 1) {
            block = palette.getMediumBlock(position.passRandom());
        } else { // ~10% chance
            block = palette.getSmallBlock(position.passRandom());
        }

        return new EntityBlock(instance, block.block(), previousPos, position, (float) spawnRotation, block.customShape());
    }

    private EntityBlock addBlock(BlockPosition previousPos) {
        BlockPosition position = getNextPosition(previousPos);
        return createBlock(previousPos.pos(), position);
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

        player.teleport(Pos.fromPoint(block.getTargetPos().pos().add(0.5, 1, 0.5)).withView(player.getPosition()));

        blocks.addFirst(addBlock(blocks.getFirst().getTargetPos()));
        block.setSteppedOn(true);
        stepCount++;

        player.playSound(Sound.sound(SoundEvent.BLOCK_ANVIL_LAND, Sound.Source.PLAYER, 0.4f, 0.5f));

        EventDispatcher.call(new PlayerStepEvent(player, stepCount, Vec.fromPoint(block.getTargetPos().pos()), true));
    }

    private void onItemUse(PlayerUseItemEvent e) {
        if (Material.CLOCK.equals(e.getItemStack().material())) {
            player.openInventory(TimeSwitcherMenu.MENU.getInventory());
        }
    }

    private void onDisconnect(PlayerDisconnectEvent e) {
        MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
            MinecraftServer.getInstanceManager().unregisterInstance(instance);
        });
    }
}
