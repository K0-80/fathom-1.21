package com.k080.fathom.client.renderer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public class FathomRenderLayers extends RenderLayer {

    private FathomRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

    //trial particle
    private static final RenderLayer TRAIL = RenderLayer.of("fathom_trail",
            VertexFormats.POSITION_COLOR,
            VertexFormat.DrawMode.TRIANGLES,
            16384,
            RenderLayer.MultiPhaseParameters.builder()
                    .program(COLOR_PROGRAM)
                    .transparency(TRANSLUCENT_TRANSPARENCY)
                    .cull(DISABLE_CULLING)
                    .lightmap(DISABLE_LIGHTMAP)
                    .writeMaskState(ALL_MASK)
                    .depthTest(LEQUAL_DEPTH_TEST)
                    .layering(POLYGON_OFFSET_LAYERING)
                    .build(false)
    );
    public static RenderLayer getTrailLayer() {
        return TRAIL;
    }

}