package com.mon1tor.radiocraft.item.custom;

import com.mon1tor.radiocraft.item.ModItemGroup;
import com.mon1tor.radiocraft.item.template.IEnergyStorageItem;
import net.minecraft.item.Item;

public class BatteryItem extends Item implements IEnergyStorageItem {
    public static final int BATTERY_CAPACITY = 200;
    public BatteryItem() {
        super(new Item.Properties().tab(ModItemGroup.RADIO_COMPONENTS).durability(BATTERY_CAPACITY).setNoRepair());
    }
}
