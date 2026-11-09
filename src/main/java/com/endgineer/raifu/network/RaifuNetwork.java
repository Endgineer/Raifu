package com.endgineer.raifu.network;

import com.endgineer.raifu.client.ClientLifeState;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class RaifuNetwork {
    private RaifuNetwork() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(
                        LifeStatePayload.TYPE,
                        LifeStatePayload.STREAM_CODEC,
                        (payload, context) -> ClientLifeState.update(payload)
                );
    }
}
