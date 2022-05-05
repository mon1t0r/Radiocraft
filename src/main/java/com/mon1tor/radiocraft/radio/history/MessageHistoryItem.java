package com.mon1tor.radiocraft.radio.history;

import net.minecraft.util.math.BlockPos;

public class MessageHistoryItem {
    private static int lastMessageId = 0;

    public final String sender;
    public final String message;
    public final BlockPos pos;
    public final int id;
    private final long timestamp;

    public MessageHistoryItem(String sender, String message, BlockPos pos, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.pos = pos;
        this.timestamp = timestamp;
        id = getNextAvaliableMessageId();
    }

    public String getDisplayText() {
        return pos + " - " + sender + " - " + timestamp + " - " + message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private static int getNextAvaliableMessageId() {
        if(lastMessageId >= Integer.MAX_VALUE - 1)
            lastMessageId = 0;
        return lastMessageId++;
    }
}