package com.mon1tor.radiocraft.block.properties;

import net.minecraft.util.IStringSerializable;

public enum RadioStationPart implements IStringSerializable {
    LEFT("left"),
    RIGHT("right");

    private final String name;

    private RadioStationPart(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }

    public RadioStationPart opposite() {
        return this == LEFT ? RIGHT : LEFT;
    }
}