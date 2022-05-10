package com.mon1tor.radiocraft.radio.history;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class RadioTextHistoryItem implements IHistoryItem {
    public final String sender;
    public final String message;

    public RadioTextHistoryItem(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    @Override
    public ITextComponent getDisplayText() {
        return new StringTextComponent("<" + sender + "> " + message);
    }

    @Override
    public long getTimestamp() {
        return 0;
    }

    @Override
    public HistoryItemType getType() {
        return HistoryItemType.RADIO_TEXT;
    }

    public static void write(RadioTextHistoryItem item, PacketBuffer buf) {
        buf.writeUtf(item.sender);
        buf.writeUtf(item.message);
    }

    public static RadioTextHistoryItem read(PacketBuffer buf) {
        String s = buf.readUtf();
        String m = buf.readUtf();
        return new RadioTextHistoryItem(s, m);
    }
}