package com.mon1tor.radiocraft.item.template;

import com.mon1tor.radiocraft.radio.history.HistoryItemType;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.MessageHistoryItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IRadioReceivable {
    boolean canReceive(ItemStack stack, int freq);
    boolean canReceive(ItemStack stack);
    IHistoryItem getCorruptedTextHistoryItem(MessageHistoryItem item, BlockPos recieverPos);
    HistoryItemType getTextHistoryItemType();
}
