package com.mon1tor.radiocraft.tileentity.custom;

import com.mon1tor.radiocraft.block.custom.BatteryChargerBlock;
import com.mon1tor.radiocraft.block.properties.BatteryChargerSlots;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.tileentity.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BatteryChargerTile extends TileEntity implements ITickableTileEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private int ticksCount = 0;

    public BatteryChargerTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public BatteryChargerTile() {
        this(ModTileEntities.BATTERY_CHARGER_TILE.get());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BatteryChargerBlock.SLOTS_CHARGING, getChargingSlotsProperty(this)));
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == ModItems.BATTERY.get();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            ++ticksCount;
            if(ticksCount > 100) ticksCount = 0;
            if(ticksCount % 20 == 0)
                handler.ifPresent((h) -> {
                    for(int i = 0; i < h.getSlots(); ++i) {
                        ItemStack stack = h.getStackInSlot(i);
                        if(!stack.isEmpty() && stack.getDamageValue() > 0)
                            stack.setDamageValue(stack.getDamageValue() - 1);
                    }
                });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("inv"));
        super.load(blockState, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        nbt.put("inv", itemHandler.serializeNBT());
        return super.save(nbt);
    }

    public BatteryChargerSlots getChargingSlotsProperty() {
        return getChargingSlotsProperty(itemHandler);
    }

    public BatteryChargerSlots getChargingSlotsProperty(IItemHandler h) {
        boolean slot0 = !h.getStackInSlot(0).isEmpty();
        boolean slot1 = !h.getStackInSlot(1).isEmpty();
        if(slot0 && slot1)
            return BatteryChargerSlots.BOTH;
        if(slot0)
            return BatteryChargerSlots.LEFT;
        if(slot1)
            return BatteryChargerSlots.RIGHT;
        return BatteryChargerSlots.NONE;
    }
}
