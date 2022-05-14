package com.mon1tor.radiocraft.network.packet;

import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.item.nbt.StackFrequencyNBT;
import com.mon1tor.radiocraft.radio.Constants;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Supplier;

public class CPacketSetDirectionFinderFrequency {
    private final int freq;
    private final int slot;

    public CPacketSetDirectionFinderFrequency(int frequency, int slot) {
        freq = frequency;
        this.slot = slot;
    }

    public static void encode(CPacketSetDirectionFinderFrequency packet, PacketBuffer buf) {
        buf.writeInt(packet.freq);
        buf.writeInt(packet.slot);
    }

    public static CPacketSetDirectionFinderFrequency decode(PacketBuffer buf) {
        int f = buf.readInt();
        int s = buf.readInt();
        return new CPacketSetDirectionFinderFrequency(f, s);
    }

    public static void handle(CPacketSetDirectionFinderFrequency packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                ItemStack dirFinder;
                if(packet.slot >= 0 && !(dirFinder = inv.getStackInSlot(packet.slot)).isEmpty() && RadioItem.isEnabled(dirFinder)
                        && StackFrequencyNBT.getFrequency(dirFinder) != packet.freq) {
                    StackFrequencyNBT.setFrequency(dirFinder, Constants.Frequency.clampFreq(packet.freq));
                }
            });

        });
        context.get().setPacketHandled(true);
    }
}
