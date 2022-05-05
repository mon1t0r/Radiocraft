package com.mon1tor.radiocraft.radio.history;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;


public class RadioFrequencyHistoryItem implements IHistoryItem {
    public final int newFreq;
    private final long timestamp;

    public RadioFrequencyHistoryItem(int newFreq, long timestamp) {
        this.newFreq = newFreq;
        this.timestamp = timestamp;
    }

    @Override
    public ITextComponent getDisplayText() {
        return new TranslationTextComponent("screen.radiocraft.radio.changeFrequency", newFreq);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public HistoryItemType getType(){
        return HistoryItemType.RADIO_FREQUENCY_CHANGE;
    }

    public static void write(RadioFrequencyHistoryItem item, PacketBuffer buf) {
        buf.writeInt(item.newFreq);
        buf.writeLong(item.timestamp);
    }

    public static RadioFrequencyHistoryItem read(PacketBuffer buf) {
        int f = buf.readInt();
        long time = buf.readLong();
        return new RadioFrequencyHistoryItem(f, time);
    }
}