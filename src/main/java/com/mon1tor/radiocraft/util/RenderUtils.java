package com.mon1tor.radiocraft.util;

import com.sun.javafx.geom.Vec2f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix4f;

public class RenderUtils {
    public static void renderLine(Matrix4f matrix, BufferBuilder buffer, Vec2f p1, Vec2f p2) {
        buffer.vertex(matrix, p1.x, p1.y, 0).endVertex();
        buffer.vertex(matrix, p2.x, p2.y, 0).endVertex();
    }

    public static void renderGraph(Matrix4f matrix, BufferBuilder buffer, float xPos, float yPos, float xSize, float ySize, float deltaX, float scaleX) {
        for(float x = -180.0f; x <= 180.0f; x += 0.1) {
            float sin = (float) Math.sin(Math.toRadians(x / (scaleX + 0.1f) + deltaX));
            buffer.vertex(matrix, xPos + (x / 360.0f) * xSize + xSize / 2.0f, yPos + (sin + 1.0f) * ySize, 0)
                    .color(Math.abs(sin) * 0.8f, 1.0f, Math.abs(sin) * 0.8f, 1.0f).endVertex();
        }
    }
}
