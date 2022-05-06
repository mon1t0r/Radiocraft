package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.client.gui.screen.RadioStationScreen;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class SPacketSendRadioStationHistory {
    private final List<IHistoryItem> messages;

    public SPacketSendRadioStationHistory(List<IHistoryItem> history) {
        messages = history;
    }

    public static void encode(SPacketSendRadioStationHistory packet, PacketBuffer buf) {
        PacketBufferUtils.writeMessageHistory(buf, packet.messages);
    }

    public static SPacketSendRadioStationHistory decode(PacketBuffer buf) {
        List<IHistoryItem> m = PacketBufferUtils.readMessageHistory(buf);
        return new SPacketSendRadioStationHistory(m);
    }

    public static void handle(SPacketSendRadioStationHistory packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if(mc.screen instanceof RadioStationScreen) {
                    ((RadioStationScreen) mc.screen).setHistory(packet.messages);
                    /*System.out.println("RECIEVED HISTORY");
                    for(IHistoryItem m : packet.messages) {
                        System.out.println(m.getDisplayText().getString());
                    }*/
                }
            });
        });
        context.get().setPacketHandled(true);
    }
}
