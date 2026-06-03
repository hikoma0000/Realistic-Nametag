package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import io.github.hikoma0000.realisticnametag.RealisticNametag;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = RealisticNametag.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DelayedNametagRenderer implements IRenderTypeBuffer {
    public static final DelayedNametagRenderer INSTANCE = new DelayedNametagRenderer();
    public static boolean isFlushing = false;

    private final Map<RenderType, BufferBuilder> buffers = new LinkedHashMap<>();

    private DelayedNametagRenderer() {
    }

    @Override
    public IVertexBuilder getBuffer(RenderType renderType) {
        BufferBuilder builder = buffers.get(renderType);
        if (builder == null) {
            builder = new BufferBuilder(renderType.bufferSize());
            builder.begin(renderType.mode(), renderType.format());
            buffers.put(renderType, builder);
        }
        return builder;
    }

    public void flush() {
        if (buffers.isEmpty()) return;

        isFlushing = true;
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);

        for (Map.Entry<RenderType, BufferBuilder> entry : buffers.entrySet()) {
            RenderType type = entry.getKey();
            BufferBuilder builder = entry.getValue();

            builder.end();
            type.setupRenderState();
            WorldVertexBufferUploader.end(builder);
            type.clearRenderState();
        }
        buffers.clear();

        isFlushing = false;
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        INSTANCE.flush();
    }
}
