package com.endgineer.raifu.mixin.client;

import com.endgineer.raifu.client.ClientLifeState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.multiplayer.ClientLevel$ClientLevelData")
public abstract class ClientLevelDataMixin {
    @Inject(method = "isHardcore", at = @At("RETURN"), cancellable = true)
    private void raifu$useHardcoreDeathScreen(CallbackInfoReturnable<Boolean> callback) {
        if (ClientLifeState.eliminated()) {
            callback.setReturnValue(true);
        }
    }
}
