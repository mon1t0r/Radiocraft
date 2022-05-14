package com.mon1tor.radiocraft.network.packet;

import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.tileentity.custom.RadioStationTile;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketSetRadioStationEnabled {
    private final BlockPos blockPos;
    private final boolean state;

    public CPacketSetRadioStationEnabled(boolean state, BlockPos blockPos) {
        this.state = state;
        this.blockPos = blockPos;
    }

    public static void encode(CPacketSetRadioStationEnabled packet, PacketBuffer buf) {
        buf.writeBoolean(packet.state);
        buf.writeBlockPos(packet.blockPos);
    }

    public static CPacketSetRadioStationEnabled decode(PacketBuffer buf) {
        boolean b = buf.readBoolean();
        BlockPos pos = buf.readBlockPos();
        return new CPacketSetRadioStationEnabled(b, pos);
    }

    public static void handle(CPacketSetRadioStationEnabled packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerWorld world = context.get().getSender().getLevel();
            RadioStationTile tileEntity;
            if((tileEntity = (RadioStationTile) world.getBlockEntity(packet.blockPos)) != null) {
                tileEntity.setEnabled(packet.state);
                if(packet.state)
                    RadioMessageRegistry.syncStationHistoryToAllPlayersWatchingTileEntity(world, tileEntity.getBlockPos());
                BlockState state = tileEntity.getBlockState();
                world.sendBlockUpdated(tileEntity.getBlockPos(), state, state, 0);
            }
        });
        context.get().setPacketHandled(true);
    }
}
