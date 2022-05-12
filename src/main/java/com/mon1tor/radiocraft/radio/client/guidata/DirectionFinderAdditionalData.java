package com.mon1tor.radiocraft.radio.client.guidata;

import com.mon1tor.radiocraft.radio.history.IHistoryItem;

public class DirectionFinderAdditionalData implements IAdditionalGUIItemData {
    private IHistoryItem historyItem;

    public DirectionFinderAdditionalData(IHistoryItem historyItem) {
        this.historyItem = historyItem;
    }

    public DirectionFinderAdditionalData() {
    }

    public IHistoryItem getHistoryItem() {
        return historyItem;
    }

    public void setHistoryItem(IHistoryItem historyItem) {
        this.historyItem = historyItem;
    }
}
