package com.mon1tor.radiocraft.network.packet;

import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.item.nbt.StackFrequencyNBT;
import com.mon1tor.radiocraft.radio.RadioMessageCorrupter;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CPacketSendRadioMessage {
    public static final int MAX_MESSAGE_LENGTH = 100;
    private final int slot;
    private final String message;

    public CPacketSendRadioMessage(int slot, String msg) {
        message = msg.length() > MAX_MESSAGE_LENGTH ? msg.substring(0, MAX_MESSAGE_LENGTH) : msg;
        this.slot = slot;
    }

    public static void encode(CPacketSendRadioMessage packet, PacketBuffer buf) {
        buf.writeInt(packet.slot);
        buf.writeUtf(packet.message);
    }

    public static CPacketSendRadioMessage decode(PacketBuffer buf) {
        int s = buf.readInt();
        String str = buf.readUtf();
        return new CPacketSendRadioMessage(s, str);
    }

    public static void handle(CPacketSendRadioMessage packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            ServerWorld world = player.getLevel();
            ItemStack radio;
            if(packet.slot >= 0 && !(radio = player.inventory.getItem(packet.slot)).isEmpty() && RadioItem.isEnabled(radio) &&
            !packet.message.trim().isEmpty()) {
                RadioMessageRegistry.sendMessageOnFrequency(StackFrequencyNBT.getFrequency(radio),
                        new RadioMessageRegistry.MessageItem(player.getDisplayName().getString(), packet.message, player.blockPosition(),
                                RadioMessageCorrupter.SenderType.RADIO, System.currentTimeMillis()), world);

            }
        });
        context.get().setPacketHandled(true);
    }
}
