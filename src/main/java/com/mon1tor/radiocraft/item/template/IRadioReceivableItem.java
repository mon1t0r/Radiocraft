package com.mon1tor.radiocraft.item.template;

import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.radio.history.HistoryItemType;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IRadioReceivableItem {
    boolean canReceive(ItemStack stack, int freq);
    boolean canReceive(ItemStack stack);
    IHistoryItem getCorruptedTextHistoryItem(RadioMessageRegistry.MessageItem item, BlockPos receiverPos);
    HistoryItemType getTextHistoryItemType();
}
