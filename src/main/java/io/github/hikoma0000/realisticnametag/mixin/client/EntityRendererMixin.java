package io.github.hikoma0000.realisticnametag.mixin.client;

import io.github.hikoma0000.realisticnametag.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    private boolean shouldApplyRealisticNametag() {
        if (ServerConfig.DISABLE_MOD.get()) {
            return false;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player.isSpectator() && ServerConfig.DISABLE_IN_SPECTATOR.get()) {
            return false;
        }

        return true;
    }

    @org.spongepowered.asm.mixin.injection.ModifyVariable(
            method = "renderNameTag(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/text/ITextComponent;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private net.minecraft.client.renderer.IRenderTypeBuffer modifyBufferSource(net.minecraft.client.renderer.IRenderTypeBuffer originalBuffer) {
        if (shouldApplyRealisticNametag()) {
            return io.github.hikoma0000.realisticnametag.client.DelayedNametagRenderer.INSTANCE;
        }
        return originalBuffer;
    }

    @ModifyArg(
            method = "renderNameTag(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/text/ITextComponent;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawInBatch(Lnet/minecraft/util/text/ITextComponent;FFIZLnet/minecraft/util/math/vector/Matrix4f;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ZII)I",
                    ordinal = 0
            ),
            index = 7
    )
    private boolean conditionallyForceDepthTest(boolean seeThrough) {
        if (shouldApplyRealisticNametag()) {
            return false;
        }
        return seeThrough;
    }
}