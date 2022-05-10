package com.mon1tor.radiocraft.item.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StackIdentifierNBT {
    public static final String NBT_NAME = "stackUUID";
    public static void checkStackClientDataUUIDServer(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if(!nbt.contains(NBT_NAME))
            nbt.putUUID(NBT_NAME, UUID.randomUUID());
    }
    @Nullable
    public static UUID getStackClientDataUUIDClient(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        return nbt.contains(NBT_NAME) ? nbt.getUUID(NBT_NAME) : null;
    }
}
