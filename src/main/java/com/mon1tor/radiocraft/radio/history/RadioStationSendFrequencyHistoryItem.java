package com.mon1tor.radiocraft.radio.history;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RadioStationSendFrequencyHistoryItem implements IHistoryItem {
    public final int newFreq;
    private final long timestamp;

    public RadioStationSendFrequencyHistoryItem(int newFreq, long timestamp) {
        this.newFreq = newFreq;
        this.timestamp = timestamp;
    }

    @Override
    public ITextComponent getDisplayText() {
        return new TranslationTextComponent("screen.radiocraft.radio_station.changeSendFrequency", newFreq);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public HistoryItemType getType() {
        return HistoryItemType.RADIO_STATION_SEND_FREQUENCY_CHANGE;
    }

    public static void write(RadioStationSendFrequencyHistoryItem item, PacketBuffer buf) {
        buf.writeInt(item.newFreq);
        buf.writeLong(item.timestamp);
    }

    public static RadioStationSendFrequencyHistoryItem read(PacketBuffer buf) {
        int f = buf.readInt();
        long time = buf.readLong();
        return new RadioStationSendFrequencyHistoryItem(f, time);
    }
}