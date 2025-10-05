package io.github.hikoma0000.realisticnametag.network;

import io.github.hikoma0000.realisticnametag.RealisticNametag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ModPresenceChecker() implements CustomPacketPayload {
    public static final ModPresenceChecker INSTANCE = new ModPresenceChecker();
    public static final CustomPacketPayload.Type<ModPresenceChecker> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RealisticNametag.MOD_ID, "presence_check"));
    public static final StreamCodec<FriendlyByteBuf, ModPresenceChecker> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}