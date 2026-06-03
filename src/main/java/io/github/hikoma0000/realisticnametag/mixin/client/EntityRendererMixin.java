package io.github.hikoma0000.realisticnametag.mixin.client;

import io.github.hikoma0000.realisticnametag.client.DelayedNametagRenderer;
import io.github.hikoma0000.realisticnametag.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;

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

    @ModifyVariable(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MultiBufferSource modifyBufferSource(MultiBufferSource original) {
        if (shouldApplyRealisticNametag()) {
            return DelayedNametagRenderer.INSTANCE;
        }
        return original;
    }


    @Redirect(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
                    ordinal = 0
            )
    )
    private int redirectFirstDrawInBatch(Font font, Component text, float x, float y, int color, boolean dropShadow, Matrix4f pose, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLightCoords) {
        if (shouldApplyRealisticNametag()) {
            if (backgroundColor != 0) {
                VertexConsumer vertexConsumer = DelayedNametagRenderer.INSTANCE.getBuffer(RenderType.textBackground());
                float x1 = x - 1;
                float x2 = x + font.width(text) + 1;
                float y1 = y - 1;
                float y2 = y + 8 + 1;
                float z = -0.01F;

                int alpha = (backgroundColor >> 24) & 0xFF;
                vertexConsumer.addVertex(pose, x1, y1, z).setColor(0, 0, 0, alpha).setLight(packedLightCoords);
                vertexConsumer.addVertex(pose, x1, y2, z).setColor(0, 0, 0, alpha).setLight(packedLightCoords);
                vertexConsumer.addVertex(pose, x2, y2, z).setColor(0, 0, 0, alpha).setLight(packedLightCoords);
                vertexConsumer.addVertex(pose, x2, y1, z).setColor(0, 0, 0, alpha).setLight(packedLightCoords);
            }
            return font.drawInBatch(text, x, y, -1, false, pose, DelayedNametagRenderer.INSTANCE, Font.DisplayMode.NORMAL, 0, packedLightCoords);
        }
        return font.drawInBatch(text, x, y, color, dropShadow, pose, bufferSource, displayMode, backgroundColor, packedLightCoords);
    }

    @Redirect(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;drawInBatch(Lnet/minecraft/network/chat/Component;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I",
                    ordinal = 1
            )
    )
    private int redirectSecondDrawInBatch(Font font, Component text, float x, float y, int color, boolean dropShadow, Matrix4f pose, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int backgroundColor, int packedLightCoords) {
        if (shouldApplyRealisticNametag()) {
            return 0;
        }
        return font.drawInBatch(text, x, y, color, dropShadow, pose, bufferSource, displayMode, backgroundColor, packedLightCoords);
    }
}