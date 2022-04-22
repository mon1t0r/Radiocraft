package com.mon1tor.radiocraft.radio;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.*;

public class RadioMessagesQueue {
    private static final int MESSAGE_BUFFER_SIZE = 10;
    private static int lastMessageId = 0;
    private static final Map<Integer, Message> messageMap = new HashMap<>();

    public static int insertMessageToQueue(Message msg) {
        if(messageMap.size() >= MESSAGE_BUFFER_SIZE) {
            messageMap.clear();
        }
        int id = getNextAvaliableMessageId();
        messageMap.put(id, msg);
        return id;
    }

    @Nullable
    public static Message getMessageById(int id) {
        if(messageMap.containsKey(id))
            return messageMap.get(id);
        return null;
    }

    private static int getNextAvaliableMessageId() {
        if(lastMessageId >= Integer.MAX_VALUE - 1)
            lastMessageId = 0;
        return lastMessageId++;
    }

    public static class Message {
        public final UUID sender;
        public final int freq;
        public final BlockPos senderPos;
        public final String message;

        public Message(UUID sender, int freq, BlockPos senderPos, String message) {
            this.sender = sender;
            this.freq = freq;
            this.senderPos = senderPos;
            this.message = message;
        }
    }
}
