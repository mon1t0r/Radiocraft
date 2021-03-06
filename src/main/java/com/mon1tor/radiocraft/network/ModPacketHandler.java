package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.Radiocraft;
import com.mon1tor.radiocraft.network.packet.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.List;

public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel CHANNEL;

    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(Radiocraft.MOD_ID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        int id = 0;
        CHANNEL.registerMessage(id++, CPacketSetRadioFrequency.class, CPacketSetRadioFrequency::encode, CPacketSetRadioFrequency::decode, CPacketSetRadioFrequency::handle);
        CHANNEL.registerMessage(id++, CPacketSendRadioMessage.class, CPacketSendRadioMessage::encode, CPacketSendRadioMessage::decode, CPacketSendRadioMessage::handle);
        CHANNEL.registerMessage(id++, SPacketGetAvaliableReceivers.class, SPacketGetAvaliableReceivers::encode, SPacketGetAvaliableReceivers::decode, SPacketGetAvaliableReceivers::handle);
        CHANNEL.registerMessage(id++, CPacketSendAvaliableRecievers.class, CPacketSendAvaliableRecievers::encode, CPacketSendAvaliableRecievers::decode, CPacketSendAvaliableRecievers::handle);
        CHANNEL.registerMessage(id++, SPacketDeliverMessage.class, SPacketDeliverMessage::encode, SPacketDeliverMessage::decode, SPacketDeliverMessage::handle);
        CHANNEL.registerMessage(id++, CPacketSetRadioStationRecieveFrequency.class, CPacketSetRadioStationRecieveFrequency::encode, CPacketSetRadioStationRecieveFrequency::decode, CPacketSetRadioStationRecieveFrequency::handle);
        CHANNEL.registerMessage(id++, CPacketSetRadioStationSendFrequency.class, CPacketSetRadioStationSendFrequency::encode, CPacketSetRadioStationSendFrequency::decode, CPacketSetRadioStationSendFrequency::handle);
        CHANNEL.registerMessage(id++, CPacketSendRadioStationMessage.class, CPacketSendRadioStationMessage::encode, CPacketSendRadioStationMessage::decode, CPacketSendRadioStationMessage::handle);
        CHANNEL.registerMessage(id++, CPacketSetRadioStationEnabled.class, CPacketSetRadioStationEnabled::encode, CPacketSetRadioStationEnabled::decode, CPacketSetRadioStationEnabled::handle);
        CHANNEL.registerMessage(id++, SPacketSendRadioStationHistory.class, SPacketSendRadioStationHistory::encode, SPacketSendRadioStationHistory::decode, SPacketSendRadioStationHistory::handle);
        CHANNEL.registerMessage(id++, CPacketSetDirectionFinderFrequency.class, CPacketSetDirectionFinderFrequency::encode, CPacketSetDirectionFinderFrequency::decode, CPacketSetDirectionFinderFrequency::handle);

        if (id >= 255) {
            Radiocraft.LOGGER.error("DEVELOPMENT ERROR: Number of packet types has reached the maximum of {}}!", id);
        }
    }

    public static void sendToServer(Object msg) {
        CHANNEL.sendToServer(msg);
    }

    public static void sendTo(Object msg, ServerPlayerEntity player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> {
            return player;
        }), msg);
    }

    public static void sendToAll(Object msg) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static void sendToNear(Object msg, PacketDistributor.TargetPoint target) {
        CHANNEL.send(PacketDistributor.NEAR.with(() -> {
            return target;
        }), msg);
    }

    public static void sendToAllInRange(Object msg, RegistryKey<World> dim, BlockPos point, float radius) {
        sendToNear(msg, new PacketDistributor.TargetPoint(point.getX(), point.getY(), point.getZ(), radius, dim));
    }


    public static void sendToAllExcept(Object msg, ServerPlayerEntity player) {
        List<ServerPlayerEntity> players = player.getLevel().players();

        for (ServerPlayerEntity p : players) {
            if(p.getUUID() != player.getUUID()) {
                sendTo(msg, p);
            }
        }

    }


}
