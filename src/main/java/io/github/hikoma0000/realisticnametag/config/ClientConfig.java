package io.github.hikoma0000.realisticnametag.config;

import net.minecraftforge.common.ForgeConfigSpec;


public class ClientConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD;
    public static final ForgeConfigSpec.BooleanValue DISABLE_IN_SPECTATOR;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("General settings for Realistic Nametags").push("general");

        ENABLE_MOD = builder
                .comment("Set to false to completely disable the mod's features.")
                .translation("realisticnametags.config.enableMOD")
                .define("enableMOD", true);

        DISABLE_IN_SPECTATOR = builder
                .comment("If true, the mod's features will be disabled while in spectator mode.")
                .translation("realisticnametags.config.disableInSpectator")
                .define("disableInSpectator", true);

        builder.pop();

        SPEC = builder.build();
    }
}