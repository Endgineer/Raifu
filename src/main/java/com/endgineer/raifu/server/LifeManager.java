package com.endgineer.raifu.server;

import com.endgineer.raifu.compat.PartyIntegration;
import com.endgineer.raifu.config.RaifuConfig;
import com.endgineer.raifu.data.RaifuSavedData;
import com.endgineer.raifu.network.LifeStatePayload;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.network.PacketDistributor;

public final class LifeManager {
    public static final int MAX_LIVES = 10;
    private static final Map<UUID, LifeStatePayload> LAST_SENT = new HashMap<>();

    private LifeManager() {
    }

    public static int getLives(MinecraftServer server, UUID playerId) {
        ResolvedState resolved = resolve(server, playerId);
        return resolved.partyId() == null
                ? resolved.player().lives()
                : resolved.data().partyLives(resolved.partyId());
    }

    public static int setLives(MinecraftServer server, UUID playerId, int lives) {
        ResolvedState resolved = resolve(server, playerId);
        int clamped = Mth.clamp(lives, 0, MAX_LIVES);
        if (resolved.partyId() == null) {
            if (resolved.player().lives() != clamped) {
                resolved.player().setLives(clamped);
                resolved.data().setDirty();
            }
        } else {
            resolved.data().setPartyLives(resolved.partyId(), clamped);
        }

        if (clamped > 0 && resolved.player().eliminated()) {
            resolved.player().setEliminated(false);
            resolved.data().setDirty();
        }
        syncAll(server, false);
        return clamped;
    }

    public static int addLives(MinecraftServer server, UUID playerId, int amount) {
        return setLives(server, playerId, getLives(server, playerId) + amount);
    }

    public static boolean isHardcore(MinecraftServer server, UUID playerId) {
        return RaifuConfig.ENABLED.get() && resolve(server, playerId).player().hardcore();
    }

    public static void setHardcore(MinecraftServer server, UUID playerId, boolean hardcore) {
        ResolvedState resolved = resolve(server, playerId);
        boolean effective = RaifuConfig.ENABLED.get() && hardcore;
        if (resolved.player().hardcore() != effective) {
            resolved.player().setHardcore(effective);
            resolved.data().setDirty();
        }
        ServerPlayer online = server.getPlayerList().getPlayer(playerId);
        if (online != null) {
            sync(online, true);
        }
    }

    public static void handleDeath(ServerPlayer player) {
        MinecraftServer server = player.server;
        ResolvedState resolved = resolve(server, player.getUUID());
        if (!RaifuConfig.ENABLED.get() || !resolved.player().hardcore()) {
            return;
        }

        int remaining = Math.max(0, getLives(server, player.getUUID()) - 1);
        setLives(server, player.getUUID(), remaining);
        resolved = resolve(server, player.getUUID());
        if (remaining == 0 && !resolved.player().eliminated()) {
            resolved.player().setEliminated(true);
            resolved.data().setDirty();
        }
        syncAll(server, true);
    }

    public static boolean isEliminated(MinecraftServer server, UUID playerId) {
        return RaifuConfig.ENABLED.get() && resolve(server, playerId).player().eliminated();
    }

    public static void reconcileAndSync(MinecraftServer server) {
        if (!RaifuConfig.ENABLED.get()) {
            RaifuSavedData data = RaifuSavedData.get(server);
            data.clearHardcoreToggles();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                RaifuSavedData.PlayerState state = data.player(player.getUUID());
                if (state.eliminated() && player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                    player.setGameMode(server.getDefaultGameType());
                }
                if (state.eliminated()) {
                    state.setEliminated(false);
                    data.setDirty();
                }
                sync(player, false);
            }
            return;
        }

        syncAll(server, false);
    }

    public static void handleLogin(ServerPlayer player) {
        if (!RaifuConfig.ENABLED.get()) {
            RaifuSavedData.PlayerState state = RaifuSavedData.get(player.server).player(player.getUUID());
            boolean wasEliminated = state.eliminated();
            if (state.hardcore() || state.eliminated()) {
                state.setHardcore(false);
                state.setEliminated(false);
                RaifuSavedData.get(player.server).setDirty();
            }
            if (wasEliminated && player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
                player.setGameMode(player.server.getDefaultGameType());
            }
        }
        resolve(player.server, player.getUUID());
        sync(player, true);
    }

    public static void handleLogout(ServerPlayer player) {
        LAST_SENT.remove(player.getUUID());
    }

    public static void handleRespawn(ServerPlayer player) {
        if (isEliminated(player.server, player.getUUID())) {
            player.setGameMode(GameType.SPECTATOR);
        }
        sync(player, true);
    }

    public static void sync(ServerPlayer player, boolean force) {
        LifeStatePayload payload = snapshot(player);
        if (force || !payload.equals(LAST_SENT.get(player.getUUID()))) {
            PacketDistributor.sendToPlayer(player, payload);
            LAST_SENT.put(player.getUUID(), payload);
        }
    }

    private static void syncAll(MinecraftServer server, boolean force) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sync(player, force);
        }
    }

    private static LifeStatePayload snapshot(ServerPlayer player) {
        boolean enabled = RaifuConfig.ENABLED.get();
        ResolvedState resolved = resolve(player.server, player.getUUID());
        int lives = resolved.partyId() == null
                ? resolved.player().lives()
                : resolved.data().partyLives(resolved.partyId());
        if (lives > 0 && resolved.player().eliminated()) {
            resolved.player().setEliminated(false);
            resolved.data().setDirty();
        }
        return new LifeStatePayload(
                enabled,
                enabled && resolved.player().hardcore(),
                lives,
                enabled && resolved.player().eliminated()
        );
    }

    private static ResolvedState resolve(MinecraftServer server, UUID playerId) {
        RaifuSavedData data = RaifuSavedData.get(server);
        RaifuSavedData.PlayerState player = data.player(playerId);
        if (!RaifuConfig.ENABLED.get()) {
            return new ResolvedState(data, player, player.partyId());
        }
        Optional<UUID> currentParty = PartyIntegration.findParty(server, playerId);

        if (currentParty.isPresent()) {
            UUID partyId = currentParty.get();
            if (!partyId.equals(player.partyId())) {
                int mergedLives = Mth.clamp(data.partyLives(partyId) + player.lives(), 0, MAX_LIVES);
                data.setPartyLives(partyId, mergedLives);
                player.setLives(0);
                player.setPartyId(partyId);
                data.setDirty();
            }
            return new ResolvedState(data, player, partyId);
        }

        if (player.partyId() != null) {
            player.setLives(0);
            player.setPartyId(null);
            data.setDirty();
        }
        return new ResolvedState(data, player, null);
    }

    private record ResolvedState(
            RaifuSavedData data,
            RaifuSavedData.PlayerState player,
            UUID partyId
    ) {
    }
}
