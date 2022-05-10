package com.mon1tor.radiocraft.item.template;

import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TickDamageUniqueItemBase extends Item {
    public final int damageTickRate;

    public TickDamageUniqueItemBase(Properties pProperties, int damageTickRate) {
        super(pProperties);
        this.damageTickRate = damageTickRate;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(entityIn instanceof LivingEntity && isActive(stack) && entityIn.tickCount % damageTickRate == 0) {
            if(stack.getDamageValue() >= stack.getMaxDamage() - 1) {
                setActive(worldIn, entityIn, stack,false);
            }
            else stack.setDamageValue(stack.getDamageValue() + 1);
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(stack.getDamageValue() >= stack.getMaxDamage() - 1) return ActionResult.fail(stack);
        boolean isActive = isActive(stack);
        if(isActive) {
            if(playerIn.isCrouching()) {
                setActive(worldIn, playerIn, stack, false);
            }
            else {
                onEnableUse(worldIn, playerIn, handIn);
            }
        }
        else {
            setActive(worldIn, playerIn, stack, true);
        }
        return ActionResult.success(stack);
    }

    public void onActiveStateChange(World world, Entity entity, ItemStack stack, boolean state) {
    }

    public void onEnableUse(World world, PlayerEntity player, Hand hand) {
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    public static boolean isEnabled(ItemStack stack) {
        return isActive(stack) && stack.getDamageValue() < stack.getMaxDamage() - 1;
    }
    public static void setActive(ItemStack stack, boolean state) {
        stack.getOrCreateTagElement("active").putBoolean("active", state);
        if(state)
            StackIdentifierNBT.checkStackClientDataUUIDServer(stack);
    }
    public static void setActive(World world, Entity entity, ItemStack stack, boolean state) {
        setActive(stack, state);
        Item item = stack.getItem();
        if(item instanceof TickDamageUniqueItemBase) {
            ((TickDamageUniqueItemBase) item).onActiveStateChange(world, entity, stack, state);
        }
    }
    public static boolean isActive(ItemStack stack) {
        return stack.getOrCreateTagElement("active").getBoolean("active");
    }
}
