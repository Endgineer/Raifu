package com.endgineer.raifu.network;

import com.endgineer.raifu.Raifu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LifeStatePayload(
        boolean enabled,
        boolean hardcore,
        int lives,
        boolean eliminated
) implements CustomPacketPayload {
    public static final Type<LifeStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Raifu.MOD_ID, "life_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LifeStatePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    LifeStatePayload::enabled,
                    ByteBufCodecs.BOOL,
                    LifeStatePayload::hardcore,
                    ByteBufCodecs.VAR_INT,
                    LifeStatePayload::lives,
                    ByteBufCodecs.BOOL,
                    LifeStatePayload::eliminated,
                    LifeStatePayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
