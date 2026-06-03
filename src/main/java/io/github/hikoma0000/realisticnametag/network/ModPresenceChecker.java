package io.github.hikoma0000.realisticnametag.network;

import io.github.hikoma0000.realisticnametag.RealisticNametag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModPresenceChecker {

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RealisticNametag.MOD_ID, "presence_check"),
            () -> RealisticNametag.PROTOCOL_VERSION,
            RealisticNametag.PROTOCOL_VERSION::equals,
            RealisticNametag.PROTOCOL_VERSION::equals);

    public static void register() {
    }
}