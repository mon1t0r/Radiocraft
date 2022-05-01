package com.mon1tor.radiocraft.item.custom;

import com.mon1tor.radiocraft.client.screen.RadioScreen;
import com.mon1tor.radiocraft.item.ModItemGroup;
import com.mon1tor.radiocraft.item.StackIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RadioItem extends Item {
    public static final int MAX_HISTORY_LINES = 10;
    public RadioItem() {
        super(new Item.Properties().tab(ModItemGroup.RADIO_GROUP).durability(150).setNoRepair());
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(entityIn instanceof LivingEntity && isActive(stack) && entityIn.tickCount % 100 == 0) {
            if(stack.getDamageValue() >= stack.getMaxDamage() - 1) {
                setActive(stack,false);
                if(worldIn.isClientSide) {
                    System.out.println("CLIENT");
                    Minecraft mc = Minecraft.getInstance();
                    if(mc.screen instanceof RadioScreen && entityIn instanceof PlayerEntity) {
                        PlayerEntity player = (PlayerEntity) entityIn;
                        if(player.getItemInHand(((RadioScreen)mc.screen).getHeldHand()) == stack)
                            mc.setScreen(null);
                    }
                }
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
            if(playerIn.isCrouching())
                setActive(stack, false);
            else {
                if(worldIn.isClientSide) {
                    Minecraft mc = Minecraft.getInstance();
                    mc.setScreen(new RadioScreen(playerIn, stack, handIn));
                } else
                    StackIdentifier.checkStackClientDataUUIDServer(stack);
            }
        }
        else {
            if(!worldIn.isClientSide)
                StackIdentifier.checkStackClientDataUUIDServer(stack);
            setActive(stack, true);
        }
        return ActionResult.success(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    public static boolean isEnabled(ItemStack stack) {
        return isActive(stack) && stack.getDamageValue() < stack.getMaxDamage() - 1;
    }
    public static boolean canDoTheJob(ItemStack stack, int freq) {
        return isActive(stack) && getFrequency(stack) == freq && stack.getDamageValue() < stack.getMaxDamage() - 1;
    }
    public static void setActive(ItemStack stack, boolean state) {
        getCompoundNBT(stack).putBoolean("active", state);
    }
    public static boolean isActive(ItemStack stack) {
        return getCompoundNBT(stack).getBoolean("active");
    }
    public static void setFrequency(ItemStack stack, int freq) {
        getCompoundNBT(stack).putInt("frequency", freq);
    }
    public static int getFrequency(ItemStack stack) {
        return getCompoundNBT(stack).getInt("frequency");
    }
    private static CompoundNBT getCompoundNBT(ItemStack stack) {
        if (stack.getTag() == null) {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean("active", false);
            tag.putInt("frequency", 0);
            stack.setTag(tag);
        }
        return stack.getTag();
    }
}
