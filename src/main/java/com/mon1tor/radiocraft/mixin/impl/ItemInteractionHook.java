package com.mon1tor.radiocraft.mixin.impl;

import com.mon1tor.radiocraft.mixin.OnItemInteract;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public abstract class ItemInteractionHook {
    @Inject(method = "doClick(IILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;", cancellable = true, at = @At(value = "RETURN"))
    public void method_30010(int pSlotId, int pDragType, ClickType pClickType, PlayerEntity pPlayer, CallbackInfoReturnable<ItemStack> ci) {
        Container screen = (Container) (Object) this;
        try {
            OnItemInteract.on(screen, pSlotId, pDragType, pClickType, pPlayer, ci);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
