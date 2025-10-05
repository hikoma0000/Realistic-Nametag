package io.github.hikoma0000.realisticnametag.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue DISABLE_MOD;
    public static final ModConfigSpec.BooleanValue DISABLE_IN_SPECTATOR;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("General settings for Realistic Nametag").push("general");

        DISABLE_MOD = builder
                .comment("Set to true to completely disable the mod's features.")
                .translation("realisticnametag.config.disableMOD")
                .define("disableMOD", false);

        DISABLE_IN_SPECTATOR = builder
                .comment("If true, the mod's features will be disabled while in spectator mode.")
                .translation("realisticnametag.config.disableInSpectator")
                .define("disableInSpectator", true);

        builder.pop();

        SPEC = builder.build();
    }
}