package com.mon1tor.radiocraft.container;

import com.mon1tor.radiocraft.block.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RadioStationContainer extends Container {
    private final TileEntity tileEntity;
    private final PlayerEntity playerEntity;

    public RadioStationContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player) {
        super(ModContainers.RADIO_STATION_CONTAINER.get(), windowId);
        this.tileEntity = world.getBlockEntity(pos);
        this.playerEntity = player;
    }

    @Override
    public boolean stillValid(PlayerEntity pPlayer) {
        if(tileEntity == null)
            return false;
        return stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()), pPlayer, ModBlocks.RADIO_STATION.get());
    }
}
