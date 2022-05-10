package com.mon1tor.radiocraft.item.custom;

import com.mon1tor.radiocraft.client.gui.screen.DirectionFinderScreen;
import com.mon1tor.radiocraft.item.ModItemGroup;
import com.mon1tor.radiocraft.item.nbt.StackFrequencyNBT;
import com.mon1tor.radiocraft.item.nbt.StackIdentifierNBT;
import com.mon1tor.radiocraft.item.template.IRadioReceivable;
import com.mon1tor.radiocraft.item.template.TickDamageUniqueItemBase;
import com.mon1tor.radiocraft.radio.RadioMessageCorrupter;
import com.mon1tor.radiocraft.radio.history.DirectionFinderTextHistoryItem;
import com.mon1tor.radiocraft.radio.history.HistoryItemType;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.MessageHistoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.world.World;

public class DirectionFinderItem extends TickDamageUniqueItemBase implements IRadioReceivable {
    public DirectionFinderItem() {
        super(new Item.Properties().tab(ModItemGroup.RADIO_GROUP).durability(150).setNoRepair(), 100);
    }

    @Override
    public void onActiveStateChange(World world, Entity entity, ItemStack stack, boolean state) {
        if(world.isClientSide) {
            if(!state) {
                Minecraft mc = Minecraft.getInstance();
                if(mc.screen instanceof DirectionFinderScreen && entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    if(player.getItemInHand(((DirectionFinderScreen)mc.screen).getHeldHand()) == stack)
                        mc.setScreen(null);
                }
            }
        } else {
            if(state)
                StackIdentifierNBT.checkStackClientDataUUIDServer(stack);
        }
    }

    @Override
    public void onEnableUse(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(world.isClientSide) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new DirectionFinderScreen(player, stack, hand));
        } else
            StackIdentifierNBT.checkStackClientDataUUIDServer(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canReceive(ItemStack stack, int freq) {
        return isEnabled(stack) && StackFrequencyNBT.getFrequency(stack) == freq;
    }

    @Override
    public boolean canReceive(ItemStack stack) {
        return isEnabled(stack);
    }

    @Override
    public IHistoryItem getCorruptedTextHistoryItem(MessageHistoryItem item, BlockPos recieverPos) {
        return new DirectionFinderTextHistoryItem(
                item.sender,
                RadioMessageCorrupter.corruptMessageFromDist(item.message, item.pos, recieverPos, item.senderType, item.getTimestamp()),
                recieverPos,
                new Vector2f(0,0),
                item.getTimestamp());
    }

    @Override
    public HistoryItemType getTextHistoryItemType() {
        return HistoryItemType.DIRECTION_FINDER_TEXT;
    }
}
