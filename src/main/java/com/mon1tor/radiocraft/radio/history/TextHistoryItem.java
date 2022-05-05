package com.mon1tor.radiocraft.radio.history;

import com.mon1tor.radiocraft.util.TimeUtils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TextHistoryItem implements IHistoryItem {
    public final String sender;
    public final String message;
    private final long timestamp;

    public TextHistoryItem(String sender, String message, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.timestamp = timestamp;
    }

    @Override
    public ITextComponent getDisplayText() {
        return new StringTextComponent("<" + sender + "@" + TimeUtils.timestampToString(timestamp) + ">" + message);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public HistoryItemType getType() {
        return HistoryItemType.TEXT;
    }

    public static void write(TextHistoryItem item, PacketBuffer buf) {
        buf.writeUtf(item.sender);
        buf.writeUtf(item.message);
        buf.writeLong(item.timestamp);
    }

    public static TextHistoryItem read(PacketBuffer buf) {
        String s = buf.readUtf();
        String m = buf.readUtf();
        long time = buf.readLong();
        return new TextHistoryItem(s, m, time);
    }
}