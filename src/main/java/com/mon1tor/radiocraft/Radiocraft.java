package com.mon1tor.radiocraft;

import com.mon1tor.radiocraft.block.ModBlocks;
import com.mon1tor.radiocraft.client.gui.screen.BatteryChargerScreen;
import com.mon1tor.radiocraft.client.gui.screen.RadioChargerScreen;
import com.mon1tor.radiocraft.client.gui.screen.RadioStationScreen;
import com.mon1tor.radiocraft.client.sound.ModSoundEvents;
import com.mon1tor.radiocraft.container.ModContainers;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.DirectionFinderItem;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.recipe.ModCustomRecipes;
import com.mon1tor.radiocraft.tileentity.ModTileEntities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Radiocraft.MOD_ID)
public class Radiocraft
{
    public static final String MOD_ID = "radiocraft";
    public static final Logger LOGGER = LogManager.getLogger();
    public static Radiocraft instance;

    public Radiocraft() {
        instance = this;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModTileEntities.register(modEventBus);
        ModContainers.register(modEventBus);
        ModSoundEvents.register(modEventBus);
        ModCustomRecipes.register(modEventBus);
        ModPacketHandler.register();

        modEventBus.addListener(this::onSetup);
        modEventBus.addListener(this::onClientSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onSetup(final FMLCommonSetupEvent event) {

    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ScreenManager.register(ModContainers.RADIO_CHARGER_CONTAINER.get(), RadioChargerScreen::new);
            ScreenManager.register(ModContainers.RADIO_STATION_CONTAINER.get(), RadioStationScreen::new);
            ScreenManager.register(ModContainers.BATTERY_CHARGER_CONTAINER.get(), BatteryChargerScreen::new);

            RenderTypeLookup.setRenderLayer(ModBlocks.RADIO_CHARGER.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.RADIO_STATION.get(), RenderType.cutout());
            RenderTypeLookup.setRenderLayer(ModBlocks.BATTERY_CHARGER.get(), RenderType.cutout());

            ItemModelsProperties.register(
                    ModItems.RADIO.get(), new ResourceLocation(MOD_ID, "enabled"),
                    (stack, world, entity) -> RadioItem.isActive(stack) ? 1.0F : 0.0F
            );
            ItemModelsProperties.register(
                    ModItems.RADIO.get(), new ResourceLocation(MOD_ID, "highlight"),
                    (stack, world, entity) -> RadioItem.needsMessageHighlight(stack) ? 1.0F : 0.0F
            );
            ItemModelsProperties.register(
                    ModItems.DIRECTION_FINDER.get(), new ResourceLocation(MOD_ID, "enabled"),
                    (stack, world, entity) -> DirectionFinderItem.isActive(stack) ? 1.0F : 0.0F
            );
        });
    }
}
