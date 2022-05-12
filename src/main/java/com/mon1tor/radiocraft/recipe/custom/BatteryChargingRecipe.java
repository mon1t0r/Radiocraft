package com.mon1tor.radiocraft.recipe.custom;

import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.template.IBatteryChargeable;
import com.mon1tor.radiocraft.recipe.ModCustomRecipes;
import javafx.util.Pair;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BatteryChargingRecipe extends SpecialRecipe {
    public BatteryChargingRecipe(ResourceLocation pResourceLocation) {
        super(pResourceLocation);
    }

    @Override
    public boolean matches(CraftingInventory pInv, World pLevel) {
        Pair<ItemStack, ItemStack> stacks = getMatching(pInv);
        return !stacks.getKey().isEmpty() && !stacks.getValue().isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInventory pInv) {
        ItemStack batteryStack;
        ItemStack chargingStack;

        Pair<ItemStack, ItemStack> stacks = getMatching(pInv);
        batteryStack = stacks.getKey();
        chargingStack = stacks.getValue();

        if (!batteryStack.isEmpty() && !chargingStack.isEmpty() && batteryStack.getCount() == 1 && chargingStack.getCount() == 1) {
            int chargingAmount = batteryStack.getMaxDamage() - batteryStack.getDamageValue() - 1;

            int possibleRecieveAmount = chargingStack.getDamageValue();

            if(possibleRecieveAmount < chargingAmount)
                chargingAmount = possibleRecieveAmount;

            batteryStack.setDamageValue(batteryStack.getDamageValue() + chargingAmount);
            chargingStack.setDamageValue(chargingStack.getDamageValue() - chargingAmount);

            return chargingStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModCustomRecipes.BATTERY_CHARGING.get();
    }

    private static Pair<ItemStack, ItemStack> getMatching(CraftingInventory pInv) {
        ItemStack batteryStack = ItemStack.EMPTY;
        ItemStack chargingStack = ItemStack.EMPTY;

        for(int i = 0; i < pInv.getContainerSize(); ++i) {
            ItemStack itemStack = pInv.getItem(i);
            if (!itemStack.isEmpty() && itemStack.getCount() == 1) {
                if(itemStack.getItem() == ModItems.BATTERY.get() && itemStack.getDamageValue() < itemStack.getMaxDamage() - 1) {
                    batteryStack = itemStack;
                } else if(itemStack.getItem() instanceof IBatteryChargeable && itemStack.getDamageValue() > 0){
                    chargingStack = itemStack;
                }
            }
        }

        return new Pair<>(batteryStack, chargingStack);
    }
}