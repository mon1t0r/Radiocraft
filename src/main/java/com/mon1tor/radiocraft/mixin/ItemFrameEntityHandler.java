package com.mon1tor.radiocraft.mixin;

import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ItemFrameEntityHandler {
    public static void setItem(ItemFrameEntity itemFrame, ItemStack itemStack, boolean updateNeighbour, CallbackInfo ci) {
        if(!itemStack.isEmpty()) {
            StackIdentifierNBT.stackClientDataUUIDReassignServer(itemStack);
        }
    }

    public static ItemStack getPickedResult(ItemFrameEntity itemFrame, RayTraceResult target) {
        ItemStack held = itemFrame.getItem();
        if (held.isEmpty()) {
            return new ItemStack(Items.ITEM_FRAME);
        }
        else {
            ItemStack copy = held.copy();
            StackIdentifierNBT.stackClientDataUUIDReassignServer(copy);
            return copy;
        }
    }
}
