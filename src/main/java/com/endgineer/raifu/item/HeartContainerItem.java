package com.endgineer.raifu.item;

import com.endgineer.raifu.api.RaifuApi;
import com.endgineer.raifu.client.ClientLifeState;
import com.endgineer.raifu.config.RaifuConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class HeartContainerItem extends Item {
    private static final int USE_DURATION_TICKS = 32;

    public HeartContainerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide
                && (!ClientLifeState.enabled() || ClientLifeState.lives() >= RaifuApi.MAX_LIVES)) {
            return InteractionResultHolder.fail(stack);
        }
        if (player instanceof ServerPlayer serverPlayer
                && (!RaifuConfig.ENABLED.get()
                || RaifuApi.getLives(serverPlayer) >= RaifuApi.MAX_LIVES)) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_DURATION_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!(livingEntity instanceof ServerPlayer player)
                || !RaifuConfig.ENABLED.get()
                || RaifuApi.getLives(player) >= RaifuApi.MAX_LIVES) {
            return stack;
        }

        RaifuApi.addLives(player, 1);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.PLAYER_LEVELUP,
                SoundSource.PLAYERS,
                0.9F,
                1.1F
        );
        return stack;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            TooltipContext context,
            java.util.List<Component> tooltipComponents,
            TooltipFlag tooltipFlag
    ) {
        tooltipComponents.add(Component.translatable("item.raifu.heart_container.tooltip"));
    }
}
