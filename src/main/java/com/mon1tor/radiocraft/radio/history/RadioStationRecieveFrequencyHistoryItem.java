package com.mon1tor.radiocraft.radio.history;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class RadioStationRecieveFrequencyHistoryItem implements IHistoryItem {
    public final int[] newFreq;
    private final long timestamp;

    public RadioStationRecieveFrequencyHistoryItem(int[] newFreq, long timestamp) {
        this.newFreq = newFreq;
        this.timestamp = timestamp;
    }

    public RadioStationRecieveFrequencyHistoryItem(int min, int max, long timestamp){
        this(new int[] { min, max }, timestamp);
    }

    @Override
    public ITextComponent getDisplayText() {
        return new TranslationTextComponent("screen.radiocraft.radio_station.changeRecieveFrequency", newFreq[0], newFreq[1]);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public HistoryItemType getType() {
        return HistoryItemType.RADIO_STATION_RECEIVE_FREQUENCY_CHANGE;
    }

    public static void write(RadioStationRecieveFrequencyHistoryItem item, PacketBuffer buf) {
        buf.writeInt(item.newFreq[0]);
        buf.writeInt(item.newFreq[1]);
        buf.writeLong(item.timestamp);
    }

    public static RadioStationRecieveFrequencyHistoryItem read(PacketBuffer buf) {
        int min = buf.readInt();
        int max = buf.readInt();
        long time = buf.readLong();
        return new RadioStationRecieveFrequencyHistoryItem(min, max, time);
    }
}