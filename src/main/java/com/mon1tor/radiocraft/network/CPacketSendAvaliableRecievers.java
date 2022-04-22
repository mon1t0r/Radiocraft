package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.radio.RadioMessageCorrupter;
import com.mon1tor.radiocraft.radio.RadioMessagesQueue;
import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.util.PacketBufferUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Supplier;

public class CPacketSendAvaliableRecievers {
    private final int messageId;
    private final int[] slots;

    public CPacketSendAvaliableRecievers(int messageId, int[] slots) {
        this.messageId = messageId;
        this.slots = slots;
    }

    public static void encode(CPacketSendAvaliableRecievers packet, PacketBuffer buf) {
        buf.writeInt(packet.messageId);
        PacketBufferUtils.writeIntArray(buf, packet.slots);
    }

    public static CPacketSendAvaliableRecievers decode(PacketBuffer buf) {
        int id = buf.readInt();
        int[] s = PacketBufferUtils.readIntArray(buf);
        return new CPacketSendAvaliableRecievers(id, s);
    }

    public static void handle(CPacketSendAvaliableRecievers packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            ServerWorld world = player.getLevel();
            RadioMessagesQueue.Message msg = RadioMessagesQueue.getMessageById(packet.messageId);
            if(msg != null && packet.slots.length > 0) {
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                    if(packet.slots.length < inv.getSlots()) {
                        boolean canSend = false;
                        for(int i = 0; i < packet.slots.length; ++i) {
                            ItemStack stack = inv.getStackInSlot(packet.slots[i]);
                            if(!stack.isEmpty() && stack.getItem() == ModItems.RADIO.get() && RadioItem.canDoTheJob(stack, msg.freq)) {
                                canSend = true;
                                RadioItem.checkStackClientDataUUIDServer(stack);
                            }
                        }
                        if(canSend) {
                            PlayerEntity msgSender = world.getPlayerByUUID(msg.sender);
                            String content = "<" + (msgSender == null ? msg.sender.toString() : msgSender.getDisplayName().getString()) +
                                    "> " +  RadioMessageCorrupter.corruptMessageFromDist(msg.message, msg.senderPos, player.blockPosition());
                            ModPacketHandler.sendTo(new SPacketDeliverMessage(msg.freq, content), player);
                        }
                    }
                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
