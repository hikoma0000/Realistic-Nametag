package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import io.github.hikoma0000.realisticnametag.RealisticNametag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = RealisticNametag.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DelayedNametagRenderer implements MultiBufferSource {
    public static final DelayedNametagRenderer INSTANCE = new DelayedNametagRenderer();
    public static boolean isFlushing = false;

    private final Map<RenderType, BufferBuilder> builders = new LinkedHashMap<>();
    private Optional<RenderType> lastState = Optional.empty();

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        Optional<RenderType> state = renderType.asOptional();
        BufferBuilder builder = builders.computeIfAbsent(renderType, rt -> {
            BufferBuilder b = new BufferBuilder(rt.bufferSize());
            b.begin(rt.mode(), rt.format());
            return b;
        });

        if (!state.equals(lastState) || !renderType.canConsolidateConsecutiveGeometry()) {
            lastState = state;
        }
        return builder;
    }

    public void flush() {
        if (builders.isEmpty())
            return;

        isFlushing = true;
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);

        for (Map.Entry<RenderType, BufferBuilder> entry : builders.entrySet()) {
            RenderType rt = entry.getKey();
            BufferBuilder builder = entry.getValue();
            if (builder.building()) {
                rt.end(builder, RenderSystem.getVertexSorting());
            }
        }
        builders.clear();
        lastState = Optional.empty();
        isFlushing = false;
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            INSTANCE.flush();
        }
    }
}
