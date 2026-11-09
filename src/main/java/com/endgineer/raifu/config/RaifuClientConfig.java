package com.endgineer.raifu.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class RaifuClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<HorizontalAnchor> HORIZONTAL_ANCHOR = BUILDER
            .comment("Horizontal screen anchor used by the lives HUD.")
            .defineEnum("horizontalAnchor", HorizontalAnchor.LEFT);

    public static final ModConfigSpec.EnumValue<VerticalAnchor> VERTICAL_ANCHOR = BUILDER
            .comment("Vertical screen anchor used by the lives HUD.")
            .defineEnum("verticalAnchor", VerticalAnchor.TOP);

    public static final ModConfigSpec.IntValue X_OFFSET = BUILDER
            .comment(
                    "Horizontal distance from the selected anchor.",
                    "Positive values move inward from LEFT/RIGHT; CENTER uses normal signed coordinates."
            )
            .defineInRange("xOffset", 8, -10000, 10000);

    public static final ModConfigSpec.IntValue Y_OFFSET = BUILDER
            .comment(
                    "Vertical distance from the selected anchor.",
                    "Positive values move inward from TOP/BOTTOM; CENTER uses normal signed coordinates."
            )
            .defineInRange("yOffset", 8, -10000, 10000);

    public static final ModConfigSpec.IntValue HEART_SPACING = BUILDER
            .comment("Horizontal spacing, in GUI pixels, between heart indicators.")
            .defineInRange("heartSpacing", 18, 0, 64);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private RaifuClientConfig() {
    }

    public enum HorizontalAnchor {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum VerticalAnchor {
        TOP,
        CENTER,
        BOTTOM
    }
}
