package com.mon1tor.radiocraft.radio.client;

import com.mon1tor.radiocraft.radio.client.guidata.DirectionFinderAdditionalData;
import javafx.util.Pair;

import javax.annotation.Nullable;
import java.util.UUID;

public class DirectionFinderTempBuffer {
    private static Pair<UUID, DirectionFinderAdditionalData> bufferedAdditionalData;

    @Nullable
    public static DirectionFinderAdditionalData getData(UUID stackId) {
        if(bufferedAdditionalData == null || bufferedAdditionalData.getKey() != stackId) {
            HistoryGUIItemData.Data data = HistoryGUIItemData.getData(stackId);
            if(data != null && data.additionalData instanceof DirectionFinderAdditionalData) {
                bufferedAdditionalData = new Pair<>(stackId, (DirectionFinderAdditionalData) data.additionalData);
                return bufferedAdditionalData.getValue();
            }
            return null;
        }
        return bufferedAdditionalData.getValue();
    }
}
