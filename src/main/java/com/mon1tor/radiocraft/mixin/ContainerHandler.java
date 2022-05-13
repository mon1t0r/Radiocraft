package com.mon1tor.radiocraft.mixin;

import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ContainerHandler {
    public static void doClick(Container c, int pSlotId, int pDragType, ClickType pClickType, PlayerEntity pPlayer, CallbackInfoReturnable<ItemStack> ci) {
        ItemStack carried;
        if (pClickType == ClickType.CLONE && pPlayer.abilities.instabuild && !(carried = pPlayer.inventory.getCarried()).isEmpty() && pSlotId >= 0) {
            Slot slot = c.slots.get(pSlotId);
            if (slot != null && slot.hasItem() && slot.getItem().equals(carried, false)) {
                StackIdentifierNBT.stackClientDataUUIDReassignServer(carried);
            }
        }
    }
}
