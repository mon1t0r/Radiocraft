package com.mon1tor.radiocraft.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {
    public static final ItemGroup RADIO_GROUP = new ItemGroup("radioTab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.RADIO.get());
        }
    };

    public static final ItemGroup RADIO_COMPONENTS = new ItemGroup("radioComponentsTab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.CHIP_2.get());
        }
    };
}
