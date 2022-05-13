package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.client.gui.screen.RadioScreen;
import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import com.mon1tor.radiocraft.item.template.IRadioReceivableItem;
import com.mon1tor.radiocraft.radio.client.HistoryGUIItemData;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.util.PacketBufferUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.List;
import java.util.function.Supplier;

public class SPacketDeliverMessage {
    private final int freq;
    private final List<IHistoryItem> items;

    public SPacketDeliverMessage(int frequency, List<IHistoryItem> items) {
        freq = frequency;
        this.items = items;
    }

    public static void encode(SPacketDeliverMessage packet, PacketBuffer buf) {
        buf.writeInt(packet.freq);
        PacketBufferUtils.writeMessageHistory(buf, packet.items);
    }

    public static SPacketDeliverMessage decode(PacketBuffer buf) {
        int f = buf.readInt();
        List<IHistoryItem> i = PacketBufferUtils.readMessageHistory(buf);
        return new SPacketDeliverMessage(f, i);
    }

    public static void handle(SPacketDeliverMessage packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                ClientPlayerEntity player = mc.player;
                    player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                        for (int i = 0; i < inv.getSlots(); ++i) {
                            ItemStack stack = inv.getStackInSlot(i);
                            IRadioReceivableItem rec;
                            if(!stack.isEmpty() && stack.getItem() instanceof IRadioReceivableItem && (rec = (IRadioReceivableItem) stack.getItem()).canReceive(stack, packet.freq)) {
                                for(int j = 0; j < packet.items.size(); ++j) {
                                    IHistoryItem item = packet.items.get(j);
                                    if(rec.getTextHistoryItemType() == item.getType()) {
                                        HistoryGUIItemData.addItem(StackIdentifierNBT.getStackClientDataUUIDClient(stack), item);
                                        break;
                                    }
                                }
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
