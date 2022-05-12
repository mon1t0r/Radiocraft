package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.radio.RadioMessageCorrupter;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.tileentity.RadioStationTile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketSendRadioStationMessage {
    public static final int MAX_MESSAGE_LENGTH = 100;
    private final BlockPos blockPos;
    private final String message;

    public CPacketSendRadioStationMessage(BlockPos pos, String msg) {
        blockPos = pos;
        message = msg.length() > MAX_MESSAGE_LENGTH ? msg.substring(0, MAX_MESSAGE_LENGTH) : msg;
    }

    public static void encode(CPacketSendRadioStationMessage packet, PacketBuffer buf) {
        buf.writeBlockPos(packet.blockPos);
        buf.writeUtf(packet.message);
    }

    public static CPacketSendRadioStationMessage decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        String str = buf.readUtf();
        return new CPacketSendRadioStationMessage(pos, str);
    }

    public static void handle(CPacketSendRadioStationMessage packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            ServerWorld world = player.getLevel();
            RadioStationTile tileEntity;
            if((tileEntity = (RadioStationTile) world.getBlockEntity(packet.blockPos)) != null && tileEntity.isAvaliableForWork() &&
            !packet.message.trim().isEmpty()) {
                RadioMessageRegistry.sendMessageOnFrequency(tileEntity.getSendFrequency(),
                        new RadioMessageRegistry.MessageItem(player.getDisplayName().getString(), packet.message, tileEntity.getBlockPos(),
                                RadioMessageCorrupter.SenderType.RADIO_STATION, System.currentTimeMillis()), world);
            }
        });
        context.get().setPacketHandled(true);
    }
}
