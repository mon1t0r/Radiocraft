package com.mon1tor.radiocraft.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class RadioStationTile extends TileEntity {
    public RadioStationTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public RadioStationTile() {
        this(ModTileEntities.RADIO_STATION_TILE.get());
    }


}
