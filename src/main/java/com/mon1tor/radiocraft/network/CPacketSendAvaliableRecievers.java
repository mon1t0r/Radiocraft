package com.mon1tor.radiocraft.network;

import com.mon1tor.radiocraft.item.ModItems;
import com.mon1tor.radiocraft.item.custom.RadioItem;
import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import com.mon1tor.radiocraft.item.template.IRadioReceivableItem;
import com.mon1tor.radiocraft.radio.Constants;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.radio.history.HistoryItemType;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.util.PacketBufferUtils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public class CPacketSendAvaliableRecievers {
    private final int messageId;
    private final int freq;
    private final int slots[];

    public CPacketSendAvaliableRecievers(int messageId, int freq, int slots[]) {
        this.messageId = messageId;
        this.freq = freq;
        this.slots = slots;
    }

    public static void encode(CPacketSendAvaliableRecievers packet, PacketBuffer buf) {
        buf.writeInt(packet.messageId);
        buf.writeInt(packet.freq);
        PacketBufferUtils.writeIntArray(buf, packet.slots);
    }

    public static CPacketSendAvaliableRecievers decode(PacketBuffer buf) {
        int id = buf.readInt();
        int f = buf.readInt();
        int[] s = PacketBufferUtils.readIntArray(buf);
        return new CPacketSendAvaliableRecievers(id, f, s);
    }

    public static void handle(CPacketSendAvaliableRecievers packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();

            RadioMessageRegistry.MessageItem msg;
            if((msg = RadioMessageRegistry.getMessageFromFreqById(packet.freq, packet.messageId)) != null) {
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((inv) -> {
                    List<IHistoryItem> items = new LinkedList<>();
                    List<HistoryItemType> containedTypes = new LinkedList<>();

                    for (int i = 0; i < packet.slots.length; ++i) {
                        if (packet.slots[i] < 0 || packet.slots[i] > inv.getSlots())
                            continue;
                        ItemStack stack = inv.getStackInSlot(packet.slots[i]);

                        IRadioReceivableItem rec;
                        if (!stack.isEmpty() && stack.getItem() instanceof IRadioReceivableItem && (rec = (IRadioReceivableItem) stack.getItem()).canReceive(stack, packet.freq)) {
                            IHistoryItem item = rec.getCorruptedTextHistoryItem(msg, player.blockPosition());
                            StackIdentifierNBT.checkStackClientDataUUIDServer(stack);

                            if (item.getType().needsMessageHighlight && stack.getItem() == ModItems.RADIO.get()) {
                                RadioItem.setMessageHighlightTimestamp(stack,
                                        System.currentTimeMillis() + (long) msg.message.length() * Constants.MESSAGE_HIGHLIGHT_TIME_PER_SYMBOL);
                            }

                            if (containedTypes.contains(item.getType()))
                                continue;
                            containedTypes.add(item.getType());
                            items.add(item);
                        }
                    }

                    ModPacketHandler.sendTo(new SPacketDeliverMessage(packet.freq, items), player);

                });
            }
        });
        context.get().setPacketHandled(true);
    }
}
