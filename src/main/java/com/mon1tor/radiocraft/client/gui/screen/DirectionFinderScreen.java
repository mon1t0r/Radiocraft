package com.mon1tor.radiocraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.client.ModTextures;
import com.mon1tor.radiocraft.item.nbt.StackFrequencyNBT;
import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import com.mon1tor.radiocraft.network.CPacketSetDirectionFinderFrequency;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.radio.client.HistoryGUIItemData;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.RadioFrequencyChangeHistoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class DirectionFinderScreen extends Screen {
    private static final ITextComponent applyText = new TranslationTextComponent("screen.radiocraft.direction_finder.apply");

    protected int guiLeft;
    protected int guiTop;
    protected int xSize = 196;
    protected int ySize = 256;
    private final PlayerEntity player;
    private final Hand hand;
    private final UUID stackRadioDataUUID;
    private TextFieldWidget freqField;
    private IHistoryItem[] historyBuffer;
    private int currentFreq;

    public DirectionFinderScreen(PlayerEntity playerIn, ItemStack radio, Hand handIn) {
        super(NarratorChatListener.NO_TITLE);
        player = playerIn;
        hand = handIn;
        stackRadioDataUUID = StackIdentifierNBT.getStackClientDataUUIDClient(radio);
        setFrequency(StackFrequencyNBT.getFrequency(radio));
    }

    @Override
    protected void init() {
        super.init();
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(true);
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.freqField = new TextFieldWidget(this.font, this.guiLeft + 195, this.guiTop + 45, 34, 13, null);
        this.freqField.setTextColor(-1);
        this.freqField.setTextColorUneditable(-1);
        this.freqField.setBordered(false);
        this.freqField.setMaxLength(5);
        this.freqField.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.freqField.setValue(Integer.toString(currentFreq));
        this.children.add(this.freqField);

        this.addButton(new ImageButton(this.guiLeft + 232, this.guiTop + 41, 19, 16, 43, 0, 16, ModTextures.BUTTONS, 256, 256, (pOnPress) -> {
            consumeFreqInput();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, applyText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        updateHistory();
    }

    @Override
    public void tick() {
        super.tick();
        this.freqField.tick();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s = this.freqField.getValue();
        this.init(minecraft, width, height);
        this.freqField.setValue(s);
    }

    @Override
    public void removed() {
        super.removed();
        this.getMinecraft().keyboardHandler.setSendRepeatsToGui(false);
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.renderBackground(matrixStack);
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.getMinecraft().textureManager.bind(ModTextures.DIRECTION_FINDER_GUI);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0,0, this.xSize, this.ySize);
        this.blit(matrixStack, i + this.xSize, j, this.xSize,0, 60, 62);
        RenderSystem.disableBlend();

        super.render(matrixStack, x, y, partialTicks);

        this.freqField.render(matrixStack, x, y, partialTicks);

        ITextComponent freqText = new TranslationTextComponent("screen.radiocraft.direction_finder.currentFrequency", currentFreq);
        ScreenUtils.drawWordWrapCentered(this.font, matrixStack, freqText, i + 193 + 58 / 2, j + 12, 57, 0xFFFFFF);

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
           IHistoryItem item = historyBuffer[k];
            switch (item.getType()) {
                case DIRECTION_FINDER_TEXT:
                    this.font.drawWordWrap(item.getDisplayText(), i + 14, currentY, this.xSize - 28,0xFFFFFF);
                    break;
                default:
                    ScreenUtils.drawWordWrapCentered(this.font, matrixStack, item.getDisplayText(), i + this.xSize / 2, currentY, this.xSize - 20, 0x5ECDF2);
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
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                if(this.freqField.canConsumeInput()) {
                    consumeFreqInput();
                }
                break;
        }
        return false;
    }

    private void consumeFreqInput() {
        try {
            setFrequency(Integer.parseInt(this.freqField.getValue()), true);
        } catch(NumberFormatException | NullPointerException e) {
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getItemYSize(IHistoryItem item) {
        return this.font.wordWrapHeight(item.getDisplayText().getString(), this.xSize - 28) + 1;
    }

    public void updateHistory(@Nullable HistoryGUIItemData.Data data) {
        IHistoryItem[] items;
        if(data != null && (items = data.getHistory()) != null)
            historyBuffer = items;
        else if(historyBuffer == null)
            historyBuffer = new IHistoryItem[0];
    }

    public void updateHistory() {
        updateHistory(HistoryGUIItemData.getGUIDataForId(stackRadioDataUUID));
    }

    public int getDirFinderSlot() {
        return this.hand == Hand.MAIN_HAND ? this.player.inventory.selected : 40;
    }

    public Hand getHeldHand(){
        return hand;
    }

    private void setFrequency(int freq) {
        setFrequency(freq, false);
    }

    private void setFrequency(int freq, boolean notify) {
        boolean changed = freq != currentFreq;
        currentFreq = freq;
        if(notify && changed) {
            sendFrequencyUpdateToServer();
            HistoryGUIItemData.Data data = HistoryGUIItemData.addItem(stackRadioDataUUID,
                    new RadioFrequencyChangeHistoryItem(freq));
            updateHistory(data);
        }
    }

    private void sendFrequencyUpdateToServer() {
        ModPacketHandler.sendToServer(new CPacketSetDirectionFinderFrequency(currentFreq, getDirFinderSlot()));
    }
}
