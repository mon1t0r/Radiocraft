package com.mon1tor.radiocraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextProperties;

public class ScreenUtils {
    public static void drawWordWrapCentered(FontRenderer font, MatrixStack pMatrixStack, ITextProperties pTextProperties, int pX, int pY, int pMax, int pColor) {
        for(IReorderingProcessor ireorderingprocessor : font.split(pTextProperties, pMax)) {
            font.drawShadow(pMatrixStack, ireorderingprocessor, (float)(pX - font.width(ireorderingprocessor) / 2), (float)pY, pColor);
            pY += 9;
        }
    }
}
