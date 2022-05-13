package com.mon1tor.radiocraft.recipe.custom;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mon1tor.radiocraft.item.template.IEnergyStorageItem;
import com.mon1tor.radiocraft.recipe.ModCustomRecipes;
import javafx.util.Pair;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BatteryChargingRecipe extends SpecialRecipe {
    private final Item resultChargingItem;

    public BatteryChargingRecipe(ResourceLocation pResourceLocation, Item pChargingItem) {
        super(pResourceLocation);
        resultChargingItem = pChargingItem;
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
        chargingStack = stacks.getValue().copy();

        if (!batteryStack.isEmpty() && !chargingStack.isEmpty() && batteryStack.getCount() == 1 && chargingStack.getCount() == 1) {
            chargingStack.setDamageValue(chargingStack.getDamageValue() - getChargingAmount(batteryStack, chargingStack));
            return chargingStack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory pInv) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(pInv.getContainerSize(), ItemStack.EMPTY);

        int batteryIndex = -1;
        int chargingItemIndex = -1;

        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemStack = pInv.getItem(i).copy();
            if(itemStack.getItem() instanceof IEnergyStorageItem) {
                batteryIndex = i;
            } else if(itemStack.getItem() == resultChargingItem){
                chargingItemIndex = i;
            }
        }

        if(batteryIndex >= 0 && chargingItemIndex >= 0) {
            ItemStack batteryStack = pInv.getItem(batteryIndex).copy();
            ItemStack chargingStack = pInv.getItem(chargingItemIndex);

            batteryStack.setDamageValue(batteryStack.getDamageValue() + getChargingAmount(batteryStack, chargingStack));
            nonnulllist.set(batteryIndex, batteryStack);
        }

        return nonnulllist;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModCustomRecipes.BATTERY_CHARGING.get();
    }

    private int getChargingAmount(ItemStack batteryStack, ItemStack chargingStack) {
        int chargingAmount = batteryStack.getMaxDamage() - batteryStack.getDamageValue() - 1;

        int possibleRecieveAmount = chargingStack.getDamageValue();

        if(possibleRecieveAmount < chargingAmount)
            chargingAmount = possibleRecieveAmount;

        return chargingAmount;
    }

    private Pair<ItemStack, ItemStack> getMatching(CraftingInventory pInv) {
        ItemStack batteryStack = ItemStack.EMPTY;
        ItemStack chargingStack = ItemStack.EMPTY;

        boolean hasBattery = false;
        boolean hasCharging = false;

        for(int i = 0; i < pInv.getContainerSize(); ++i) {
            ItemStack itemStack = pInv.getItem(i);
            if (!itemStack.isEmpty() && itemStack.getCount() == 1) {
                if(itemStack.getItem() instanceof IEnergyStorageItem) {
                    if(hasBattery) {
                        return new Pair<>(ItemStack.EMPTY, ItemStack.EMPTY);
                    }
                    hasBattery = true;

                    if(itemStack.getDamageValue() < itemStack.getMaxDamage() - 1) {
                        batteryStack = itemStack;
                    }
                } else if(itemStack.getItem() == resultChargingItem) {
                    if(hasCharging) {
                        return new Pair<>(ItemStack.EMPTY, ItemStack.EMPTY);
                    }
                    hasCharging = true;

                    if(itemStack.getDamageValue() > 0) {
                        chargingStack = itemStack;
                    }
                }
            }
        }

        return new Pair<>(batteryStack, chargingStack);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BatteryChargingRecipe> {
        @Override
        public BatteryChargingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = JSONUtils.getAsString(pJson, "charging_item");
            Item item = Registry.ITEM.getOptional(new ResourceLocation(s)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + s + "'"));
            return new BatteryChargingRecipe(pRecipeId, item);
        }

        @Override
        public void toNetwork(PacketBuffer pBuffer, BatteryChargingRecipe pRecipe) {
            pBuffer.writeVarInt(Item.getId(pRecipe.resultChargingItem));
        }

        @Override
        public BatteryChargingRecipe fromNetwork(ResourceLocation pRecipeId, PacketBuffer pBuffer) {
            Item item = Item.byId(pBuffer.readVarInt());
            return new BatteryChargingRecipe(pRecipeId, item);
        }
    }
}