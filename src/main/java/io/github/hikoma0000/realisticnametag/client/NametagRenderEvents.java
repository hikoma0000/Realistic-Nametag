package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.hikoma0000.realisticnametag.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Team;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;


public class NametagRenderEvents {

    @SubscribeEvent
    public void onRenderNameTag(RenderNameTagEvent event) {
        if (ClientConfig.DISABLE_MOD.get()) return;

        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;

        if (localPlayer.isSpectator() && ClientConfig.DISABLE_IN_SPECTATOR.get()) return;



        Entity entity = event.getEntity();
        if (this.shouldShowNameTag(entity, localPlayer)) {
            event.setResult(Event.Result.DENY);
            Component nameToShow = this.getNameToDisplay(entity, event.getContent());
            if (nameToShow != null) {
                renderNametagWithDepth(entity, nameToShow, event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
            }
        }
    }



    private boolean shouldShowNameTag(Entity entity, Player localPlayer) {
        if (entity == localPlayer) return false;

        if (entity instanceof Player p) {
            return !p.isInvisibleTo(localPlayer) && isTeamVisible(p, localPlayer);
        }

        else if (entity instanceof ItemFrame frame) {
            if (Minecraft.getInstance().crosshairPickEntity == frame) {
                ItemStack stack = frame.getItem();
                return !stack.isEmpty() && stack.hasCustomHoverName();
            }
            return false;
        }

        else if (entity instanceof Mob mob) {
            if (mob.isVehicle()) {
                return false;
            }

            if (mob.isInvisibleTo(localPlayer) || !isTeamVisible(mob, localPlayer)) {
                return false;
            }

            return mob.isCustomNameVisible() || (Minecraft.getInstance().crosshairPickEntity == mob && mob.hasCustomName());
        }

        else if (entity instanceof EnderDragon) {
            return entity.hasCustomName() && entity.isCustomNameVisible();
        }

        else if (entity instanceof LivingEntity living) {
            return living.isCustomNameVisible();
        }

        else {
            return entity.hasCustomName() && entity.isCustomNameVisible();
        }
    }



    private Component getNameToDisplay(Entity entity, Component originalName) {
        if (entity instanceof ItemFrame frame) {
            ItemStack stack = frame.getItem();
            if (!stack.isEmpty() && stack.hasCustomHoverName()) {
                return stack.getHoverName();
            }
            return null;
        }
        return originalName;
    }



    private boolean isTeamVisible(LivingEntity entity, Player viewer) {
        if (entity.getTeam() == null) return true;
        Team entityTeam = entity.getTeam();
        Team.Visibility visibility = entityTeam.getNameTagVisibility();
        return switch (visibility) {
            case NEVER -> false;
            case HIDE_FOR_OTHER_TEAMS -> entityTeam.isAlliedTo(viewer.getTeam());
            case HIDE_FOR_OWN_TEAM -> !entityTeam.isAlliedTo(viewer.getTeam());
            default -> true;
        };
    }



    private void renderNametagWithDepth(Entity entity, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (component.getString().isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(0.0D, entity.getBbHeight() + 0.5F, 0.0D);
        poseStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = poseStack.last().pose();
        Font font = Minecraft.getInstance().font;
        float textWidth = font.width(component);
        float textOffset = -textWidth / 2.0f;

        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

        VertexConsumer vertexConsumer = bufferSource.getBuffer(CustomRenderTypes.REALISTIC_TEXT_BACKGROUND);
        float x1 = textOffset - 1.0F, y1 = -1.0F;
        float x2 = textOffset + textWidth + 1.0F, y2 = 9.0F;

        vertexConsumer.vertex(matrix4f, x1, y1, 0).color(backgroundColor).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y2, 0).color(backgroundColor).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y2, 0).color(backgroundColor).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y1, 0).color(backgroundColor).uv2(packedLight).endVertex();

        poseStack.translate(0.0D, 0.0D, -0.05D);
        font.drawInBatch(component, textOffset, 0.0F, -1, false, poseStack.last().pose(), bufferSource, Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }
}