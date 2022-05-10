package com.mon1tor.radiocraft.item;

import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.item.custom.DirectionFinderItem;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Radiocraft.MOD_ID);

    public static final RegistryObject<Item> RADIO = ITEMS.register("radio", RadioItem::new);
    public static final RegistryObject<Item> DIRECTION_FINDER = ITEMS.register("direction_finder", DirectionFinderItem::new);
    public static final RegistryObject<Item> ANTENNA = ITEMS.register("antenna", () -> new Item(new Item.Properties().tab(ModItemGroup.RADIO_GROUP)));
    public static final RegistryObject<Item> BATTERY = ITEMS.register("battery", () -> new Item(new Item.Properties().tab(ModItemGroup.RADIO_GROUP)));
    public static final RegistryObject<Item> DISPLAY = ITEMS.register("display", () -> new Item(new Item.Properties().tab(ModItemGroup.RADIO_GROUP)));
    public static final RegistryObject<Item> CHIP_1 = ITEMS.register("chip_1", () -> new Item(new Item.Properties().tab(ModItemGroup.RADIO_GROUP)));
    public static final RegistryObject<Item> CHIP_2 = ITEMS.register("chip_2", () -> new Item(new Item.Properties().tab(ModItemGroup.RADIO_GROUP)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
