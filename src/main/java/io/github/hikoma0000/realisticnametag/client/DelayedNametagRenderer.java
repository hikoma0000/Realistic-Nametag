package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import io.github.hikoma0000.realisticnametag.RealisticNametag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = RealisticNametag.MOD_ID, value = Dist.CLIENT)
public class DelayedNametagRenderer implements MultiBufferSource {
    public static final DelayedNametagRenderer INSTANCE = new DelayedNametagRenderer();
    public static boolean isFlushing = false;

    private final Map<RenderType, ByteBufferBuilder> byteBufferBuilders = new LinkedHashMap<>();
    private final Map<RenderType, BufferBuilder> builders = new LinkedHashMap<>();

    private DelayedNametagRenderer() {
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        BufferBuilder builder = builders.get(renderType);
        if (builder == null) {
            ByteBufferBuilder byteBufferBuilder = byteBufferBuilders.computeIfAbsent(renderType,
                    rt -> new ByteBufferBuilder(1536));
            builder = new BufferBuilder(byteBufferBuilder, renderType.mode(), renderType.format());
            builders.put(renderType, builder);
        }
        return builder;
    }

    public void flush() {
        if (builders.isEmpty())
            return;

        isFlushing = true;
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);

        List<RenderType> types = new ArrayList<>(builders.keySet());
        types.sort((t1, t2) -> {
            boolean isT1Intensity = t1.toString().contains("intensity");
            boolean isT2Intensity = t2.toString().contains("intensity");
            if (isT1Intensity && !isT2Intensity)
                return 1;
            if (!isT1Intensity && isT2Intensity)
                return -1;
            return t1.toString().compareTo(t2.toString());
        });

        for (RenderType renderType : types) {
            BufferBuilder builder = builders.get(renderType);
            MeshData meshData = builder.build();
            if (meshData != null) {
                if (renderType.sortOnUpload()) {
                    ByteBufferBuilder byteBufferBuilder = byteBufferBuilders.get(renderType);
                    meshData.sortQuads(byteBufferBuilder, RenderSystem.getVertexSorting());
                }
                renderType.draw(meshData);
            }
        }

        builders.clear();
        isFlushing = false;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            INSTANCE.flush();
        }
    }
}
