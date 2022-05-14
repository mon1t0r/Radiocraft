package com.mon1tor.radiocraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.block.properties.BatteryChargerSlots;
import com.mon1tor.radiocraft.client.ModTextures;
import com.mon1tor.radiocraft.container.custom.BatteryChargerContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BatteryChargerScreen extends ContainerScreen<BatteryChargerContainer> {
    private static final ITextComponent chargingStatusText = new TranslationTextComponent("screen.radiocraft.battery_charger.status");
    private static final ITextComponent[] chargingStatusTexts =
            new ITextComponent[] {
                    new TranslationTextComponent("screen.radiocraft.battery_charger.noDevice"),
                    new TranslationTextComponent("screen.radiocraft.battery_charger.charging"),
                    new TranslationTextComponent("screen.radiocraft.battery_charger.charged")
            };

    public BatteryChargerScreen(BatteryChargerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageHeight = 187;
        this.inventoryLabelY = this.imageHeight - 94;
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
        getMinecraft().textureManager.bind(ModTextures.BATTERY_CHARGER_GUI);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if(menu.tileEntity != null) {
            BatteryChargerSlots c = menu.tileEntity.getChargingSlotsProperty();

            int chargingStateLeft = menu.getChargingState(36);
            this.blit(matrixStack, i + 36, j + 55, 176, chargingStateLeft * 12,12,12);
            if(c == BatteryChargerSlots.LEFT || c == BatteryChargerSlots.BOTH) {
                int pr = (int)(menu.getChargingProgress(36) * 72.0f);
                this.blit(matrixStack, i + 79, j + 17 + (72 - pr), 188, 0, 6, pr);
            }

            int chargingStateRight = menu.getChargingState(37);
            this.blit(matrixStack, i + 128, j + 55, 176, chargingStateRight * 12,12,12);
            if(c == BatteryChargerSlots.RIGHT || c == BatteryChargerSlots.BOTH) {
                int pr = (int)(menu.getChargingProgress(37) * 72.0f);
                this.blit(matrixStack, i + 91, j + 17 + (72 - pr), 188, 0, 6, pr);
            }

            ScreenUtils.drawCentered(this.font, matrixStack, chargingStatusText, i + 9, j + 19, 65, 0xFFFFFF);
            ScreenUtils.drawCentered(this.font, matrixStack, chargingStatusTexts[chargingStateLeft], i + 9, j + 39, 65, chargingStateLeft == 0 ? 0xff2424 : (chargingStateLeft == 1 ? 0xfff34e : 0x69ff4e));

            ScreenUtils.drawCentered(this.font, matrixStack, chargingStatusText, i + 101, j + 19, 65, 0xFFFFFF);
            ScreenUtils.drawCentered(this.font, matrixStack, chargingStatusTexts[chargingStateRight], i + 101, j + 39, 65, chargingStateRight == 0 ? 0xff2424 : (chargingStateRight == 1 ? 0xfff34e : 0x69ff4e));
        }
    }
}
