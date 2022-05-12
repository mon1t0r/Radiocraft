package com.mon1tor.radiocraft.util;

import com.sun.javafx.geom.Vec2f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix4f;

public class RenderUtils {
    public static void renderLine(Matrix4f matrix, BufferBuilder buffer, Vec2f p1, Vec2f p2) {
        buffer.vertex(matrix, p1.x, p1.y, 0).endVertex();
        buffer.vertex(matrix, p2.x, p2.y, 0).endVertex();
    }

    public static void renderGraph(Matrix4f matrix, BufferBuilder buffer, int xPos, int yPos, int xSize, int ySize, float deltaX, float scaleX) {
        for(float x = -180.0f; x <= 180.0f; x += 0.1) {
            buffer.vertex(matrix, xPos + (x / 360.0f) * xSize + xSize / 2.0f, yPos + ((float) Math.sin(Math.toRadians(x / (scaleX + 0.1f) + deltaX)) + 0.5f) * ySize, -90).endVertex();
        }
    }
}
