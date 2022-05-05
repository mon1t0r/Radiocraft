package com.mon1tor.radiocraft.radio.history;

import net.minecraft.util.text.ITextComponent;

public interface IHistoryItem {
    ITextComponent getDisplayText();
    long getTimestamp();
    HistoryItemType getType();
}
