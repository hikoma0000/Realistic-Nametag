package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.hikoma0000.realisticnametag.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class NametagRenderEvents {

    @SubscribeEvent
    public void onRenderNameplate(RenderNameplateEvent event) {
        if (ClientConfig.DISABLE_MOD.get()) return;

        PlayerEntity localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;

        if (localPlayer.isSpectator() && ClientConfig.DISABLE_IN_SPECTATOR.get()) return;



        Entity entity = event.getEntity();
        if (shouldShowNameTag(entity, localPlayer)) {
            event.setResult(Event.Result.DENY);
            ITextComponent nameToDisplay = getNameToDisplay(entity, event.getContent());
            if (nameToDisplay != null) {
                renderNametagWithDepth(entity, nameToDisplay, event.getMatrixStack(), event.getRenderTypeBuffer(), event.getPackedLight());
            }
        }
    }



    private boolean shouldShowNameTag(Entity entity, PlayerEntity localPlayer) {
        if (entity == localPlayer) return false;

        if (entity instanceof PlayerEntity) {
            return !entity.isInvisibleTo(localPlayer) && isTeamVisible((LivingEntity) entity, localPlayer);
        }

        else if (entity instanceof ItemFrameEntity) {
            ItemFrameEntity frame = (ItemFrameEntity) entity;
            if (Minecraft.getInstance().crosshairPickEntity == frame) {
                ItemStack stack = frame.getItem();
                return !stack.isEmpty() && stack.hasCustomHoverName();
            }
            return false;
        }

        else if (entity instanceof MobEntity) {
            MobEntity mob = (MobEntity) entity;
            if (mob.isVehicle()) {
                return false;
            }

            if (mob.isInvisibleTo(localPlayer) || !isTeamVisible(mob, localPlayer)) {
                return false;
            }

            return mob.isCustomNameVisible() || (Minecraft.getInstance().crosshairPickEntity == mob && mob.hasCustomName());
        }

        else if (entity instanceof EnderDragonEntity) {
            return entity.hasCustomName() && entity.isCustomNameVisible();
        }

        else if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            return livingEntity.isCustomNameVisible();
        }

        else {
            return entity.hasCustomName() && entity.isCustomNameVisible();
        }
    }



    private ITextComponent getNameToDisplay(Entity entity, ITextComponent originalName) {
        if (entity instanceof ItemFrameEntity) {
            ItemFrameEntity frame = (ItemFrameEntity) entity;
            ItemStack stack = frame.getItem();
            if (!stack.isEmpty() && stack.hasCustomHoverName()) {
                return stack.getHoverName();
            }
            return null;
        }
        return originalName;
    }



    private boolean isTeamVisible(LivingEntity entity, PlayerEntity viewer) {
        Team entityTeam = entity.getTeam();
        if (entityTeam == null) return true;
        Team.Visible visibility = entityTeam.getNameTagVisibility();
        Scoreboard viewerBoard = viewer.getScoreboard();
        Team viewerTeam = viewerBoard.getPlayersTeam(viewer.getScoreboardName());

        switch (visibility) {
            case NEVER:
                return false;
            case HIDE_FOR_OTHER_TEAMS:
                return entityTeam.isAlliedTo(viewerTeam);
            case HIDE_FOR_OWN_TEAM:
                return !entityTeam.isAlliedTo(viewerTeam);
            case ALWAYS:
            default:
                return true;
        }
    }



    private void renderNametagWithDepth(Entity entity, ITextComponent component, MatrixStack matrixStack, IRenderTypeBuffer bufferSource, int packedLight) {
        if (component.getString().isEmpty()) return;

        matrixStack.pushPose();
        matrixStack.translate(0.0D, entity.getBbHeight() + 0.5F, 0.0D);
        matrixStack.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
        matrixStack.scale(-0.025F, -0.025F, 0.025F);

        Matrix4f matrix4f = matrixStack.last().pose();
        FontRenderer font = Minecraft.getInstance().font;
        float textWidth = font.width(component);
        float textOffset = -textWidth / 2.0f;

        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int) (backgroundOpacity * 255.0F) << 24;

        IVertexBuilder vertexConsumer = bufferSource.getBuffer(CustomRenderTypes.REALISTIC_TEXT_BACKGROUND);
        float x1 = textOffset - 1.0F, y1 = -1.0F;
        float x2 = textOffset + textWidth + 1.0F, y2 = 9.0F;

        vertexConsumer.vertex(matrix4f, x1, y1, 0).color(0, 0, 0, (backgroundColor >> 24) & 0xFF).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x1, y2, 0).color(0, 0, 0, (backgroundColor >> 24) & 0xFF).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y2, 0).color(0, 0, 0, (backgroundColor >> 24) & 0xFF).uv2(packedLight).endVertex();
        vertexConsumer.vertex(matrix4f, x2, y1, 0).color(0, 0, 0, (backgroundColor >> 24) & 0xFF).uv2(packedLight).endVertex();

        matrixStack.translate(0.0D, 0.0D, -0.05D);
        font.drawInBatch(component, textOffset, 0.0F, -1, false, matrixStack.last().pose(), bufferSource, false, 0, packedLight);

        matrixStack.popPose();
    }
}