package com.mon1tor.radiocraft.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.container.RadioStationContainer;
import com.mon1tor.radiocraft.network.CPacketSendRadioMessage;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.List;

public class RadioStationScreen extends ContainerScreen<RadioStationContainer> {
    private static final ResourceLocation GUI = new ResourceLocation(Radiocraft.MOD_ID,"textures/gui/radio_station_gui.png");
    private static final ITextComponent messageSendText = new TranslationTextComponent("screen.radiocraft.radio_station.send");
    private static final ITextComponent applyText = new TranslationTextComponent("screen.radiocraft.radio_station.apply");
    private static final ITextComponent freqRecText = new TranslationTextComponent("screen.radiocraft.radio_station.freqRec");
    private static final ITextComponent freqSendText = new TranslationTextComponent("screen.radiocraft.radio_station.freqSend");

    private TextFieldWidget freqRecFieldMin;
    private TextFieldWidget freqRecFieldMax;
    private TextFieldWidget freqSendField;
    private TextFieldWidget textField;
    private List<IHistoryItem> historyBuffer;

    public RadioStationScreen(RadioStationContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        historyBuffer = new LinkedList<>();
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);

        int i = this.leftPos;
        int j = this.topPos;

        drawCenteredString(matrixStack, this.font, messageSendText, i + 97 + 43 / 2, j + 234 + (17 - 8) / 2, 0xFFFFFF);

