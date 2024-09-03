package net.mangolise.parkourinfinite;

import net.mangolise.gamesdk.BaseGame;
import net.mangolise.gamesdk.features.AdminCommandsFeature;
import net.mangolise.gamesdk.log.Log;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;

import java.util.List;

public class ParkourInfGame extends BaseGame<ParkourInfGame.Config> {
    public ParkourInfGame() {
        super(new ParkourInfGame.Config());
    }

    @Override
    public void setup() {
        super.setup();

        DimensionType dimension = DimensionType.builder().ambientLight(15).fixedTime(1800L).build();
        DynamicRegistry.Key<DimensionType> dim = MinecraftServer.getDimensionTypeRegistry().register("normal", dimension);

        // Player spawning
        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();
        events.addListener(AsyncPlayerConfigurationEvent.class, e -> {
            Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(dim, null);
            e.setSpawningInstance(instance);

            e.getPlayer().setGameMode(GameMode.ADVENTURE);
            e.getPlayer().setRespawnPoint(new Pos(0.5, 32, 0.5, 0, 0));
        });

        events.addListener(PlayerSpawnEvent.class, e -> {
            new ParkourInfPlayer(e.getPlayer());
        });

        Log.logger().info("Started Parkour Infinite game");
    }

    @Override
    public List<Feature<?>> features() {
        return List.of(
                new AdminCommandsFeature()
        );
    }

    public record Config() { }
}
