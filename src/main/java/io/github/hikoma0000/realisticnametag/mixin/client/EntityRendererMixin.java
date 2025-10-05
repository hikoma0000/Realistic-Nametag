package io.github.hikoma0000.realisticnametag.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.hikoma0000.realisticnametag.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;
    @Shadow protected abstract Font getFont();

    @Inject(
            method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"),
            cancellable = true
    )
    private void modifyNameTagDrawing(T entity, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, CallbackInfo ci, @Local Vec3 vec3) {
        if (ServerConfig.DISABLE_MOD.get()) {
            return;
        }
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.isSpectator() && ServerConfig.DISABLE_IN_SPECTATOR.get()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(vec3.x, vec3.y + 0.5F, vec3.z);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.scale(0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = poseStack.last().pose();
        Font font = this.getFont();
        float textWidth = (float)(-font.width(component) / 2);
        int deadmau5Offset = "deadmau5".equals(component.getString()) ? -10 : 0;

        float backgroundOpacity = minecraft.options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int)(backgroundOpacity * 255.0F);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.textBackground());

        float x1 = textWidth - 1;
        float x2 = textWidth + font.width(component) + 1;
        float y1 = (float)deadmau5Offset - 1;
        float y2 = (float)deadmau5Offset + 8 + 1;
        float z = -0.01F;

        vertexConsumer.addVertex(matrix4f, x1, y1, z).setColor(0, 0, 0, backgroundColor).setLight(packedLight);
        vertexConsumer.addVertex(matrix4f, x1, y2, z).setColor(0, 0, 0, backgroundColor).setLight(packedLight);
        vertexConsumer.addVertex(matrix4f, x2, y2, z).setColor(0, 0, 0, backgroundColor).setLight(packedLight);
        vertexConsumer.addVertex(matrix4f, x2, y1, z).setColor(0, 0, 0, backgroundColor).setLight(packedLight);

        font.drawInBatch(component, textWidth, (float)deadmau5Offset, -1, false, matrix4f, bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();

        ci.cancel();
    }
}