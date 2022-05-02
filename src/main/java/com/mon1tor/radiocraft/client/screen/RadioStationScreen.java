package com.mon1tor.radiocraft.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.container.RadioStationContainer;
import com.mon1tor.radiocraft.network.CPacketSendRadioMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

public class RadioStationScreen extends ContainerScreen<RadioStationContainer> {
    private static final ResourceLocation GUI = new ResourceLocation(Radiocraft.MOD_ID,"textures/gui/radio_station_gui.png");
    private static final ITextComponent messageSendText = new TranslationTextComponent("screen.radiocraft.radio.send");
    private static final ITextComponent applyText = new TranslationTextComponent("screen.radiocraft.radio.apply");

    private TextFieldWidget minFreqRecField;
    private TextFieldWidget maxFreqRecField;
    private TextFieldWidget freqSendField;
    private TextFieldWidget textField;
    private final int[] recFreqRange = new int[] { 0, 0 };
    private int sendFreq = 0;

    public RadioStationScreen(RadioStationContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        drawCenteredString(matrixStack, this.font, messageSendText, this.leftPos + 97 + 43 / 2, this.topPos + 234 + (17 - 8) / 2, 0xFFFFFF);
        this.renderTooltip(matrixStack, x, y);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        getMinecraft().textureManager.bind(GUI);
        int i = this.leftPos;
        int j = this.topPos;
        this.blit(matrixStack, i, j, 0,0,this.imageWidth,this.imageHeight);
        this.minFreqRecField.render(matrixStack, x, y, partialTicks);
        this.maxFreqRecField.render(matrixStack, x, y, partialTicks);
        this.freqSendField.render(matrixStack, x, y, partialTicks);
        this.textField.render(matrixStack, x, y, partialTicks);
    }

    @Override
    protected void renderLabels(MatrixStack pMatrixStack, int pX, int pY) {

    }

    @Override
    protected void init() {
        this.imageWidth = 256;
        this.imageHeight = 256;
        super.init();
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(true);

        this.minFreqRecField = new TextFieldWidget(this.font, this.leftPos + 150, this.topPos + 44, 34, 13, null);
        this.minFreqRecField.setTextColor(-1);
        this.minFreqRecField.setTextColorUneditable(-1);
        this.minFreqRecField.setBordered(false);
        this.minFreqRecField.setMaxLength(5);
        this.minFreqRecField.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.minFreqRecField.setValue(Integer.toString(recFreqRange[0]));
        this.children.add(this.minFreqRecField);

        this.maxFreqRecField = new TextFieldWidget(this.font, this.leftPos + 189, this.topPos + 44, 34, 13, null);
        this.maxFreqRecField.setTextColor(-1);
        this.maxFreqRecField.setTextColorUneditable(-1);
        this.maxFreqRecField.setBordered(false);
        this.maxFreqRecField.setMaxLength(5);
        this.maxFreqRecField.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.maxFreqRecField.setValue(Integer.toString(recFreqRange[1]));
        this.children.add(this.maxFreqRecField);

        this.freqSendField = new TextFieldWidget(this.font, this.leftPos + 150, this.topPos + 99, 73, 13, null);
        this.freqSendField.setTextColor(-1);
        this.freqSendField.setTextColorUneditable(-1);
        this.freqSendField.setBordered(false);
        this.freqSendField.setMaxLength(5);
        this.freqSendField.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.freqSendField.setValue(Integer.toString(sendFreq));
        this.children.add(this.freqSendField);

        this.textField = new TextFieldWidget(this.font, this.leftPos + 13, this.topPos + 238, 75, 13, null);
        this.textField.setTextColor(-1);
        this.textField.setTextColorUneditable(-1);
        this.textField.setBordered(false);
        this.textField.setMaxLength(CPacketSendRadioMessage.MAX_MESSAGE_LENGTH);
        this.children.add(this.textField);
        this.setInitialFocus(this.textField);

        this.addButton(new ImageButton(this.leftPos + 97, this.topPos + 234, 43, 17, 196, 94, 17, RadioScreen.GUI, 256, 256, (pOnPress) -> {
            //consumeTextInput();
        }));

        this.addButton(new ImageButton(this.leftPos + 226, this.topPos + 41, 19, 16, 220, 62, 16, RadioScreen.GUI, 256, 256, (pOnPress) -> {
            //consumeFreqInput();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, applyText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        this.addButton(new ImageButton(this.leftPos + 226, this.topPos + 96, 19, 16, 220, 62, 16, RadioScreen.GUI, 256, 256, (pOnPress) -> {
            //consumeFreqInput();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, applyText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));
    }

    @Override
    public void tick() {
        super.tick();
        this.minFreqRecField.tick();
        this.maxFreqRecField.tick();
        this.freqSendField.tick();
        this.textField.tick();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s1 = this.minFreqRecField.getValue();
        String s2 = this.maxFreqRecField.getValue();
        String s3 = this.freqSendField.getValue();
        String s4 = this.textField.getValue();
        this.init(minecraft, width, height);
        this.minFreqRecField.setValue(s1);
        this.maxFreqRecField.setValue(s2);
        this.freqSendField.setValue(s3);
        this.textField.setValue(s4);
    }

    @Override
    public void removed() {
        super.removed();
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(this.textField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if(this.minFreqRecField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if(this.maxFreqRecField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if(this.freqSendField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                break;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.textField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(true);
            this.minFreqRecField.setFocus(false);
            this.maxFreqRecField.setFocus(false);
            this.freqSendField.setFocus(false);
            this.setFocused(this.textField);
            return true;
        } else if(this.minFreqRecField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(false);
            this.minFreqRecField.setFocus(true);
            this.maxFreqRecField.setFocus(false);
            this.freqSendField.setFocus(false);
            this.setFocused(this.minFreqRecField);
            return true;
        } else if(this.maxFreqRecField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(false);
            this.minFreqRecField.setFocus(false);
            this.maxFreqRecField.setFocus(true);
            this.freqSendField.setFocus(false);
            this.setFocused(this.maxFreqRecField);
            return true;
        } else if(this.freqSendField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(false);
            this.minFreqRecField.setFocus(false);
            this.maxFreqRecField.setFocus(false);
            this.freqSendField.setFocus(true);
            this.setFocused(this.freqSendField);
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}
