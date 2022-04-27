package com.mon1tor.radiocraft.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.item.StackIdentifier;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.network.CPacketSendRadioMessage;
import com.mon1tor.radiocraft.network.CPacketSetRadioFrequency;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.radio.client.RadioGUIData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RadioScreen extends Screen {
    private static final ResourceLocation GUI = new ResourceLocation(Radiocraft.MOD_ID,"textures/gui/radio_gui.png");

    protected int guiLeft;
    protected int guiTop;
    protected int xSize = 196;
    protected int ySize = 256;
    private final PlayerEntity player;
    private final Hand hand;
    private final UUID stackRadioDataUUID;
    private TextFieldWidget freqField;
    private TextFieldWidget textField;
    private ITextComponent freqText;
    private RadioGUIData.HistoryItem[] historyBuffer;
    private int currentFreq;

    public RadioScreen(PlayerEntity playerIn, ItemStack radio, Hand handIn) {
        super(NarratorChatListener.NO_TITLE);
        player = playerIn;
        hand = handIn;
        stackRadioDataUUID = StackIdentifier.getStackClientDataUUIDClient(radio);
        setFrequency(RadioItem.getFrequency(radio));
    }

    @Override
    protected void init() {
        super.init();
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.freqField = new TextFieldWidget(this.font, this.guiLeft + 13, this.guiTop + 238, 34, 13, null);
        this.freqField.setTextColor(-1);
        this.freqField.setTextColorUneditable(-1);
        this.freqField.setBordered(false);
        this.freqField.setMaxLength(5);
        this.freqField.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.freqField.setValue(Integer.toString(currentFreq));
        this.children.add(this.freqField);

        RadioGUIData.Data data = RadioGUIData.getGUIDataForId(stackRadioDataUUID);

        this.textField = new TextFieldWidget(this.font, this.guiLeft + 58, this.guiTop + 238, 120, 13, null);
        this.textField.setTextColor(-1);
        this.textField.setTextColorUneditable(-1);
        this.textField.setBordered(false);
        this.textField.setMaxLength(CPacketSendRadioMessage.MAX_MESSAGE_LENGTH);
        if(data != null)
            this.textField.setValue(data.writingMessage);
        this.children.add(this.textField);
        this.setInitialFocus(this.textField);

        updateHistory(data);
    }

    @Override
    public void tick() {
        super.tick();
        this.freqField.tick();
        this.textField.tick();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s1 = this.freqField.getValue();
        String s2 = this.textField.getValue();
        this.init(minecraft, width, height);
        this.freqField.setValue(s1);
        this.textField.setValue(s2);
    }

    @Override
    public void removed() {
        if(!this.textField.getValue().trim().isEmpty())
            RadioGUIData.setWritingMessage(stackRadioDataUUID, this.textField.getValue().trim());
        super.removed();
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, x, y, partialTicks);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.getMinecraft().textureManager.bind(GUI);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0,0,this.xSize + 60, this.ySize);
        RenderSystem.disableBlend();
        this.freqField.render(matrixStack, x, y, partialTicks);
        this.textField.render(matrixStack, x, y, partialTicks);
        int w = this.font.width(freqText.getString());
        this.font.draw(matrixStack, freqText, i + 185 + (59 - w) / 2.0f, j + 12, 0xFFFFFF);

        int totalSizeY = 0;
        for(int k = 0; k < historyBuffer.length; ++k) {
            totalSizeY += getItemYSize(historyBuffer[k]);
        }

        int firstBufferIndex = 0;
        while (totalSizeY > this.ySize - 40 && firstBufferIndex < historyBuffer.length - 1) {
            totalSizeY -= getItemYSize(historyBuffer[firstBufferIndex++]);
        }

        int currentY = j + 12;
        for(int k = firstBufferIndex; k < historyBuffer.length; ++k) {
            RadioGUIData.HistoryItem item = historyBuffer[k];
            switch (item.type) {
                case TEXT:
                    this.font.drawWordWrap(new StringTextComponent(item.content), i + 14, currentY, this.xSize - 28,0xFFFFFF);
                    break;
                case CHANGE_FREQUENCY:
                    ITextComponent text = new TranslationTextComponent("screen.radiocraft.radio.changeFrequency", item.content);
                    w = this.font.width(text.getString());
                    this.font.draw(matrixStack, text, i + (this.xSize - w) / 2.0f, currentY, 0x5ECDF2);
                    break;
            }
            currentY += getItemYSize(item);
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if(super.charTyped(codePoint, modifiers))
            return true;
        switch (codePoint){
            case 'e':
                this.onClose();
                return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(this.freqField.keyPressed(keyCode, scanCode, modifiers)){
            return true;
        }
        if(this.textField.keyPressed(keyCode, scanCode, modifiers)){
            return true;
        }
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                if(this.freqField.canConsumeInput()) {
                    try {
                        setFrequency(Integer.parseInt(this.freqField.getValue()), true);
                    } catch(NumberFormatException | NullPointerException e) {
                    }
                } else if(this.textField.canConsumeInput()) {
                    String s = this.textField.getValue();
                    if(!s.trim().isEmpty()) {
                        sendMessageToServer(s.trim());
                        clearTextField();
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getItemYSize(RadioGUIData.HistoryItem item) {
        switch (item.type) {
            case TEXT:
                return this.font.wordWrapHeight(item.content, this.xSize - 28) + 1;
            case CHANGE_FREQUENCY:
                return 10;
        }
        return 0;
    }

    private void clearTextField() {
        this.textField.setValue("");
        RadioGUIData.setWritingMessage(stackRadioDataUUID, "");
    }

    public void updateHistory(@Nullable RadioGUIData.Data data) {
        RadioGUIData.HistoryItem[] items;
        if(data != null && (items = data.getHistory()) != null)
            historyBuffer = items;
        else if(historyBuffer == null)
            historyBuffer = new RadioGUIData.HistoryItem[0];
    }

    public void updateHistory() {
        updateHistory(RadioGUIData.getGUIDataForId(stackRadioDataUUID));
    }

    public int getRadioSlot() {
        return this.hand == Hand.MAIN_HAND ? this.player.inventory.selected : 40;
    }

    public Hand getHeldHand(){
        return hand;
    }

    private void setFrequency(int freq) {
        setFrequency(freq, false);
    }

    private void setFrequency(int freq, boolean notify) {
        currentFreq = freq;
        freqText = new TranslationTextComponent("screen.radiocraft.radio.currentFrequency", currentFreq);
        if(notify) {
            sendFrequencyUpdateToServer();
            RadioGUIData.Data data = RadioGUIData.addMessage(stackRadioDataUUID,
                    new RadioGUIData.HistoryItem(RadioGUIData.HistoryItemType.CHANGE_FREQUENCY, Integer.toString(freq)));
            updateHistory(data);
        }
    }

    private void sendMessageToServer(String msg) {
        ModPacketHandler.sendToServer(new CPacketSendRadioMessage(currentFreq, getRadioSlot(), msg));
    }

    private void sendFrequencyUpdateToServer() {
        ModPacketHandler.sendToServer(new CPacketSetRadioFrequency(currentFreq, getRadioSlot()));
    }
}
