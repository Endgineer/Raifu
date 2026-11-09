package com.endgineer.raifu.mixin.client;

import com.endgineer.raifu.client.ClientLifeState;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @ModifyExpressionValue(
            method = "renderHearts",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/LevelData;isHardcore()Z"
            )
    )
    private boolean raifu$renderHardcoreHearts(boolean vanillaHardcore) {
        return vanillaHardcore || ClientLifeState.hardcore();
    }
}
