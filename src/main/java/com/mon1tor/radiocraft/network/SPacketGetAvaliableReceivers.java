package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SPacketGetAvaliableReceivers {
    private final int freq;
    private final int messageId;

    public SPacketGetAvaliableReceivers(int frequency, int messageId) {
        freq = frequency;
        this.messageId = messageId;
    }

    public static void encode(SPacketGetAvaliableReceivers packet, PacketBuffer buf) {
        buf.writeInt(packet.freq);
        buf.writeInt(packet.messageId);
    }

    public static SPacketGetAvaliableReceivers decode(PacketBuffer buf) {
        int f = buf.readInt();
        int id = buf.readInt();
        return new SPacketGetAvaliableReceivers(f, id);
    }

    public static void handle(SPacketGetAvaliableReceivers packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                ClientPlayerEntity player = mc.player;
                List<Integer> avaliableRecievers = new ArrayList<>();
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                    for (int i = 0; i < inv.getSlots(); i++) {
                        ItemStack stack = inv.getStackInSlot(i);
                        if(!stack.isEmpty() && stack.getItem() == ModItems.RADIO.get() && RadioItem.canDoTheJob(stack, packet.freq)) {
                            avaliableRecievers.add(i);
                        }
                    }
                });
                if(avaliableRecievers.size() > 0) {
                    ModPacketHandler.sendToServer(new CPacketSendAvaliableRecievers(packet.messageId, avaliableRecievers.stream().mapToInt(i->i).toArray()));
                }
            });
        });
        context.get().setPacketHandled(true);
    }
}
