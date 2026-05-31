package io.github.hikoma0000.realisticnametag.mixin.client;

import io.github.hikoma0000.realisticnametag.client.DelayedNametagRenderer;
import io.github.hikoma0000.realisticnametag.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.client.renderer.MultiBufferSource;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    private boolean shouldApplyRealisticNametag() {
        if (ServerConfig.DISABLE_MOD.get()) {
            return false;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null && player.isSpectator() && ServerConfig.DISABLE_IN_SPECTATOR.get()) {
            return false;
        }

        return true;
    }

    @ModifyVariable(method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private MultiBufferSource modifyBufferSource(MultiBufferSource originalBuffer) {
        if (shouldApplyRealisticNametag()) {
            return DelayedNametagRenderer.INSTANCE;
        }
        return originalBuffer;
    }

    @ModifyArg(method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I"), index = 7)
    private Font.DisplayMode conditionallyForceDepthTest(Font.DisplayMode originalMode) {
        if (shouldApplyRealisticNametag()) {
            return Font.DisplayMode.NORMAL;
        }
        return originalMode;
    }
}