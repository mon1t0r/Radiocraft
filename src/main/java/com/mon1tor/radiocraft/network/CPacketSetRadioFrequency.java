package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.item.nbt.StackFrequencyNBT;
import com.mon1tor.radiocraft.radio.FrequencyConstants;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Supplier;

public class CPacketSetRadioFrequency {
    private final int freq;
    private final int slot;

    public CPacketSetRadioFrequency(int frequency, int slot) {
        freq = frequency;
        this.slot = slot;
    }

    public static void encode(CPacketSetRadioFrequency packet, PacketBuffer buf) {
        buf.writeInt(packet.freq);
        buf.writeInt(packet.slot);
    }

    public static CPacketSetRadioFrequency decode(PacketBuffer buf) {
        int f = buf.readInt();
        int s = buf.readInt();
        return new CPacketSetRadioFrequency(f, s);
    }

    public static void handle(CPacketSetRadioFrequency packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                ItemStack radio;
                if(packet.slot >= 0 && !(radio = inv.getStackInSlot(packet.slot)).isEmpty() && RadioItem.isEnabled(radio)
                        && StackFrequencyNBT.getFrequency(radio) != packet.freq) {
                    StackFrequencyNBT.setFrequency(radio, FrequencyConstants.clampFreq(packet.freq));
                }
            });

        });
        context.get().setPacketHandled(true);
    }
}
