package io.github.hikoma0000.realisticnametag.mixin.client.compat;

import io.github.hikoma0000.realisticnametag.client.DelayedNametagRenderer;
import io.github.hikoma0000.realisticnametag.config.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "de.maxhenkel.voicechat.voice.client.RenderEvents", remap = false)
public class VoiceChatCompatMixin {

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
            method = "onRenderName",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private MultiBufferSource modifyVoiceChatBufferSource(MultiBufferSource originalBuffer) {
        if (shouldApplyRealisticNametag()) {
            return DelayedNametagRenderer.INSTANCE;
        }
        return originalBuffer;
    }
}
