package com.endgineer.raifu.data;

import com.endgineer.raifu.config.RaifuConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

public final class RaifuSavedData extends SavedData {
    private static final String DATA_NAME = "raifu_lives";
    private static final Factory<RaifuSavedData> FACTORY =
            new Factory<>(RaifuSavedData::new, RaifuSavedData::load);

    private final Map<UUID, PlayerState> players = new HashMap<>();
    private final Map<UUID, Integer> partyLives = new HashMap<>();

    public static RaifuSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public PlayerState player(UUID playerId) {
        PlayerState existing = players.get(playerId);
        if (existing != null) {
            return existing;
        }

        PlayerState created = new PlayerState(Mth.clamp(RaifuConfig.STARTING_LIVES.get(), 0, 10));
        players.put(playerId, created);
        setDirty();
        return created;
    }

    public int partyLives(UUID partyId) {
        return partyLives.getOrDefault(partyId, 0);
    }

    public void setPartyLives(UUID partyId, int lives) {
        int clamped = Mth.clamp(lives, 0, 10);
        if (partyLives.getOrDefault(partyId, 0) != clamped || !partyLives.containsKey(partyId)) {
            partyLives.put(partyId, clamped);
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag playerList = new ListTag();
        for (Map.Entry<UUID, PlayerState> entry : players.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("Id", entry.getKey());
            playerTag.putInt("Lives", entry.getValue().lives());
            ListTag hardcoreSources = new ListTag();
            entry.getValue().hardcoreSources().stream()
                    .map(ResourceLocation::toString)
                    .sorted()
                    .map(StringTag::valueOf)
                    .forEach(hardcoreSources::add);
            playerTag.put("HardcoreSources", hardcoreSources);
            playerTag.putBoolean("Eliminated", entry.getValue().eliminated());
            if (entry.getValue().partyId() != null) {
                playerTag.putUUID("Party", entry.getValue().partyId());
            }
            playerList.add(playerTag);
        }
        tag.put("Players", playerList);

        ListTag partyList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : partyLives.entrySet()) {
            CompoundTag partyTag = new CompoundTag();
            partyTag.putUUID("Id", entry.getKey());
            partyTag.putInt("Lives", entry.getValue());
            partyList.add(partyTag);
        }
        tag.put("Parties", partyList);
        return tag;
    }

    private static RaifuSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        RaifuSavedData data = new RaifuSavedData();
        ListTag playerList = tag.getList("Players", Tag.TAG_COMPOUND);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag playerTag = playerList.getCompound(i);
            if (!playerTag.hasUUID("Id")) {
                continue;
            }
            PlayerState state = new PlayerState(Mth.clamp(playerTag.getInt("Lives"), 0, 10));
            ListTag hardcoreSources = playerTag.getList("HardcoreSources", Tag.TAG_STRING);
            for (int sourceIndex = 0; sourceIndex < hardcoreSources.size(); sourceIndex++) {
                ResourceLocation source = ResourceLocation.tryParse(hardcoreSources.getString(sourceIndex));
                if (source != null) {
                    state.setHardcore(source, true);
                }
            }
            state.setEliminated(playerTag.getBoolean("Eliminated"));
            if (playerTag.hasUUID("Party")) {
                state.setPartyId(playerTag.getUUID("Party"));
            }
            data.players.put(playerTag.getUUID("Id"), state);
        }

        ListTag partyList = tag.getList("Parties", Tag.TAG_COMPOUND);
        for (int i = 0; i < partyList.size(); i++) {
            CompoundTag partyTag = partyList.getCompound(i);
            if (partyTag.hasUUID("Id")) {
                data.partyLives.put(
                        partyTag.getUUID("Id"),
                        Mth.clamp(partyTag.getInt("Lives"), 0, 10)
                );
            }
        }
        return data;
    }

    public static final class PlayerState {
        private int lives;
        private final Set<ResourceLocation> hardcoreSources = new HashSet<>();
        private boolean eliminated;
        @Nullable
        private UUID partyId;

        private PlayerState(int lives) {
            this.lives = lives;
        }

        public int lives() {
            return lives;
        }

        public void setLives(int lives) {
            this.lives = Mth.clamp(lives, 0, 10);
        }

        public boolean hardcore() {
            return !hardcoreSources.isEmpty();
        }

        public Set<ResourceLocation> hardcoreSources() {
            return Set.copyOf(hardcoreSources);
        }

        public boolean setHardcore(ResourceLocation source, boolean hardcore) {
            Objects.requireNonNull(source, "source");
            return hardcore ? hardcoreSources.add(source) : hardcoreSources.remove(source);
        }

        public boolean eliminated() {
            return eliminated;
        }

        public void setEliminated(boolean eliminated) {
            this.eliminated = eliminated;
        }

        @Nullable
        public UUID partyId() {
            return partyId;
        }

        public void setPartyId(@Nullable UUID partyId) {
            this.partyId = partyId;
        }
    }
}
