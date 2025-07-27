package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;


public final class CustomRenderTypes {
    private CustomRenderTypes() {}

    public static final RenderType REALISTIC_TEXT_BACKGROUND = Accessor.REALISTIC_TEXT_BACKGROUND_INSTANCE;

    private static class Accessor extends RenderType {
        private Accessor(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, useDelegate, needsSorting, setupState, clearState);
            throw new IllegalStateException("This constructor should not be called.");
        }

        private static final RenderType REALISTIC_TEXT_BACKGROUND_INSTANCE = create("realistic_text_background",
                DefaultVertexFormat.POSITION_COLOR_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true,
                CompositeState.builder()
                        .setShaderState(RENDERTYPE_TEXT_BACKGROUND_SHADER)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .setDepthTestState(LEQUAL_DEPTH_TEST)
                        .createCompositeState(true));
    }
}