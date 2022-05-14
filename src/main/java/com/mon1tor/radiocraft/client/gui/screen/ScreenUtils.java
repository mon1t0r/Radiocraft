package com.mon1tor.radiocraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public class ScreenUtils {
    public static void drawWordWrapCentered(FontRenderer pFont, MatrixStack pMatrixStack, ITextProperties pTextProperties, int pX, int pY, int pMax, int pColor) {
        for(IReorderingProcessor ireorderingprocessor : pFont.split(pTextProperties, pMax)) {
            pFont.drawShadow(pMatrixStack, ireorderingprocessor, (float)(pX - pFont.width(ireorderingprocessor) / 2), (float)pY, pColor);
            pY += 9;
        }
    }

    public static int drawWordWrap(FontRenderer pFont, MatrixStack pMatrixStack, ITextProperties pTextProperties, int pX, int pY, int pMax, int pColor) {
        pFont.drawWordWrap(pTextProperties, pX, pY, pMax, pColor);
        return pFont.split(pTextProperties, pMax).size() * 9;
    }

    public static void drawCentered(FontRenderer pFont, MatrixStack pMatrixStack, ITextComponent pTextComponent, int pX, int pY, int pWidth, int pColor) {
        int w = pFont.width(pTextComponent);
        pFont.draw(pMatrixStack, pTextComponent, pX + (pWidth - w) / 2.0f, pY, pColor);
    }
}
