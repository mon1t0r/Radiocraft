package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.radio.RadioMessageCorrupter;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.tileentity.RadioStationTile;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketSetRadioStationRecieveFrequency {
    private final int[] freq;
    private final BlockPos blockPos;

    public CPacketSetRadioStationRecieveFrequency(int[] frequency, BlockPos blockPos) {
        freq = frequency;
        this.blockPos = blockPos;
    }

    public static void encode(CPacketSetRadioStationRecieveFrequency packet, PacketBuffer buf) {
        buf.writeInt(packet.freq[0]);
        buf.writeInt(packet.freq[1]);
        buf.writeBlockPos(packet.blockPos);
    }

    public static CPacketSetRadioStationRecieveFrequency decode(PacketBuffer buf) {
        int[] arr = new int[2];
        arr[0] = buf.readInt();
        arr[1] = buf.readInt();
        BlockPos pos = buf.readBlockPos();
        return new CPacketSetRadioStationRecieveFrequency(arr, pos);
    }

    public static void handle(CPacketSetRadioStationRecieveFrequency packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerWorld world = context.get().getSender().getLevel();
            RadioStationTile tileEntity;
            if((tileEntity = (RadioStationTile) world.getBlockEntity(packet.blockPos)) != null && tileEntity.isAvaliableForWork()) {
                tileEntity.setRecFrequency(packet.freq);
                RadioMessageRegistry.syncStationHistoryToAllPlayersWatchingTileEntity(world, tileEntity.getBlockPos(), RadioMessageCorrupter.SenderType.RADIO_STATION);
                BlockState state = tileEntity.getBlockState();
                world.sendBlockUpdated(tileEntity.getBlockPos(), state, state, 0);
            }
        });
        context.get().setPacketHandled(true);
    }
}