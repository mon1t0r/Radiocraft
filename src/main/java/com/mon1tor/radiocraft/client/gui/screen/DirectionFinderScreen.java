package com.mon1tor.radiocraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mon1tor.radiocraft.client.ModTextures;
import com.mon1tor.radiocraft.item.nbt.StackFrequencyNBT;
import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.network.packet.CPacketSetDirectionFinderFrequency;
import com.mon1tor.radiocraft.radio.client.HistoryGUIItemData;
import com.mon1tor.radiocraft.radio.client.guidata.DirectionFinderAdditionalData;
import com.mon1tor.radiocraft.radio.history.DirectionFinderTextHistoryItem;
import com.mon1tor.radiocraft.radio.history.HistoryItemType;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.RadioFrequencyChangeHistoryItem;
import com.mon1tor.radiocraft.util.TimeUtils;
import com.mon1tor.radiocraft.util.direction.DirectionRange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.InputMappings;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class DirectionFinderScreen extends Screen implements IItemScreenHistoryUpdatable {
    private static final ITextComponent applyText = new TranslationTextComponent("screen.radiocraft.direction_finder.apply");
    private static final ITextComponent selectText = new TranslationTextComponent("screen.radiocraft.direction_finder.select");
    private static final ITextComponent[] signalTypes = new ITextComponent[] {
            new TranslationTextComponent("screen.radiocraft.direction_finder.type.message"),
            new TranslationTextComponent("screen.radiocraft.direction_finder.type.beacon")
    };


    protected int guiLeft;
    protected int guiTop;
    protected int xSize = 196;
    protected int ySize = 256;
    private final PlayerEntity player;
    private final Hand hand;
    private final UUID stackDataUUID;
    private TextFieldWidget freqField;
    private TextFieldWidget messageInfoField;
    private IHistoryItem[] historyBuffer;
    private int currentFreq;
    private IHistoryItem selectedItem;

    public DirectionFinderScreen(PlayerEntity playerIn, ItemStack radio, Hand handIn) {
        super(NarratorChatListener.NO_TITLE);
        player = playerIn;
        hand = handIn;
        stackDataUUID = StackIdentifierNBT.getStackClientDataUUIDClient(radio);
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

        this.messageInfoField = new TextFieldWidget(this.font, this.guiLeft + 130, this.guiTop + 239, 34, 13, null);
        this.messageInfoField.setTextColor(-1);
        this.messageInfoField.setTextColorUneditable(-1);
        this.messageInfoField.setBordered(false);
        this.messageInfoField.setMaxLength(2);
        this.messageInfoField.setFilter((s) -> s.isEmpty() || s.matches("-?\\d+"));
        this.messageInfoField.setValue(Integer.toString(1));
        this.children.add(this.messageInfoField);

        this.addButton(new ImageButton(this.guiLeft + 232, this.guiTop + 41, 19, 16, 43, 0, 16, ModTextures.BUTTONS, 256, 256, (pOnPress) -> {
            consumeFreqInput();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, applyText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        this.addButton(new ImageButton(this.guiLeft + 167, this.guiTop + 235, 19, 16, 43, 0, 16, ModTextures.BUTTONS, 256, 256, (pOnPress) -> {
            consumeHistoryItemInput();
        }, (button, matrixStack, mouseX, mouseY) -> {
            this.renderTooltip(matrixStack, selectText, mouseX, mouseY);
        }, StringTextComponent.EMPTY));

        loadHistoryItemData();
        updateHistory();
    }

    @Override
    public void tick() {
        super.tick();
        this.freqField.tick();
        this.messageInfoField.tick();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        String s1 = this.freqField.getValue();
        String s2 = this.messageInfoField.getValue();
        this.init(minecraft, width, height);
        this.freqField.setValue(s1);
        this.messageInfoField.setValue(s2);
    }

    @Override
    public void removed() {
        saveHistoryItemData();
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
        this.messageInfoField.render(matrixStack, x, y, partialTicks);

        ITextComponent freqText = new TranslationTextComponent("screen.radiocraft.direction_finder.currentFrequency", currentFreq);
        ScreenUtils.drawWordWrapCentered(this.font, matrixStack, freqText, i + 193 + 58 / 2, j + 12, 57, 0xFFFFFF);

        int totalSizeY = 0;
        for(int k = 0; k < historyBuffer.length; ++k) {
            totalSizeY += getItemYSize(historyBuffer[k]);
        }

        int firstBufferIndex = 0;
        while (totalSizeY > this.ySize - 110 && firstBufferIndex < historyBuffer.length - 1) {
            totalSizeY -= getItemYSize(historyBuffer[firstBufferIndex++]);
        }

        int currentY = j + 12;
        int textInd = 1;
        for(int k = firstBufferIndex; k < historyBuffer.length; ++k) {
           IHistoryItem item = historyBuffer[k];
            switch (item.getType()) {
                case DIRECTION_FINDER_TEXT:
                    this.font.drawWordWrap(new StringTextComponent("[" + (textInd++) + "] ").append(item.getDisplayText()), i + 14, currentY, this.xSize - 28,0xFFFFFF);
                    break;
                default:
                    ScreenUtils.drawWordWrapCentered(this.font, matrixStack, item.getDisplayText(), i + this.xSize / 2, currentY, this.xSize - 20, 0x5ECDF2);
                    break;
            }
            currentY += getItemYSize(item);
        }

        if(selectedItem != null && selectedItem instanceof DirectionFinderTextHistoryItem) {
            DirectionFinderTextHistoryItem item = (DirectionFinderTextHistoryItem) selectedItem;
            currentY = j + 168;
            currentY += ScreenUtils.drawWordWrap(this.font, matrixStack, new TranslationTextComponent("screen.radiocraft.direction_finder.type", signalTypes[0]), i + 14, currentY, this.xSize - 28, 0xFFFFFF);
            currentY += ScreenUtils.drawWordWrap(this.font, matrixStack, new TranslationTextComponent("screen.radiocraft.direction_finder.sender", item.sender), i + 14, currentY, this.xSize - 28, 0xFFFFFF);
            currentY += ScreenUtils.drawWordWrap(this.font, matrixStack, new TranslationTextComponent("screen.radiocraft.direction_finder.receivePos", item.recievePos.getX(), item.recievePos.getY(), item.recievePos.getZ()), i + 14, currentY, this.xSize - 28, 0xFFFFFF);
            currentY += ScreenUtils.drawWordWrap(this.font, matrixStack, new TranslationTextComponent("screen.radiocraft.direction_finder.time", TimeUtils.timestampToString(item.getTimestamp())), i + 14, currentY, this.xSize - 28,0xFFFFFF);
            currentY += ScreenUtils.drawWordWrap(this.font, matrixStack, new TranslationTextComponent("screen.radiocraft.direction_finder.direction", item.recieveDirection.equals(DirectionRange.ZERO) ? "_" : item.recieveDirection.getAverageDirection().getName()), i + 14, currentY, this.xSize - 28, 0xFFFFFF);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if(this.freqField.canConsumeInput() || this.freqField.keyPressed(keyCode, scanCode, modifiers)){
            return true;
        }
        if(this.messageInfoField.canConsumeInput() || this.messageInfoField.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                if(this.freqField.canConsumeInput()) {
                    consumeFreqInput();
                } else if(this.messageInfoField.canConsumeInput()) {
                    consumeHistoryItemInput();
                }
                break;
        }
        InputMappings.Input mouseKey = InputMappings.getKey(keyCode, scanCode);
        if(this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.messageInfoField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.messageInfoField.setFocus(true);
            this.freqField.setFocus(false);
            this.setFocused(this.messageInfoField);
            return true;
        } else if(this.freqField.mouseClicked(pMouseX, pMouseY, pButton)) {
            this.messageInfoField.setFocus(false);
            this.freqField.setFocus(true);
            this.setFocused(this.freqField);
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void consumeFreqInput() {
        try {
            setFrequency(Integer.parseInt(this.freqField.getValue()), true);
        } catch(NumberFormatException | NullPointerException e) {
        }
    }

    private void consumeHistoryItemInput() {
        try {
            int index = Integer.parseInt(this.messageInfoField.getValue()) - 1;
            if(index < 0 || index >= historyBuffer.length)
                return;
            List<IHistoryItem> list = new ArrayList<>();
            for(int i = 0; i < historyBuffer.length; ++i) {
                if(historyBuffer[i] instanceof DirectionFinderTextHistoryItem)
                    list.add(historyBuffer[i]);
            }
            if(index >= list.size())
                return;
            selectedItem = list.get(index); //May need copy
            saveHistoryItemData();
        } catch(NumberFormatException | NullPointerException e) {
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getItemYSize(IHistoryItem item) {
        String s = item.getDisplayText().getString();
        if(item.getType() == HistoryItemType.DIRECTION_FINDER_TEXT)
            s = "[ ] " + s;
        return this.font.wordWrapHeight(s, this.xSize - 28) + 1;
    }

    public void updateHistory(@Nullable HistoryGUIItemData.Data data) {
        IHistoryItem[] items;
        if(data != null && (items = data.getHistory()) != null)
            historyBuffer = items;
        else if(historyBuffer == null)
            historyBuffer = new IHistoryItem[0];
    }

    public void updateHistory() {
        updateHistory(HistoryGUIItemData.getOrCreateData(stackDataUUID, DirectionFinderAdditionalData::new));
    }

    public int getDirFinderSlot() {
        return this.hand == Hand.MAIN_HAND ? this.player.inventory.selected : 40;
    }

    public Hand getHeldHand(){
        return hand;
    }

    private void saveHistoryItemData() {
        HistoryGUIItemData.Data data = HistoryGUIItemData.getOrCreateData(stackDataUUID, DirectionFinderAdditionalData::new);
        if(data.additionalData instanceof DirectionFinderAdditionalData) {
            ((DirectionFinderAdditionalData) data.additionalData).setHistoryItem(selectedItem);
        }
    }

    private void loadHistoryItemData() {
        HistoryGUIItemData.Data data = HistoryGUIItemData.getOrCreateData(stackDataUUID, DirectionFinderAdditionalData::new);
        if(data.additionalData instanceof DirectionFinderAdditionalData) {
            selectedItem = ((DirectionFinderAdditionalData) data.additionalData).getHistoryItem();
        }
    }

    private void setFrequency(int freq) {
        setFrequency(freq, false);
    }

    private void setFrequency(int freq, boolean notify) {
        boolean changed = freq != currentFreq;
        currentFreq = freq;
        if(notify && changed) {
            sendFrequencyUpdateToServer();
            HistoryGUIItemData.Data data = HistoryGUIItemData.addItem(stackDataUUID,
                    new RadioFrequencyChangeHistoryItem(freq));
            updateHistory(data);
        }
    }

    private void sendFrequencyUpdateToServer() {
        ModPacketHandler.sendToServer(new CPacketSetDirectionFinderFrequency(currentFreq, getDirFinderSlot()));
    }

    @Override
    public void onHistoryUpdate() {
        updateHistory();
    }
}
