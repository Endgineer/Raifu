package com.endgineer.raifu.event;

import com.endgineer.raifu.Raifu;
import com.endgineer.raifu.loot.ConfiguredHeartContainerChanceCondition;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;

@EventBusSubscriber(modid = Raifu.MOD_ID)
public final class RaifuLootEvents {
    private static final String POOL_NAME = "raifu_heart_container";

    private RaifuLootEvents() {
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if (!event.getName().getPath().startsWith("chests/")) {
            return;
        }

        LootPool pool = LootPool.lootPool()
                .name(POOL_NAME)
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(Raifu.HEART_CONTAINER.get()))
                .when(ConfiguredHeartContainerChanceCondition.configuredChance())
                .build();
        event.getTable().addPool(pool);
    }
}
