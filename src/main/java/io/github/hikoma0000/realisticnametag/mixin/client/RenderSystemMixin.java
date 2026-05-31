package io.github.hikoma0000.realisticnametag.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.hikoma0000.realisticnametag.client.DelayedNametagRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class RenderSystemMixin {

    @Inject(method = "disableDepthTest", at = @At("HEAD"), cancellable = true)
    private static void onDisableDepthTest(CallbackInfo ci) {
        if (DelayedNametagRenderer.isFlushing) {
            ci.cancel();
        }
    }
}
