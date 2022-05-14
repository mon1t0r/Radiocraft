package com.mon1tor.radiocraft.block.properties;

import net.minecraft.util.IStringSerializable;

public enum BatteryChargerSlots implements IStringSerializable {
    NONE("none"),
    LEFT("left"),
    RIGHT("right"),
    BOTH("both");

    private final String name;

    private BatteryChargerSlots(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}