package com.endgineer.raifu.client;

import com.endgineer.raifu.Raifu;
import com.endgineer.raifu.config.RaifuClientConfig;
import com.endgineer.raifu.server.LifeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Raifu.MOD_ID, value = Dist.CLIENT)
public final class LifeHudRenderer {
    private static final ResourceLocation HEART_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(Raifu.MOD_ID, "textures/gui/heart.png");
    private static final int HEART_SIZE = 16;

    private LifeHudRenderer() {
    }

    @SubscribeEvent
    public static void onRenderGuiLayer(RenderGuiLayerEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)
                || minecraft.options.hideGui
                || minecraft.player == null
                || !ClientLifeState.enabled()) {
            return;
        }

        int hearts = Math.min(ClientLifeState.lives(), LifeManager.MAX_LIVES);
        if (hearts <= 0) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        int spacing = RaifuClientConfig.HEART_SPACING.get();
        int totalWidth = HEART_SIZE + (hearts - 1) * spacing;
        int x = anchoredX(graphics.guiWidth(), totalWidth);
        int y = anchoredY(graphics.guiHeight());

        for (int i = 0; i < hearts; i++) {
            graphics.blit(
                    HEART_TEXTURE,
                    x + i * spacing,
                    y,
                    0.0F,
                    0.0F,
                    HEART_SIZE,
                    HEART_SIZE,
                    HEART_SIZE,
                    HEART_SIZE
            );
        }
    }

    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientLifeState.reset();
    }

    private static int anchoredX(int screenWidth, int totalWidth) {
        int offset = RaifuClientConfig.X_OFFSET.get();
        return switch (RaifuClientConfig.HORIZONTAL_ANCHOR.get()) {
            case LEFT -> offset;
            case CENTER -> screenWidth / 2 - totalWidth / 2 + offset;
            case RIGHT -> screenWidth - totalWidth - offset;
        };
    }

    private static int anchoredY(int screenHeight) {
        int offset = RaifuClientConfig.Y_OFFSET.get();
        return switch (RaifuClientConfig.VERTICAL_ANCHOR.get()) {
            case TOP -> offset;
            case CENTER -> screenHeight / 2 - HEART_SIZE / 2 + offset;
            case BOTTOM -> screenHeight - HEART_SIZE - offset;
        };
    }
}
