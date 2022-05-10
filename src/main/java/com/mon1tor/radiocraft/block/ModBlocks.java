package com.mon1tor.radiocraft.block;

import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.block.custom.RadioChargerBlock;
import com.mon1tor.radiocraft.block.custom.RadioStationBlock;
import com.mon1tor.radiocraft.item.ModItemGroup;
import com.mon1tor.radiocraft.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,  Radiocraft.MOD_ID);

    public static final RegistryObject<Block> RADIO_CHARGER = registerBlock("radio_charger",
            RadioChargerBlock::new, new Item.Properties().tab(ModItemGroup.RADIO_GROUP).stacksTo(1));
    public static final RegistryObject<Block> RADIO_STATION = registerBlock("radio_station",
            RadioStationBlock::new, new Item.Properties().tab(ModItemGroup.RADIO_GROUP).stacksTo(1));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, ItemGroup itemGroup) {
        RegistryObject<T> result = BLOCKS.register(name, block);
        registerBlockItem(name, result, itemGroup);
        return result;
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Properties properties) {
        RegistryObject<T> result = BLOCKS.register(name, block);
        registerBlockItem(name, result, properties);
        return result;
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> result = BLOCKS.register(name, block);
        registerBlockItem(name, result, ModItemGroup.RADIO_GROUP);
        return result;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, ItemGroup itemGroup) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(itemGroup)));
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, Item.Properties properties) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), properties));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
