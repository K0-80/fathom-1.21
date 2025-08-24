package com.k080.fathom.client.rendering;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;

public class FathomRenderLayers extends RenderLayer {

    private FathomRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
        super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
    }

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