package com.mon1tor.radiocraft.item.custom;

import com.mon1tor.radiocraft.client.gui.screen.RadioScreen;
import com.mon1tor.radiocraft.item.ModItemGroup;
import com.mon1tor.radiocraft.item.nbt.StackFrequencyNBT;
import com.mon1tor.radiocraft.item.template.IRadioReceivable;
import com.mon1tor.radiocraft.item.template.TickDamageUniqueItemBase;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.radio.history.HistoryItemType;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.RadioTextHistoryItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RadioItem extends TickDamageUniqueItemBase implements IRadioReceivable { //TODO: Frame clone bug
    public RadioItem() {
        super(new Item.Properties().tab(ModItemGroup.RADIO_GROUP).durability(150).setNoRepair(), 100);
    }

    @Override
    public void onActiveStateChange(World world, Entity entity, ItemStack stack, boolean state) {
        if(world.isClientSide) {
            if(!state) {
                Minecraft mc = Minecraft.getInstance();
                if(mc.screen instanceof RadioScreen && entity instanceof PlayerEntity) {
                    PlayerEntity player = (PlayerEntity) entity;
                    if(player.getItemInHand(((RadioScreen)mc.screen).getHeldHand()) == stack)
                        mc.setScreen(null);
                }
            }
        }
    }

    @Override
    public void onEnableUse(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if(world.isClientSide) {
            Minecraft mc = Minecraft.getInstance();
            mc.setScreen(new RadioScreen(player, stack, hand));
        }
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
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
    public IHistoryItem getCorruptedTextHistoryItem(RadioMessageRegistry.MessageItem item, BlockPos recieverPos) {
        return new RadioTextHistoryItem(item.sender, item.message);
    }

    @Override
    public HistoryItemType getTextHistoryItemType() {
        return HistoryItemType.RADIO_TEXT;
    }
}
