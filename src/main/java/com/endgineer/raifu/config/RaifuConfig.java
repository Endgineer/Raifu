package com.endgineer.raifu.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class RaifuConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment(
                    "Master switch for Raifu's hardcore mechanics.",
                    "When false, every player is made non-hardcore, the lives HUD is hidden,",
                    "deaths do not consume lives, and Heart Containers cannot be consumed."
            )
            .define("enabled", true);

    public static final ModConfigSpec.IntValue STARTING_LIVES = BUILDER
            .comment("Number of lives assigned to a player the first time Raifu sees them.")
            .defineInRange("startingLives", 3, 0, 10);

    public static final ModConfigSpec.BooleanValue PARTY_LIFE_POOLING = BUILDER
            .comment(
                    "When Open Parties and Claims is installed, party members share one life pool.",
                    "A player's personal lives are transferred into the pool when they join.",
                    "Leaving does not return any lives; rejoining restores access to the same pool."
            )
            .define("partyLifePooling", true);

    public static final ModConfigSpec.DoubleValue HEART_CONTAINER_CHEST_CHANCE = BUILDER
            .comment(
                    "Chance for a Heart Container to be added to any generated chest loot table.",
                    "The default 0.001 is approximately one container per one thousand generated chests."
            )
            .defineInRange("heartContainerChestChance", 0.001D, 0.0D, 1.0D);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private RaifuConfig() {
    }
}
