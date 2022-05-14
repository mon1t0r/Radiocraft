package com.mon1tor.radiocraft.tileentity;

import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.block.ModBlocks;
import com.mon1tor.radiocraft.tileentity.custom.BatteryChargerTile;
import com.mon1tor.radiocraft.tileentity.custom.RadioChargerTile;
import com.mon1tor.radiocraft.tileentity.custom.RadioStationTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Radiocraft.MOD_ID);

    public static RegistryObject<TileEntityType<RadioChargerTile>> RADIO_CHARGER_TILE =
            TILE_ENTITIES.register("radio_charger_tile", () -> TileEntityType.Builder.of(
                    RadioChargerTile::new, ModBlocks.RADIO_CHARGER.get()).build(null));

    public static RegistryObject<TileEntityType<RadioStationTile>> RADIO_STATION_TILE =
            TILE_ENTITIES.register("radio_station_tile", () -> TileEntityType.Builder.of(
                    RadioStationTile::new, ModBlocks.RADIO_STATION.get()).build(null));

    public static RegistryObject<TileEntityType<BatteryChargerTile>> BATTERY_CHARGER_TILE =
            TILE_ENTITIES.register("battery_charger_tile", () -> TileEntityType.Builder.of(
                    BatteryChargerTile::new, ModBlocks.BATTERY_CHARGER.get()).build(null));

    public static void register(IEventBus eventBus){
        TILE_ENTITIES.register(eventBus);
    }
}
