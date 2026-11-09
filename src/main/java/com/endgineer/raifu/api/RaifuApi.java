package com.endgineer.raifu.api;

import com.endgineer.raifu.server.LifeManager;
import java.util.Set;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * Public server-side API for mods that integrate with Raifu.
 */
public final class RaifuApi {
    public static final int MAX_LIVES = LifeManager.MAX_LIVES;

    private RaifuApi() {
    }

    public static boolean isHardcore(ServerPlayer player) {
        return LifeManager.isHardcore(player.server, player.getUUID());
    }

    public static Set<ResourceLocation> getHardcoreSources(ServerPlayer player) {
        return LifeManager.getHardcoreSources(player.server, player.getUUID());
    }

    public static Set<ResourceLocation> getHardcoreSources(MinecraftServer server, UUID playerId) {
        return LifeManager.getHardcoreSources(server, playerId);
    }

    public static void setHardcore(
            ServerPlayer player,
            ResourceLocation source,
            boolean hardcore
    ) {
        LifeManager.setHardcore(player.server, player.getUUID(), source, hardcore);
    }

    public static void setHardcore(
            MinecraftServer server,
            UUID playerId,
            ResourceLocation source,
            boolean hardcore
    ) {
        LifeManager.setHardcore(server, playerId, source, hardcore);
    }

    public static int getLives(ServerPlayer player) {
        return LifeManager.getLives(player.server, player.getUUID());
    }

    public static int getLives(MinecraftServer server, UUID playerId) {
        return LifeManager.getLives(server, playerId);
    }

    public static int setLives(ServerPlayer player, int lives) {
        return LifeManager.setLives(player.server, player.getUUID(), lives);
    }

    public static int setLives(MinecraftServer server, UUID playerId, int lives) {
        return LifeManager.setLives(server, playerId, lives);
    }

    public static int addLives(ServerPlayer player, int amount) {
        return LifeManager.addLives(player.server, player.getUUID(), amount);
    }
}
