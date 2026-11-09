package com.endgineer.raifu.client;

import com.endgineer.raifu.network.LifeStatePayload;

public final class ClientLifeState {
    private static boolean enabled;
    private static boolean hardcore;
    private static int lives;
    private static boolean eliminated;

    private ClientLifeState() {
    }

    public static void update(LifeStatePayload payload) {
        enabled = payload.enabled();
        hardcore = payload.hardcore();
        lives = payload.lives();
        eliminated = payload.eliminated();
    }

    public static void reset() {
        enabled = false;
        hardcore = false;
        lives = 0;
        eliminated = false;
    }

    public static boolean enabled() {
        return enabled;
    }

    public static boolean hardcore() {
        return enabled && hardcore;
    }

    public static int lives() {
        return enabled ? lives : 0;
    }

    public static boolean eliminated() {
        return enabled && eliminated;
    }
}