        if(this.menu.tileEntity.isEnabled()) {
            this.freqRecFieldMin.render(matrixStack, x, y, partialTicks);
            this.freqRecFieldMax.render(matrixStack, x, y, partialTicks);
            this.freqSendField.render(matrixStack, x, y, partialTicks);
            this.textField.render(matrixStack, x, y, partialTicks);

            drawWordWrapCentered(matrixStack, freqRecText, i + 148 + 97 / 2, j + 6 + (16 - 8) / 2, 97, 0xFFFFFF);
            drawCenteredString(matrixStack, this.font, new TranslationTextComponent("screen.radiocraft.radio_station.freqRecContent", getFreqRec(0),getFreqRec(1)), i + 148 + 97 / 2, j + 25 + (16 - 8) / 2, 0xFFFFFF);

            drawWordWrapCentered(matrixStack, freqSendText, i + 148 + 97 / 2, j + 63 + (16 - 8) / 2, 97, 0xFFFFFF);
            drawCenteredString(matrixStack, this.font, Integer.toString(getFreqSend()), i + 148 + 97 / 2, j + 77 + (16 - 8) / 2, 0xFFFFFF);

            int totalSizeY = 0;
            for(int k = 0; k < historyBuffer.size(); ++k) {
                IHistoryItem msg = historyBuffer.get(k);
                totalSizeY += getItemYSize(msg);
            }

            int firstBufferIndex = 0;
            while (totalSizeY > this.imageHeight - 40 && firstBufferIndex < historyBuffer.size() - 1) {
                totalSizeY -= getItemYSize(historyBuffer.get(firstBufferIndex++));
            }

            int currentY = j + 12;
            for(int k = firstBufferIndex; k < historyBuffer.size(); ++k) {
                IHistoryItem item = historyBuffer.get(k);
                switch (item.getType()) {
                    case TEXT:
                        this.font.drawWordWrap(item.getDisplayText(), i + 14, currentY, this.imageWidth - 119 - 14,0xFFFFFF);
                        break;
                    case RADIO_STATION_SEND_FREQUENCY_CHANGE:
                    case RADIO_STATION_RECIEVE_FREQUENCY_CHANGE:
                        drawWordWrapCentered(matrixStack, item.getDisplayText(), i + 14 + 122 / 2, currentY, this.imageWidth - 119 - 14, 0x5ECDF2);
                        break;
                }
                currentY += getItemYSize(item);
            }

            this.renderTooltip(matrixStack, x, y);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        getMinecraft().textureManager.bind(GUI);
        this.blit(matrixStack, this.leftPos, this.topPos, 0,0,this.imageWidth,this.imageHeight);
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

        this.freqRecFieldMin = new TextFieldWidget(this.font, this.leftPos + 150, this.topPos + 44, 34, 13, null);
        this.freqRecFieldMin.setTextColor(-1);
        this.freqRecFieldMin.setTextColorUneditable(-1);
        this.freqRecFieldMin.setBordered(false);
        this.freqRecFieldMin.setMaxLength(5);
        this.freqRecFieldMin.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.freqRecFieldMin.setValue(Integer.toString(getFreqRec(0)));
        this.children.add(this.freqRecFieldMin);

        this.freqRecFieldMax = new TextFieldWidget(this.font, this.leftPos + 189, this.topPos + 44, 34, 13, null);
        this.freqRecFieldMax.setTextColor(-1);
        this.freqRecFieldMax.setTextColorUneditable(-1);
        this.freqRecFieldMax.setBordered(false);
        this.freqRecFieldMax.setMaxLength(5);
        this.freqRecFieldMax.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.freqRecFieldMax.setValue(Integer.toString(getFreqRec(1)));
        this.children.add(this.freqRecFieldMax);

        this.freqSendField = new TextFieldWidget(this.font, this.leftPos + 150, this.topPos + 99, 73, 13, null);
        this.freqSendField.setTextColor(-1);
        this.freqSendField.setTextColorUneditable(-1);
        this.freqSendField.setBordered(false);
        this.freqSendField.setMaxLength(5);
        this.freqSendField.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.freqSendField.setValue(Integer.toString(getFreqSend()));
        this.children.add(this.freqSendField);

        this.textField = new TextFieldWidget(this.font, this.leftPos + 13, this.topPos + 238, 75, 13, null);
        this.textField.setTextColor(-1);
        this.textField.setTextColorUneditable(-1);
        this.textField.setBordered(false);
        this.textField.setMaxLength(CPacketSendRadioMessage.MAX_MESSAGE_LENGTH);
        this.children.add(this.textField);
        this.setInitialFocus(this.textField);

        this.addButton(new ImageButton(this.leftPos + 97, this.topPos + 234, 43, 17, 196, 94, 17, RadioScreen.GUI, 256, 256, (pOnPress) -> {
            consumeTextInput();
        }));

        this.addButton(new ImageButton(this.leftPos + 226, this.topPos + 41, 19, 16, 220, 62, 16, RadioScreen.GUI, 256, 256, (pOnPress) -> {
            consumeRecFreqInput();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, applyText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        this.addButton(new ImageButton(this.leftPos + 226, this.topPos + 96, 19, 16, 220, 62, 16, RadioScreen.GUI, 256, 256, (pOnPress) -> {
            consumeSendFreqInput();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, applyText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        this.addButton(new ImageButton(this.leftPos + 226, this.topPos + 120, 19, 16, 220, 62, 16, RadioScreen.GUI, 256, 256, (pOnPress) -> {
            this.menu.tileEntity.setEnabled(!this.menu.tileEntity.isEnabled());
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, applyText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));
    }

    @Override
    public void tick() {
        super.tick();
        this.freqRecFieldMin.tick();
        this.freqRecFieldMax.tick();
        this.freqSendField.tick();
        this.textField.tick();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s1 = this.freqRecFieldMin.getValue();
        String s2 = this.freqRecFieldMax.getValue();
        String s3 = this.freqSendField.getValue();
        String s4 = this.textField.getValue();
        this.init(minecraft, width, height);
        this.freqRecFieldMin.setValue(s1);
        this.freqRecFieldMax.setValue(s2);
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
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                if(this.freqRecFieldMin.canConsumeInput() || this.freqRecFieldMax.canConsumeInput())
                    consumeRecFreqInput();
                else if(this.freqSendField.canConsumeInput())
                    consumeSendFreqInput();
                else if(this.textField.canConsumeInput())
                    consumeTextInput();
                break;
        }
        if(this.freqRecFieldMin.keyPressed(keyCode, scanCode, modifiers) || this.freqRecFieldMin.canConsumeInput()) {
            return true;
        }
        if(this.freqRecFieldMax.keyPressed(keyCode, scanCode, modifiers) || this.freqRecFieldMax.canConsumeInput()) {
            return true;
        }
        if(this.freqSendField.keyPressed(keyCode, scanCode, modifiers) || this.freqSendField.canConsumeInput()) {
            return true;
        }
        if(this.textField.keyPressed(keyCode, scanCode, modifiers) || this.textField.canConsumeInput()) {
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.textField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(true);
            this.freqRecFieldMin.setFocus(false);
            this.freqRecFieldMax.setFocus(false);
            this.freqSendField.setFocus(false);
            this.setFocused(this.textField);
            return true;
        } else if(this.freqRecFieldMin.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(false);
            this.freqRecFieldMin.setFocus(true);
            this.freqRecFieldMax.setFocus(false);
            this.freqSendField.setFocus(false);
            this.setFocused(this.freqRecFieldMin);
            return true;
        } else if(this.freqRecFieldMax.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(false);
            this.freqRecFieldMin.setFocus(false);
            this.freqRecFieldMax.setFocus(true);
            this.freqSendField.setFocus(false);
            this.setFocused(this.freqRecFieldMax);
            return true;
        } else if(this.freqSendField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.textField.setFocus(false);
            this.freqRecFieldMin.setFocus(false);
            this.freqRecFieldMax.setFocus(false);
            this.freqSendField.setFocus(true);
            this.setFocused(this.freqSendField);
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public void setHistory(List<IHistoryItem> history) {
        historyBuffer = history;
    }

    private void consumeTextInput() {
        String s = this.textField.getValue();
        if(!s.trim().isEmpty()) {
            this.menu.tileEntity.sendMessageToServer(s.trim());
            this.textField.setValue("");
        }
    }

    private void consumeRecFreqInput() {
        try {
            setFreqRec(Integer.parseInt(this.freqRecFieldMin.getValue()), Integer.parseInt(this.freqRecFieldMax.getValue()));
        } catch(NumberFormatException | NullPointerException e) {
        }
    }

    private void consumeSendFreqInput() {
        try {
            setFreqSend(Integer.parseInt(this.freqSendField.getValue()));
        } catch(NumberFormatException | NullPointerException e) {
        }
    }

    private void setFreqRec(int min, int max) {
        this.menu.tileEntity.setRecFrequency(min, max);
    }

    private void setFreqSend(int freq) {
        this.menu.tileEntity.setSendFrequency(freq);
    }

    private int[] getFreqRec() {
        return this.menu.tileEntity.getRecFrequency();
    }

    private int getFreqRec(int i) {
        return getFreqRec()[i];
    }

    private int getFreqSend() {
        return this.menu.tileEntity.getSendFrequency();
    }

    private void drawWordWrapCentered(MatrixStack pMatrixStack, ITextProperties pTextProperties, int pX, int pY, int pMax, int pColor) {
        for(IReorderingProcessor ireorderingprocessor : this.font.split(pTextProperties, pMax)) {
            this.font.drawShadow(pMatrixStack, ireorderingprocessor, (float)(pX - this.font.width(ireorderingprocessor) / 2), (float)pY, pColor);
            pY += 9;
        }
    }

    private int getItemYSize(IHistoryItem msg) {
        switch (msg.getType()) {
            case TEXT:
                return this.font.wordWrapHeight(msg.getDisplayText().getString(), this.imageWidth - 119 - 14) + 1;
            case RADIO_STATION_SEND_FREQUENCY_CHANGE:
            case RADIO_STATION_RECIEVE_FREQUENCY_CHANGE:
            case RADIO_FREQUENCY_CHANGE:
                return this.font.split(msg.getDisplayText(), this.imageWidth - 119 - 14).size() * 9;
        }
        return 0;
    }
}
