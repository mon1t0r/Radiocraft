package com.mon1tor.radiocraft.item.nbt;

import com.mon1tor.radiocraft.radio.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class StackFrequencyNBT {
    public static final String NBT_NAME = "stackFrequency";
    public static void setFrequency(ItemStack stack, int freq) {
        stack.getOrCreateTag().putInt(NBT_NAME, freq);
    }
    public static int getFrequency(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        if(!tag.contains(NBT_NAME)) {
            tag.putInt(NBT_NAME, Constants.Frequency.MIN_FREQUENCY);
        }
        return tag.getInt(NBT_NAME);
    }
}
