package com.mon1tor.radiocraft.item;

import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.item.custom.*;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Radiocraft.MOD_ID);

    public static final RegistryObject<Item> RADIO = ITEMS.register("radio",
            () -> new RadioItem());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
