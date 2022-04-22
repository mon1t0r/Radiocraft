package com.mon1tor.radiocraft;

import com.mon1tor.radiocraft.block.ModBlocks;
import com.mon1tor.radiocraft.container.ModContainers;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.screen.RadioChargerScreen;
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
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Radiocraft.MOD_ID)
public class Radiocraft
{
    public static final String MOD_ID = "radiocraft";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ModEventHandler eventHandler = new ModEventHandler();

    public Radiocraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModTileEntities.register(modEventBus);
        ModContainers.register(modEventBus);
        ModPacketHandler.register();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ScreenManager.register(ModContainers.RADIO_CHARGER_CONTAINER.get(), RadioChargerScreen::new);
            RenderTypeLookup.setRenderLayer(ModBlocks.RADIO_CHARGER.get(), RenderType.cutout());
            ItemModelsProperties.register(
                    ModItems.RADIO.get(), new ResourceLocation(MOD_ID, "enabled"),
                    (stack, world, entity) -> RadioItem.isActive(stack) ? 1.0F : 0.0F
            );
        });
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {

    }
}
