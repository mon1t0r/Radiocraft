package com.mon1tor.radiocraft.radio;

import com.mon1tor.radiocraft.container.RadioStationContainer;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.network.SPacketGetAvaliableReceivers;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.MessageHistoryItem;
import com.mon1tor.radiocraft.radio.history.TextHistoryItem;
import com.mon1tor.radiocraft.util.MathUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.*;

public class RadioMessageRegistry {
    private static final int MESSAGE_BUFFER_SIZE = 50; //Per frequency
    private static final Map<Integer, List<MessageHistoryItem>> messageMap = new HashMap<>(); //Frequency - Info

    public static void sendMessageOnFrequency(int freq, MessageHistoryItem msg, ServerWorld world) {
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

    public static int addMessageToFrequency(int freq, MessageHistoryItem msg) {
        List<MessageHistoryItem> list = getMessagesFromFrequency(freq);
        list.add(msg);
        while (list.size() > MESSAGE_BUFFER_SIZE)
            list.remove(0);
        messageMap.put(freq, list);
        return msg.id;
    }

    public static List<MessageHistoryItem> getMessagesFromFrequency(int freq) {
        return messageMap.getOrDefault(freq, new LinkedList<>());
    }

    public static List<MessageHistoryItem> getMessagesFromFrequencySince(int freq, long timeStart, long timeEnd) {
        List<MessageHistoryItem> list = new LinkedList<>(getMessagesFromFrequency(freq));
        list.removeIf((msg) -> {
            return msg.getTimestamp() < timeStart || msg.getTimestamp() > timeEnd;
        });
        return list;
    }

    public static List<MessageHistoryItem> getMessagesFromFreqRange(long timeStart, long timeEnd, int minFreq, int maxFreq) {
        List<MessageHistoryItem> all = new LinkedList<>();
        for(int i = minFreq; i <= maxFreq; ++i) {
            List<MessageHistoryItem> temp = getMessagesFromFrequencySince(i, timeStart, timeEnd);
            all.addAll(temp);
        }
        return all;
    }

    @Nullable
    public static MessageHistoryItem getMessageFromFreqById(int freq, int id) {
        List<MessageHistoryItem> list = getMessagesFromFrequency(freq);
        for(int i = 0; i < list.size(); ++i) {
            MessageHistoryItem item = list.get(i);
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

    public static List<TextHistoryItem> convertMessageToTextList(List<MessageHistoryItem> list) {
        List<TextHistoryItem> res = new LinkedList<>();
        for(int i = 0; i < list.size(); ++i) {
            MessageHistoryItem msg = list.get(i);
            res.add(new TextHistoryItem(msg.sender, msg.message, msg.getTimestamp()));
        }
        return res;
    }

    public static List<TextHistoryItem> convertMessageToTextListAndCorrupt(List<MessageHistoryItem> list, BlockPos recieverPos) {
        List<TextHistoryItem> res = new LinkedList<>();
        for(int i = 0; i < list.size(); ++i) {
            MessageHistoryItem msg = list.get(i);
            System.out.print(msg.getDisplayText() + " " + recieverPos + "\n");
            res.add(new TextHistoryItem(msg.sender, RadioMessageCorrupter.corruptMessageFromDist(msg.message, recieverPos, msg.pos, msg.senderType, msg.getTimestamp()), msg.getTimestamp()));
        }
        return res;
    }

    public static void printRadioHistory() {
        Integer[] keys = messageMap.keySet().toArray(new Integer[0]);
        for(int i = 0; i < keys.length; ++i) {
            int f = keys[i];
            if(!messageMap.containsKey(f)) continue;
            System.out.print("---" + f + "---\n");
            List<MessageHistoryItem> msgs = messageMap.get(f);
            for(int j = 0; j < msgs.size(); ++j) {
                MessageHistoryItem msg = msgs.get(j);
                System.out.print(msg.getDisplayText() + "\n");
            }
            System.out.print("\n");
        }
    }
}