package com.endgineer.raifu;

import com.endgineer.raifu.config.RaifuClientConfig;
import com.endgineer.raifu.config.RaifuConfig;
import com.endgineer.raifu.item.HeartContainerItem;
import com.endgineer.raifu.loot.ConfiguredHeartContainerChanceCondition;
import com.endgineer.raifu.network.RaifuNetwork;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(Raifu.MOD_ID)
public final class Raifu {
  public static final String MOD_ID = "raifu";
  public static final Logger LOGGER = LogUtils.getLogger();

  public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
  public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES =
      DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MOD_ID);

  public static final DeferredItem<HeartContainerItem> HEART_CONTAINER =
      ITEMS.register("heart_container",
          () -> new HeartContainerItem(new net.minecraft.world.item.Item.Properties().stacksTo(1)));

  public static final DeferredHolder<LootItemConditionType, LootItemConditionType>
      CONFIGURED_HEART_CONTAINER_CHANCE =
          LOOT_CONDITION_TYPES.register("configured_heart_container_chance",
              () -> new LootItemConditionType(ConfiguredHeartContainerChanceCondition.CODEC));

  public Raifu(IEventBus modEventBus, ModContainer modContainer) {
    ITEMS.register(modEventBus);
    LOOT_CONDITION_TYPES.register(modEventBus);
    modEventBus.addListener(this::addCreativeTabContents);
    modEventBus.addListener(RaifuNetwork::registerPayloads);

    modContainer.registerConfig(ModConfig.Type.SERVER, RaifuConfig.SPEC);
    modContainer.registerConfig(ModConfig.Type.CLIENT, RaifuClientConfig.SPEC);
  }

  private void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
    if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
      event.accept(HEART_CONTAINER);
    }
  }
}
