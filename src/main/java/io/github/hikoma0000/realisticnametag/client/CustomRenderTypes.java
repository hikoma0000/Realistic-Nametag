package io.github.hikoma0000.realisticnametag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;


public final class CustomRenderTypes extends RenderType {

    private CustomRenderTypes(String name, VertexFormat format, int mode, int bufferSize, boolean useDelegate, boolean needsSorting, Runnable setup, Runnable clear) {
        super(name, format, mode, bufferSize, useDelegate, needsSorting, setup, clear);
        throw new IllegalStateException("This constructor should not be called directly.");
    }

    public static final RenderType REALISTIC_TEXT_BACKGROUND = create("realistic_text_background",
            DefaultVertexFormats.POSITION_COLOR_LIGHTMAP,
            GL11.GL_QUADS,
            256,
            true,
            true,
            State.builder().setTextureState(NO_TEXTURE)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(true));
}