package com.endgineer.raifu.compat;

import com.endgineer.raifu.Raifu;
import com.endgineer.raifu.config.RaifuConfig;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModList;

public final class PartyIntegration {
    private static final String OPEN_PARTIES_AND_CLAIMS = "openpartiesandclaims";
    private static PartyLookup lookup;

    private PartyIntegration() {
    }

    public static Optional<UUID> findParty(MinecraftServer server, UUID playerId) {
        if (!RaifuConfig.PARTY_LIFE_POOLING.get()) {
            return Optional.empty();
        }
        return lookup().findParty(server, playerId);
    }

    private static PartyLookup lookup() {
        if (lookup != null) {
            return lookup;
        }

        if (!ModList.get().isLoaded(OPEN_PARTIES_AND_CLAIMS)) {
            lookup = PartyLookup.NONE;
            return lookup;
        }

        try {
            lookup = (PartyLookup) Class.forName("com.endgineer.raifu.compat.OpenPacPartyLookup")
                    .getDeclaredConstructor()
                    .newInstance();
            Raifu.LOGGER.info("Enabled Open Parties and Claims life pooling integration");
        } catch (ReflectiveOperationException | LinkageError exception) {
            Raifu.LOGGER.error("Could not initialize Open Parties and Claims integration", exception);
            lookup = PartyLookup.NONE;
        }
        return lookup;
    }
}
