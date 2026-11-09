package com.endgineer.raifu.compat;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;

final class OpenPacPartyLookup implements PartyLookup {
    @Override
    public Optional<UUID> findParty(MinecraftServer server, UUID playerId) {
        OpenPACServerAPI api = OpenPACServerAPI.get(server);
        if (api == null) {
            return Optional.empty();
        }
        IServerPartyAPI party = api.getPartyManager().getPartyByMember(playerId);
        return party == null ? Optional.empty() : Optional.of(party.getId());
    }
}
