package com.endgineer.raifu.compat;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;

interface PartyLookup {
    PartyLookup NONE = (server, playerId) -> Optional.empty();

    Optional<UUID> findParty(MinecraftServer server, UUID playerId);
}
