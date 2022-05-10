package com.mon1tor.radiocraft.radio.client;

import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class HistoryGUIItemData {
    private static final int HISTORY_BUFFER_SIZE = 20;
    private static final Map<UUID, Data> dataMap = new HashMap<>();

    @Nullable
    public static Data addItem(UUID stackId, IHistoryItem item) {
        if (stackId == null)
            return null;
        Data data = getOrCreateData(stackId);

        data.history.add(item);

        if(data.history.size() > HISTORY_BUFFER_SIZE)
            data.history.remove(0);

        dataMap.put(stackId, data);
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
        return dataMap.getOrDefault(radioStackId, new Data());
    }

    @Nullable
    public static Data getGUIDataForId(UUID stackId) {
        if(stackId != null && dataMap.containsKey(stackId)){
            return dataMap.get(stackId);
        }
        return null;
    }

    public static void clearAllData(){
        dataMap.clear();
    }

    public static class Data {
        public final LinkedList<IHistoryItem> history = new LinkedList<>();
        public String writingMessage = "";

        public IHistoryItem[] getHistory() {
            return history.toArray(new IHistoryItem[0]);
        }
    }
}
