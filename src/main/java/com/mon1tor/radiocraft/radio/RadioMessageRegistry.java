package com.mon1tor.radiocraft.radio;

import com.mon1tor.radiocraft.container.custom.RadioStationContainer;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.network.packet.SPacketGetAvaliableReceivers;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.RadioStationTextHistoryItem;
import com.mon1tor.radiocraft.util.MathUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class RadioMessageRegistry {
    private static final int MESSAGE_BUFFER_SIZE = 50; //Per frequency
    private static final Map<Integer, List<MessageItem>> messageMap = new HashMap<>(); //Frequency - Info

    public static void sendMessageOnFrequency(int freq, MessageItem msg, ServerWorld world) {
        int id = addMessageToFrequency(freq, msg);

        ModPacketHandler.sendToAllInRange(new SPacketGetAvaliableReceivers(freq, id),
                world.dimension(), msg.pos, msg.senderType.maxCorruptDist);
        syncStationHistoryToAllPlayersInRange(world, msg.pos, msg.senderType.maxCorruptDist);
    }

    public static void syncStationHistoryToAllPlayersInRange(ServerWorld world, BlockPos pos, float range) {
        for(ServerPlayerEntity player : world.players()) {
            if (MathUtils.getDistance(pos, player.blockPosition()) <= range && player.containerMenu instanceof RadioStationContainer) {
                RadioStationContainer c = (RadioStationContainer) player.containerMenu;
                if(c.tileEntity != null) {
                    c.tileEntity.sendHistoryUpdateToClient(player);
                }
            }
        }
    }

    public static void syncStationHistoryToAllPlayersWatchingTileEntity(ServerWorld world, BlockPos pos) {
        for(ServerPlayerEntity player : world.players()) {
            RadioStationContainer c;
            if (player.containerMenu instanceof RadioStationContainer && (c = (RadioStationContainer) player.containerMenu).tileEntity.getBlockPos() == pos) {
                c.tileEntity.sendHistoryUpdateToClient(player);
            }
        }
    }

    public static int addMessageToFrequency(int freq, MessageItem msg) {
        List<MessageItem> list = getMessagesFromFrequency(freq);
        list.add(msg);
        while (list.size() > MESSAGE_BUFFER_SIZE)
            list.remove(0);
        messageMap.put(freq, list);
        return msg.id;
    }

    public static List<MessageItem> getMessagesFromFrequency(int freq) {
        return messageMap.getOrDefault(freq, new LinkedList<>());
    }

    public static List<MessageItem> getMessagesFromFrequencySince(int freq, long timeStart, long timeEnd) {
        List<MessageItem> list = new LinkedList<>(getMessagesFromFrequency(freq));
        list.removeIf((msg) -> {
            return msg.getTimestamp() < timeStart || msg.getTimestamp() > timeEnd;
        });
        return list;
    }

    public static List<MessageItem> getMessagesFromFreqRange(long timeStart, long timeEnd, int minFreq, int maxFreq) {
        List<MessageItem> all = new LinkedList<>();
        for(int i = minFreq; i <= maxFreq; ++i) {
            List<MessageItem> temp = getMessagesFromFrequencySince(i, timeStart, timeEnd);
            all.addAll(temp);
        }
        return all;
    }

    @Nullable
    public static MessageItem getMessageFromFreqById(int freq, int id) {
        List<MessageItem> list = getMessagesFromFrequency(freq);
        for(int i = 0; i < list.size(); ++i) {
            MessageItem item = list.get(i);
            if(item.id == id)
                return item;
        }
        return null;
    }

    public static List<IHistoryItem> sortAndLimitMessagesByTimestamps(List<IHistoryItem> list) {
        Collections.sort(list, (m1, m2) -> (int) (m1.getTimestamp() - m2.getTimestamp()));
        if(list.size() <= MESSAGE_BUFFER_SIZE)
            return list;
        return list.subList(list.size() - MESSAGE_BUFFER_SIZE, list.size());
    }

    public static List<RadioStationTextHistoryItem> convertMessageToTextList(List<MessageItem> list) {
        List<RadioStationTextHistoryItem> res = new LinkedList<>();
        for(int i = 0; i < list.size(); ++i) {
            MessageItem msg = list.get(i);
            res.add(new RadioStationTextHistoryItem(msg.sender, msg.message, msg.getTimestamp()));
        }
        return res;
    }

    public static List<RadioStationTextHistoryItem> convertMessageToTextListAndCorrupt(List<MessageItem> list, BlockPos recieverPos) {
        List<RadioStationTextHistoryItem> res = new LinkedList<>();
        for(int i = 0; i < list.size(); ++i) {
            MessageItem msg = list.get(i);
            res.add(new RadioStationTextHistoryItem(msg.sender, RadioMessageCorrupter.corruptMessageFromDist(msg.message, recieverPos, msg.pos, msg.senderType, msg.getTimestamp()), msg.getTimestamp()));
        }
        return res;
    }

    public static class MessageItem {
        private static int lastMessageId = 0;

        public final String sender;
        public final String message;
        public final BlockPos pos;
        public final RadioMessageCorrupter.SenderType senderType;
        public final int id;
        private final long timestamp;

        public MessageItem(String sender, String message, BlockPos pos, RadioMessageCorrupter.SenderType senderType, long timestamp) {
            this.sender = sender;
            this.message = message;
            this.pos = new BlockPos(pos);
            this.senderType = senderType;
            this.timestamp = timestamp;
            id = getNextAvaliableMessageId();
        }

        public String getDisplayText() {
            return pos + " - " + sender + " - " + senderType + " - " + timestamp + " - " + message;
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
}
