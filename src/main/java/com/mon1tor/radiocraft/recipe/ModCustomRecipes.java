package com.mon1tor.radiocraft.recipe;

import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.recipe.custom.BatteryChargingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModCustomRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Radiocraft.MOD_ID);

    public static final RegistryObject<IRecipeSerializer<?>> BATTERY_CHARGING = RECIPE_SERIALIZERS.register("battery_charging",
            BatteryChargingRecipe.Serializer::new);//TODO: Crafting recipes with discharged battery

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
