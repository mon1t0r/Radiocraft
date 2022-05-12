package com.mon1tor.radiocraft.item.custom;

import com.mon1tor.radiocraft.item.ModItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BatteryItem extends Item {
    public static final int BATTERY_CAPACITY = 200;
    public BatteryItem() {
        super(new Item.Properties().tab(ModItemGroup.RADIO_COMPONENTS).durability(BATTERY_CAPACITY).setNoRepair());
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        ItemStack container = itemStack.copy();
        if(container.hurt(1, random, null)) {
            return ItemStack.EMPTY;
        } else {
            return container;
        }
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }
}
