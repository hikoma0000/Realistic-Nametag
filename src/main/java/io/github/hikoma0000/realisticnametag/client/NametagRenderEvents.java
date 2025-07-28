package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.hikoma0000.realisticnametag.client.CustomRenderTypes;
import io.github.hikoma0000.realisticnametag.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

public class NametagRenderEvents {

    @SubscribeEvent
    public void onRenderNameTag(RenderNameTagEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.isSpectator() && ClientConfig.DISABLE_IN_SPECTATOR.get()) {
                return;
            }

            boolean shouldShow = this.shouldShowNameplate(entity);

            if (shouldShow) {
                event.setResult(Event.Result.DENY);
                renderNametagWithDepth(
                        entity,
                        event.getContent(),
                        event.getPoseStack(),
                        event.getMultiBufferSource(),
                        event.getPackedLight()
                );
            } else {
                event.setResult(Event.Result.DENY);
            }
        }
    }


    private boolean shouldShowNameplate(LivingEntity entity) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || entity == player || entity.isVehicle() || entity == minecraft.getCameraEntity()) {
            return false;
        }

        double distanceSq = entity.distanceToSqr(player);
        float renderDistance = entity.isDiscrete() ? 32.0F : 64.0F;
        if (distanceSq >= (renderDistance * renderDistance)) {
            return false;
        }

        boolean isVisible = !entity.isInvisibleTo(player);
        if (!Minecraft.renderNames()) {
            return false;
        }

        if (entity.getTeam() != null) {
            Team entityTeam = entity.getTeam();
            Team playerTeam = player.getTeam();
            Team.Visibility visibility = entityTeam.getNameTagVisibility();

            switch (visibility) {
                case ALWAYS:
                    return isVisible;
                case NEVER:
                    return false;
                case HIDE_FOR_OTHER_TEAMS:
                    return playerTeam != null && entityTeam.isAlliedTo(playerTeam) && (entityTeam.canSeeFriendlyInvisibles() || isVisible);
                case HIDE_FOR_OWN_TEAM:
                    return (playerTeam == null || !entityTeam.isAlliedTo(playerTeam)) && isVisible;
            }
        }

        return isVisible;
    }

    private void renderNametagWithDepth(LivingEntity entity, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (component.getString().isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() + 0.5F, 0.0D);

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        poseStack.mulPose(dispatcher.cameraOrientation());

        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = poseStack.last().pose();
        Font font = Minecraft.getInstance().font;
        float textWidth = font.width(component);
        float textOffset = -textWidth / 2.0f;

        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(CustomRenderTypes.REALISTIC_TEXT_BACKGROUND);

        float x1 = textOffset - 1.0F;
        float y1 = -1.0F;
        float x2 = textOffset + textWidth + 1.0F;
        float y2 = 9.0F;
        float z = 0.0F;

        vertexConsumer.vertex(matrix4f, x1, y1, z).color(backgroundColor).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y2, z).color(backgroundColor).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y2, z).color(backgroundColor).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y1, z).color(backgroundColor).uv2(packedLight).endVertex();

        poseStack.translate(0.0D, 0.0D, -0.05D);

        font.drawInBatch(component, textOffset, 0.0F, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }
}