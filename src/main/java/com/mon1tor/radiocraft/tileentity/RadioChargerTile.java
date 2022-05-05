package com.mon1tor.radiocraft.tileentity;

import com.mon1tor.radiocraft.block.custom.RadioChargerBlock;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.RadioItem;
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

public class RadioChargerTile extends TileEntity implements ITickableTileEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private int serverTicksCounter = 0;

    public RadioChargerTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public RadioChargerTile() {
        this(ModTileEntities.RADIO_CHARGER_TILE.get());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                level.setBlockAndUpdate(worldPosition, getBlockState().setValue(RadioChargerBlock.CHARGING, !getStackInSlot(slot).isEmpty()));
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return stack.getItem() == ModItems.RADIO.get();
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
            ++serverTicksCounter;
            if(serverTicksCounter > 100) serverTicksCounter = 0;
            if(serverTicksCounter % 20 == 0)
                handler.ifPresent((h) -> {
                    ItemStack stack = h.getStackInSlot(0);
                    if(RadioItem.isActive(stack)) RadioItem.setActive(stack, false);
                    if(!stack.isEmpty() && stack.getDamageValue() > 0)
                        stack.setDamageValue(stack.getDamageValue() - 1);
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
}
