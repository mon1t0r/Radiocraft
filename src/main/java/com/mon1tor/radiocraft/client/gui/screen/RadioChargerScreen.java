package com.mon1tor.radiocraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.client.ModTextures;
import com.mon1tor.radiocraft.container.custom.RadioChargerContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RadioChargerScreen extends ContainerScreen<RadioChargerContainer> {
    private static final ITextComponent chargingStatusText = new TranslationTextComponent("screen.radiocraft.radio_charger.status");
    private static final ITextComponent[] chargingStatusTexts =
            new ITextComponent[] {
                    new TranslationTextComponent("screen.radiocraft.radio_charger.noDevice"),
                    new TranslationTextComponent("screen.radiocraft.radio_charger.charging"),
                    new TranslationTextComponent("screen.radiocraft.radio_charger.charged")
            };

    public RadioChargerScreen(RadioChargerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        this.renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        getMinecraft().textureManager.bind(ModTextures.RADIO_CHARGER_GUI);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0,0,this.imageWidth,this.imageHeight);
        int chargingState = menu.getChargingState();
        this.blit(matrixStack, i + 82, j + 21, 176, chargingState * 12,12,12);
        this.blit(matrixStack, i + 53, j + 62, 176, 36, (int)(menu.getChargingProgress() * 70.0f), 6);

        ScreenUtils.drawCentered(this.font, matrixStack, chargingStatusText, i + 104, j + 23, 65, 0xFFFFFF);
        ScreenUtils.drawCentered(this.font, matrixStack, chargingStatusTexts[chargingState], i + 104, j + 43, 65, chargingState == 0 ? 0xff2424 : (chargingState == 1 ? 0xfff34e : 0x69ff4e));
    }
}
