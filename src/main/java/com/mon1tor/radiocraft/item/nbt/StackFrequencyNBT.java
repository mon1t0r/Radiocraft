package com.mon1tor.radiocraft.item.nbt;

import net.minecraft.item.ItemStack;

public class StackFrequencyNBT {
    public static final String NBT_NAME = "stackFrequency";
    public static void setFrequency(ItemStack stack, int freq) {
        stack.getOrCreateTagElement(NBT_NAME).putInt(NBT_NAME, freq);
    }
    public static int getFrequency(ItemStack stack) {
        return stack.getOrCreateTagElement(NBT_NAME).getInt(NBT_NAME);
    }
}
