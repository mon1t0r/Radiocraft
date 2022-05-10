package com.mon1tor.radiocraft.radio.history;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RadioFrequencyChangeHistoryItem implements IHistoryItem {
    public final int newFreq;

    public RadioFrequencyChangeHistoryItem(int newFreq) {
        this.newFreq = newFreq;
    }

    @Override
    public ITextComponent getDisplayText() {
        return new TranslationTextComponent("screen.radiocraft.radio.changeFrequency", newFreq);
    }

    @Override
    public long getTimestamp() {
        return 0;
    }

    @Override
    public HistoryItemType getType() {
        return HistoryItemType.RADIO_FREQUENCY_CHANGE;
    }

    public static void write(RadioFrequencyChangeHistoryItem item, PacketBuffer buf) {
        buf.writeInt(item.newFreq);
    }

    public static RadioFrequencyChangeHistoryItem read(PacketBuffer buf) {
        int f = buf.readInt();
        return new RadioFrequencyChangeHistoryItem(f);
    }
}