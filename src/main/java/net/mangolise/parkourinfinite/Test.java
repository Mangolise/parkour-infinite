package net.mangolise.parkourinfinite;

import net.mangolise.gamesdk.util.GameSdkUtils;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.permission.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// This is a dev server, not used in production
public class Test {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.getConnectionManager().setUuidProvider((connection, username) -> GameSdkUtils.createFakeUUID(username));

        if (GameSdkUtils.useBungeeCord()) {
            BungeeCordProxy.enable();
        }

        Map<UUID, Integer> scores = new HashMap<>();

        // give every permission to every player
        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, e -> {
            e.getPlayer().addPermission(new Permission("*"));

            UUID uuid = e.getPlayer().getUuid();
            if (!scores.containsKey(uuid)) {
                scores.put(uuid, 0);
            }
        });

        MinecraftServer.getGlobalEventHandler().addListener(PlayerStepEvent.class, e -> {
            scores.put(e.getPlayer().getUuid(), e.getStepCount());
        });

        server.start("0.0.0.0", GameSdkUtils.getConfiguredPort());

        ParkourInfGame.Config config = new ParkourInfGame.Config(player -> new ParkourInfPlayer(player, scores.get(player.getUuid())));
        ParkourInfGame game = new ParkourInfGame(config);
        game.setup();
    }
}
