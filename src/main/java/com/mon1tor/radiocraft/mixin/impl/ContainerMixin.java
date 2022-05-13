package com.mon1tor.radiocraft.mixin.impl;

import com.mon1tor.radiocraft.mixin.ContainerHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public abstract class ContainerMixin {
    @Inject(method = "doClick(IILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", cancellable = true, at = @At(value = "RETURN"))
    public void doClick(int pSlotId, int pDragType, ClickType pClickType, PlayerEntity pPlayer, CallbackInfoReturnable<ItemStack> ci) {
        Container screen = (Container) (Object) this;
        try {
            ContainerHandler.doClick(screen, pSlotId, pDragType, pClickType, pPlayer, ci);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
