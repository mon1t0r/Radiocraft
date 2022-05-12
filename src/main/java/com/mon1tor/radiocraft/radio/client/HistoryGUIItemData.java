package com.mon1tor.radiocraft.radio.client;

import com.mon1tor.radiocraft.radio.client.guidata.IAdditionalGUIItemData;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

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

        //dataMap.put(stackId, data);
        return data;
    }

    public static Data getOrCreateData(UUID stackId, Supplier<IAdditionalGUIItemData> additionalData) {
        IAdditionalGUIItemData ad = additionalData.get();
        if(!dataMap.containsKey(stackId))
            dataMap.put(stackId, new Data(ad));
        Data data = dataMap.get(stackId);
        if(ad != null && data.additionalData == null)
            data.additionalData = ad;
        return data;
    }

    public static Data getOrCreateData(UUID stackId) {
        return getOrCreateData(stackId, () -> null);
    }

    @Nullable
    public static Data getData(UUID stackId) {
        return dataMap.get(stackId);
    }

    public static void clearAllData(){
        dataMap.clear();
    }

    public static class Data {
        public final LinkedList<IHistoryItem> history = new LinkedList<>();
        public IAdditionalGUIItemData additionalData;

        public Data(IAdditionalGUIItemData additionalData) {
            this.additionalData = additionalData;
        }

        public Data() {

        }

        public IHistoryItem[] getHistory() {
            return history.toArray(new IHistoryItem[0]);
        }
    }
}
