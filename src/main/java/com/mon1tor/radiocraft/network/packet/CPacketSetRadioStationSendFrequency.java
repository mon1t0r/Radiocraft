package com.mon1tor.radiocraft.network.packet;

import com.mon1tor.radiocraft.radio.Constants;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.tileentity.custom.RadioStationTile;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketSetRadioStationSendFrequency {
    private final int freq;
    private final BlockPos blockPos;

    public CPacketSetRadioStationSendFrequency(int frequency, BlockPos blockPos) {
        freq = frequency;
        this.blockPos = blockPos;
    }

    public static void encode(CPacketSetRadioStationSendFrequency packet, PacketBuffer buf) {
        buf.writeInt(packet.freq);
        buf.writeBlockPos(packet.blockPos);
    }

    public static CPacketSetRadioStationSendFrequency decode(PacketBuffer buf) {
        int f = buf.readInt();
        BlockPos pos = buf.readBlockPos();
        return new CPacketSetRadioStationSendFrequency(f, pos);
    }

    public static void handle(CPacketSetRadioStationSendFrequency packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerWorld world = context.get().getSender().getLevel();
            RadioStationTile tileEntity;
            if((tileEntity = (RadioStationTile) world.getBlockEntity(packet.blockPos)) != null && tileEntity.isAvaliableForWork()) {
                tileEntity.setSendFrequency(Constants.Frequency.clampFreq(packet.freq));
                RadioMessageRegistry.syncStationHistoryToAllPlayersWatchingTileEntity(world, tileEntity.getBlockPos());
                BlockState state = tileEntity.getBlockState();
                world.sendBlockUpdated(tileEntity.getBlockPos(), state, state, 0);
            }
        });
        context.get().setPacketHandled(true);
    }
}