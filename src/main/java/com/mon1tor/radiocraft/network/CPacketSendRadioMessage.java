package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.radio.RadioMessageCorrupter;
import com.mon1tor.radiocraft.radio.RadioMessagesQueue;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketSendRadioMessage {
    public static final int MAX_MESSAGE_LENGTH = 100;
    private final int freq;
    private final int slot;
    private final String message;

    public CPacketSendRadioMessage(int frequency, int slot, String msg) {
        freq = frequency;
        message = msg.length() > MAX_MESSAGE_LENGTH ? msg.substring(0, MAX_MESSAGE_LENGTH) : msg;
        this.slot = slot;
    }

    public static void encode(CPacketSendRadioMessage packet, PacketBuffer buf) {
        buf.writeInt(packet.freq);
        buf.writeInt(packet.slot);
        buf.writeUtf(packet.message);
    }

    public static CPacketSendRadioMessage decode(PacketBuffer buf) {
        int f = buf.readInt();
        int s = buf.readInt();
        String str = buf.readUtf();
        return new CPacketSendRadioMessage(f, s, str);
    }

    public static void handle(CPacketSendRadioMessage packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            ServerWorld world = player.getLevel();
            ItemStack radio;
            if(packet.slot >= 0 && !(radio = player.inventory.getItem(packet.slot)).isEmpty() && RadioItem.canDoTheJob(radio, packet.freq) &&
            !packet.message.trim().isEmpty()) {
                RadioMessagesQueue.Message msg = new RadioMessagesQueue.Message(player.getUUID(), packet.freq, player.blockPosition(), packet.message);
                //ModPacketHandler.sendToAllExcept(new SPacketGetAvaliableReceivers(packet.freq, RadioMessagesQueue.insertMessageToQueue(msg)), player);
                //ModPacketHandler.sendToAll(new SPacketGetAvaliableReceivers(packet.freq, RadioMessagesQueue.insertMessageToQueue(msg)));
                ModPacketHandler.sendToAllInRange(new SPacketGetAvaliableReceivers(packet.freq, RadioMessagesQueue.insertMessageToQueue(msg)),
                        world.dimension(), player.blockPosition(), RadioMessageCorrupter.MAX_CORRUPT_DIST);
            }
        });
        context.get().setPacketHandled(true);
    }
}
