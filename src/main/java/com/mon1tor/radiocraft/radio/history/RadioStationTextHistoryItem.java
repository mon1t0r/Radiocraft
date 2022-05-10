package com.mon1tor.radiocraft.radio.history;

import com.mon1tor.radiocraft.util.TimeUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class RadioStationTextHistoryItem implements IHistoryItem {
    public final String sender;
    public final String message;
    private final long timestamp;

    public RadioStationTextHistoryItem(String sender, String message, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public ITextComponent getDisplayText() {
        return new StringTextComponent("<" + sender + "-" + TimeUtils.timestampToString(timestamp) + "> " + message);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public HistoryItemType getType() {
        return HistoryItemType.RADIO_STATION_TEXT;
    }

    public static void write(RadioStationTextHistoryItem item, PacketBuffer buf) {
        buf.writeUtf(item.sender);
        buf.writeUtf(item.message);
        buf.writeLong(item.timestamp);
    }

    public static RadioStationTextHistoryItem read(PacketBuffer buf) {
        String s = buf.readUtf();
        String m = buf.readUtf();
        long time = buf.readLong();
        return new RadioStationTextHistoryItem(s, m, time);
    }
}