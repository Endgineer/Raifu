package com.endgineer.raifu.loot;

import com.endgineer.raifu.Raifu;
import com.endgineer.raifu.config.RaifuConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * Evaluates the configured chance when chest loot is generated. Loot tables
 * load before NeoForge's per-world server config, so their construction must
 * not read that config.
 */
public final class ConfiguredHeartContainerChanceCondition implements LootItemCondition {
    public static final ConfiguredHeartContainerChanceCondition INSTANCE =
            new ConfiguredHeartContainerChanceCondition();
    public static final MapCodec<ConfiguredHeartContainerChanceCondition> CODEC = MapCodec.unit(INSTANCE);

    private ConfiguredHeartContainerChanceCondition() {
    }

    public static Builder configuredChance() {
        return () -> INSTANCE;
    }

    @Override
    public LootItemConditionType getType() {
        return Raifu.CONFIGURED_HEART_CONTAINER_CHANCE.get();
    }

    @Override
    public boolean test(LootContext context) {
        double chance = RaifuConfig.SPEC.isLoaded()
                ? RaifuConfig.HEART_CONTAINER_CHEST_CHANCE.get()
                : RaifuConfig.HEART_CONTAINER_CHEST_CHANCE.getDefault();
        return chance > 0.0D && context.getRandom().nextDouble() < chance;
    }
}
