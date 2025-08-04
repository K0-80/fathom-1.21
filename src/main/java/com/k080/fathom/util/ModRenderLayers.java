package com.k080.fathom.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ModRenderLayers extends RenderLayer {

    private ModRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    private static final Function<Identifier, RenderLayer> EMISSIVE = Util.memoize(texture -> {
        RenderPhase.Texture textureState = new RenderPhase.Texture(texture, false, false);

        return of("emissive",
                VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS,
                256,
                false,
                true,
                MultiPhaseParameters.builder()
                        .texture(textureState)
                        .program(EYES_PROGRAM)
                        .transparency(TRANSLUCENT_TRANSPARENCY)
                        .writeMaskState(COLOR_MASK)
                        .cull(DISABLE_CULLING)
                        .depthTest(LEQUAL_DEPTH_TEST)
                        .build(false));
    });

    public static RenderLayer getEmissive(Identifier texture) {
        return EMISSIVE.apply(texture);
    }
}