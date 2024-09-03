package net.mangolise.parkourinfinite;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerStepEvent implements PlayerEvent {
    private final Player player;
    private final int stepCount;
    private final Vec stepPosition;
    private final boolean usedSkip;

    public PlayerStepEvent(Player player, int stepCount, Vec stepPosition, boolean usedSkip) {
        this.player = player;
        this.stepCount = stepCount;
        this.stepPosition = stepPosition;
        this.usedSkip = usedSkip;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public int getStepCount() {
        return stepCount;
    }

    /**
     * @return the position of the block that was jumped on (not the players position)
     */
    public Vec getStepPosition() {
        return stepPosition;
    }

    /**
     * @return whether the player used the failsafe item to skip a jump
     */
    public boolean getUsedSkip() {
        return usedSkip;
    }
}
