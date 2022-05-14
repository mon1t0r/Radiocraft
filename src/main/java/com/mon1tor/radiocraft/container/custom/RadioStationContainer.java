package com.mon1tor.radiocraft.container.custom;

import com.mon1tor.radiocraft.block.ModBlocks;
import com.mon1tor.radiocraft.container.ModContainers;
import com.mon1tor.radiocraft.tileentity.custom.RadioStationTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RadioStationContainer extends Container {
    public final RadioStationTile tileEntity;
    public final PlayerEntity playerEntity;

    public RadioStationContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModContainers.RADIO_STATION_CONTAINER.get(), windowId);
        this.tileEntity = (RadioStationTile) world.getBlockEntity(pos);
        if (this.tileEntity == null) {
            throw new NullPointerException("Radio statio tile entity was NULL");
        }
        this.playerEntity = player;
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        if(tileEntity == null)
            return false;
        return stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()), pPlayer, ModBlocks.RADIO_STATION.get());
    }
}
