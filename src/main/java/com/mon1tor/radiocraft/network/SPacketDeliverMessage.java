package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.radio.client.RadioGUIData;
import com.mon1tor.radiocraft.screen.RadioScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.function.Supplier;

public class SPacketDeliverMessage {
    private final int freq;
    private final String message;

    public SPacketDeliverMessage(int frequency, String message) {
        freq = frequency;
        this.message = message;
    }

    public static void encode(SPacketDeliverMessage packet, PacketBuffer buf) {
        buf.writeInt(packet.freq);
        buf.writeUtf(packet.message);
    }

    public static SPacketDeliverMessage decode(PacketBuffer buf) {
        int f = buf.readInt();
        String s = buf.readUtf();
        return new SPacketDeliverMessage(f, s);
    }

    public static void handle(SPacketDeliverMessage packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                ClientPlayerEntity player = mc.player;
                System.out.println("RECIEVED MESSAGE FROM SERVER: " + player.getDisplayName().getString());
                    player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                        for (int i = 0; i < inv.getSlots(); ++i) {
                            ItemStack stack = inv.getStackInSlot(i);
                            if(!stack.isEmpty() && stack.getItem() == ModItems.RADIO.get() && RadioItem.getFrequency(stack) == packet.freq) {
                                RadioGUIData.addMessage(RadioItem.getStackClientDataUUIDClient(stack),
                                        new RadioGUIData.HistoryItem(RadioGUIData.HistoryItemType.TEXT, packet.message));
                            }
                        }

                    });
                if(mc.screen instanceof RadioScreen) {
                    RadioScreen radioScreen = (RadioScreen) mc.screen;
                    radioScreen.updateHistory();
                }
            });
        });
        context.get().setPacketHandled(true);
    }
}
