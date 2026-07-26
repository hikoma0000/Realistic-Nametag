package io.github.hikoma0000.realisticnametag.mixin.client;

import io.github.hikoma0000.realisticnametag.client.DelayedNametagRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    private void realisticnametag$afterChunkLayer(
            RenderType renderType,
            MatrixStack matrixStack,
            double x,
            double y,
            double z,
            CallbackInfo ci
    ) {
        if (renderType == RenderType.translucent()) {
            DelayedNametagRenderer.INSTANCE.flush();
        }
    }
}
