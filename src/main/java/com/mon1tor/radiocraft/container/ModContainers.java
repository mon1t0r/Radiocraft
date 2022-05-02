package com.mon1tor.radiocraft.container;

import com.mon1tor.radiocraft.Radiocraft;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, Radiocraft.MOD_ID);

    public static final RegistryObject<ContainerType<RadioChargerContainer>> RADIO_CHARGER_CONTAINER =
            CONTAINERS.register("radio_charger_container", () -> IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();
                return new RadioChargerContainer(windowId, world, pos, inv, inv.player);
            }));

    public static final RegistryObject<ContainerType<RadioStationContainer>> RADIO_STATION_CONTAINER =
            CONTAINERS.register("radio_station_container", () -> IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                World world = inv.player.getCommandSenderWorld();
                return new RadioStationContainer(windowId, world, pos, inv, inv.player);
            }));

    public static void register(IEventBus eventBus) {
        CONTAINERS.register(eventBus);
    }
}
