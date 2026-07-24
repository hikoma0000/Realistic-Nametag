package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
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
import java.util.SequencedMap;

@EventBusSubscriber(modid = RealisticNametag.MOD_ID, value = Dist.CLIENT)
public class DelayedNametagRenderer implements MultiBufferSource {
    public static final DelayedNametagRenderer INSTANCE = new DelayedNametagRenderer();
    public static boolean isFlushing = false;

    // Fixed per-type allocators so type switches do not flush before AFTER_TRANSLUCENT_BLOCKS.
    private final SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers = new LinkedHashMap<>();
    private final MultiBufferSource.BufferSource bufferSource =
            MultiBufferSource.immediateWithBuffers(fixedBuffers, new ByteBufferBuilder(256));

    private DelayedNametagRenderer() {
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        fixedBuffers.computeIfAbsent(renderType, rt -> new ByteBufferBuilder(1536));
        return bufferSource.getBuffer(renderType);
    }

    public void flush() {
        isFlushing = true;
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);

        // Draw voice-chat intensity overlays after nametag text/background.
        List<RenderType> types = new ArrayList<>(fixedBuffers.keySet());
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
            bufferSource.endBatch(renderType);
        }
        bufferSource.endLastBatch();

        isFlushing = false;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            INSTANCE.flush();
        }
    }
}
