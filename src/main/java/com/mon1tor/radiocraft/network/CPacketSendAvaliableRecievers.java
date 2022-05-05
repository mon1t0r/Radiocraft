package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.StackIdentifier;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.radio.RadioMessageCorrupter;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.radio.history.MessageHistoryItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Supplier;

public class CPacketSendAvaliableRecievers {
    private final int messageId;
    private final int slot;

    public CPacketSendAvaliableRecievers(int messageId, int slot) {
        this.messageId = messageId;
        this.slot = slot;
    }

    public static void encode(CPacketSendAvaliableRecievers packet, PacketBuffer buf) {
        buf.writeInt(packet.messageId);
        buf.writeInt(packet.slot);
    }

    public static CPacketSendAvaliableRecievers decode(PacketBuffer buf) {
        int id = buf.readInt();
        int s = buf.readInt();
        return new CPacketSendAvaliableRecievers(id, s);
    }

    public static void handle(CPacketSendAvaliableRecievers packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                    if(packet.slot >= 0 && packet.slot < inv.getSlots()) {
                        ItemStack stack = inv.getStackInSlot(packet.slot);

                        if(!stack.isEmpty() && stack.getItem() == ModItems.RADIO.get() && RadioItem.canDoTheJob(stack)) {
                            MessageHistoryItem msg;
                            int freq = RadioItem.getFrequency(stack);
                            if((msg = RadioMessageRegistry.getMessageFromFreqById(freq, packet.messageId)) != null) {
                                StackIdentifier.checkStackClientDataUUIDServer(stack);
                                String content = "<" + msg.sender +
                                        "> " +  RadioMessageCorrupter.corruptMessageFromDist(msg.message, msg.pos, player.blockPosition(), RadioMessageCorrupter.SenderType.RADIO, msg.getTimestamp());
                                ModPacketHandler.sendTo(new SPacketDeliverMessage(freq, content), player);
                            }
                        }
                    }
                });
        });
        context.get().setPacketHandled(true);
    }
}
