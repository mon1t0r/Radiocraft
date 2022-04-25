package com.mon1tor.radiocraft.mixin;

import com.mon1tor.radiocraft.item.StackIdentifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class OnItemInteract {
    public static void on(Container c, int pSlotId, int pDragType, ClickType pClickType, PlayerEntity pPlayer, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack carried;
        if (pClickType == ClickType.CLONE && pPlayer.abilities.instabuild && !(carried = pPlayer.inventory.getCarried()).isEmpty() && pSlotId >= 0) {
            Slot slot = c.slots.get(pSlotId);
            if (slot != null && slot.hasItem() && slot.getItem().equals(carried, false)) {
                CompoundNBT tag = carried.getTag();
                if(tag.contains(StackIdentifier.NBT_NAME)) {
                    tag.remove(StackIdentifier.NBT_NAME);
                    StackIdentifier.checkStackClientDataUUIDServer(carried);
                }
            }
        }
    }
}
