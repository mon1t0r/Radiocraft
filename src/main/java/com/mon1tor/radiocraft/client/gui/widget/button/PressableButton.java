package com.mon1tor.radiocraft.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PressableButton extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int xDiffTex;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;
    private boolean isPressed = false;

    public PressableButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, ResourceLocation pResourceLocation, IPressable pOnPress) {
        this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pXDiffTex, pYDiffTex, pResourceLocation, 256, 256, pOnPress);
    }

    public PressableButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, Button.IPressable pOnPress) {
        this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pXDiffTex, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress, StringTextComponent.EMPTY);
    }

    public PressableButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, Button.IPressable pOnPress, ITextComponent pMessage) {
        this(pX, pY, pWidth, pHeight, pXTexStart, pYTexStart, pXDiffTex, pYDiffTex, pResourceLocation, pTextureWidth, pTextureHeight, pOnPress, Button.NO_TOOLTIP, pMessage);
    }

    public PressableButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pXDiffTex, int pYDiffTex, ResourceLocation pResourceLocation, int pTextureWidth, int pTextureHeight, IPressable pOnPress, ITooltip pOnTooltip, ITextComponent pMessage) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pOnTooltip);
        this.textureWidth = pTextureWidth;
        this.textureHeight = pTextureHeight;
        this.xTexStart = pXTexStart;
        this.yTexStart = pYTexStart;
        this.xDiffTex = pXDiffTex;
        this.yDiffTex = pYDiffTex;
        this.resourceLocation = pResourceLocation;
    }

    public void setPosition(int pX, int pY) {
        this.x = pX;
        this.y = pY;
    }

    @Override
    public void onClick(double pMouseX, double pMouseY) {
        super.onClick(pMouseX, pMouseY);
        isPressed = true;
    }

    @Override
    public void onRelease(double pMouseX, double pMouseY) {
        super.onRelease(pMouseX, pMouseY);
        isPressed = false;
    }

    @Override
    public void renderButton(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(this.resourceLocation);

        if(isPressed && !this.isHovered()) {
            isPressed = false;
        }

        int i = this.xTexStart;
        if (isPressed) {
            i += this.xDiffTex;
        }

        int j = this.yTexStart;
        if (this.isHovered()) {
            j += this.yDiffTex;
        }

        RenderSystem.enableDepthTest();
        blit(pMatrixStack, this.x, this.y, (float)i, (float)j, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.isHovered()) {
            this.renderToolTip(pMatrixStack, pMouseX, pMouseY);
        }
    }
}
