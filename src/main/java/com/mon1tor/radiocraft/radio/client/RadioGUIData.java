package com.mon1tor.radiocraft.radio.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class RadioGUIData {
    private static final int HISTORY_BUFFER_SIZE = 20;
    private static Map<UUID, Data> dataMap = new HashMap<>();

    @Nullable
    public static Data addMessage(UUID radioStackId, HistoryItem msg) {
        if (radioStackId == null)
            return null;
        Data data = getOrCreateData(radioStackId);

        data.history.add(msg);

        if(data.history.size() > HISTORY_BUFFER_SIZE)
            data.history.remove(0);

        dataMap.put(radioStackId, data);
        return data;
    }

    public static void setWritingMessage(UUID radioStackId, String msg) {
        if (radioStackId == null)
            return;
        Data data = getOrCreateData(radioStackId);

        data.writingMessage = msg;

        dataMap.put(radioStackId, data);
    }

    private static Data getOrCreateData(UUID radioStackId) {
        Data data = dataMap.get(radioStackId);
        if(data == null)
            data = new Data();
        return data;
    }

    @Nullable
    public static Data getGUIDataForId(UUID radioStackId) {
        if(radioStackId != null && dataMap.containsKey(radioStackId)){
            Data data = dataMap.get(radioStackId);
            return data;
        }
        return null;
    }

    public static void clearAllData(){
        dataMap.clear();
    }

    public static class Data {
        public final LinkedList<HistoryItem> history = new LinkedList<>();
        public String writingMessage = "";

        public HistoryItem[] getHistory() {
            return history.toArray(new HistoryItem[history.size()]);
        }
    }
    public static class HistoryItem {
        public final HistoryItemType type;
        public final String content;

        public HistoryItem(HistoryItemType type, String content) {
            this.type = type;
            this.content = content;
        }
    }
    public enum HistoryItemType {
        TEXT,
        CHANGE_FREQUENCY
    }
}
