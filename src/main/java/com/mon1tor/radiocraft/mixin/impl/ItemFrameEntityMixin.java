package com.mon1tor.radiocraft.mixin.impl;

import com.mon1tor.radiocraft.mixin.ItemFrameEntityHandler;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin implements IForgeEntity {
    @Inject(method = "setItem(Lnet/minecraft/item/ItemStack;Z)V", cancellable = true, at = @At(value = "RETURN"))
    public void setItem(ItemStack pStack, boolean pUpdateNeighbour, CallbackInfo ci) {
        ItemFrameEntity entity = (ItemFrameEntity) (Object) this;
        try {
            ItemFrameEntityHandler.setItem(entity, pStack, pUpdateNeighbour, ci);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        return ItemFrameEntityHandler.getPickedResult((ItemFrameEntity) (Object) this, target);
    }
}
