package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.shader.Framebuffer;

import java.util.LinkedHashMap;
import java.util.Map;

public class DelayedNametagRenderer implements IRenderTypeBuffer {
    public static final DelayedNametagRenderer INSTANCE = new DelayedNametagRenderer();
    public static boolean isFlushing = false;

    private final Map<RenderType, BufferBuilder> buffers = new LinkedHashMap<>();

    private DelayedNametagRenderer() {
    }

    @Override
    public IVertexBuilder getBuffer(RenderType renderType) {
        BufferBuilder builder = buffers.computeIfAbsent(renderType, rt -> new BufferBuilder(rt.bufferSize()));
        if (!builder.building()) {
            builder.begin(renderType.mode(), renderType.format());
        }
        return builder;
    }

    public void flush() {
        boolean anyBuilding = false;
        for (BufferBuilder builder : buffers.values()) {
            if (builder.building()) {
                anyBuilding = true;
                break;
            }
        }
        if (!anyBuilding) {
            return;
        }

        isFlushing = true;
        Minecraft minecraft = Minecraft.getInstance();
        boolean fabulous = Minecraft.useShaderTransparency();
        try {
            if (fabulous) {
                Framebuffer translucentTarget = minecraft.levelRenderer.getTranslucentTarget();
                if (translucentTarget != null) {
                    translucentTarget.bindWrite(false);
                } else {
                    fabulous = false;
                }
            }

            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(515);

            for (Map.Entry<RenderType, BufferBuilder> entry : buffers.entrySet()) {
                RenderType type = entry.getKey();
                BufferBuilder builder = entry.getValue();
                if (!builder.building()) {
                    continue;
                }

                builder.end();
                type.setupRenderState();
                RenderSystem.disableFog();
                RenderSystem.disableCull();
                WorldVertexBufferUploader.end(builder);
                type.clearRenderState();
            }
        } finally {
            if (fabulous) {
                minecraft.getMainRenderTarget().bindWrite(false);
            }
            isFlushing = false;
        }
    }
}
