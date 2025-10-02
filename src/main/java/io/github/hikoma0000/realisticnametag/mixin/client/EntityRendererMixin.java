package io.github.hikoma0000.realisticnametag.mixin.client;

import io.github.hikoma0000.realisticnametag.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
    @ModifyArg(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"
            ),
            index = 7
    )
    private Font.DisplayMode conditionallyForceDepthTest(Font.DisplayMode originalMode) {
        if (ServerConfig.DISABLE_MOD.get()) {
            return originalMode;
        }

        Player player = Minecraft.getInstance().player;

        if (player != null && player.isSpectator() && ServerConfig.DISABLE_IN_SPECTATOR.get()) {
            return originalMode;
        }

        return Font.DisplayMode.NORMAL;
    }
}