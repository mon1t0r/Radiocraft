package com.mon1tor.radiocraft.tileentity.custom;

import com.mon1tor.radiocraft.block.custom.RadioStationBlock;
import com.mon1tor.radiocraft.network.ModPacketHandler;
import com.mon1tor.radiocraft.network.packet.*;
import com.mon1tor.radiocraft.radio.Constants;
import com.mon1tor.radiocraft.radio.RadioMessageRegistry;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import com.mon1tor.radiocraft.radio.history.RadioStationRecieveFrequencyHistoryItem;
import com.mon1tor.radiocraft.radio.history.RadioStationSendFrequencyHistoryItem;
import com.mon1tor.radiocraft.tileentity.ModTileEntities;
import javafx.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class RadioStationTile extends TileEntity {
    public static final int MAX_RECIEVE_FREQUENCY_RANGE = 50;
    private static final int MAX_FREQ_CHANGE_HISTORY = 20;

    private int[] freqRec = new int[] { Constants.Frequency.MIN_FREQUENCY,  Constants.Frequency.MIN_FREQUENCY + MAX_RECIEVE_FREQUENCY_RANGE };
    private int freqSend = Constants.Frequency.MIN_FREQUENCY;
    private boolean enabled = false;
    private LinkedList<Pair<Long, int[]>> freqRecChangeList = new LinkedList<>();
    private LinkedList<Pair<Long, Integer>> freqSendChangeList = new LinkedList<>();

    public RadioStationTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public RadioStationTile() {
        this(ModTileEntities.RADIO_STATION_TILE.get());
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        saveRadioData(pCompound);
        return super.save(pCompound);
    }

    @Override
    public void load(BlockState pBlockState, CompoundNBT pCompound) {
        loadRadioData(pCompound);
        initStationFreqHistory();
        super.load(pBlockState, pCompound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        saveRadioData(nbt);
        return nbt;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 0, saveRadioData(new CompoundNBT()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        loadRadioData(packet.getTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        loadRadioData(tag);
        super.handleUpdateTag(state, tag);
    }

    public CompoundNBT saveRadioData(CompoundNBT nbt) {
        nbt.putInt("freqRecMin", freqRec[0]);
        nbt.putInt("freqRecMax", freqRec[1]);
        nbt.putInt("freqSend", freqSend);
        nbt.putBoolean("enabled", enabled);
        return nbt;
    }

    public void loadRadioData(CompoundNBT nbt) {
        int[] rec = new int[] { nbt.getInt("freqRecMin"), nbt.getInt("freqRecMax") };
        if(rec[0] > rec[1]){
            int t = rec[1];
            rec[1] = rec[0];
            rec[0] = t;
        }
        if(rec[1] - rec[0] > MAX_RECIEVE_FREQUENCY_RANGE)
            rec[1] = rec[0] + MAX_RECIEVE_FREQUENCY_RANGE;
        freqRec = rec;
        freqSend = nbt.getInt("freqSend");
        enabled = nbt.getBoolean("enabled");
    }

    public void sendMessageToServer(String msg) {
        CPacketSendRadioStationMessage packet = new CPacketSendRadioStationMessage(worldPosition, msg);
        ModPacketHandler.sendToServer(packet);
    }

    public void setRecFrequency(int[] freq) {
        setRecFrequency(freq[0], freq[1]);
    }

    public void setRecFrequency(int min, int max) {
        if(min > max){
            int t = max;
            max = min;
            min = t;
        }

        if(max - min > MAX_RECIEVE_FREQUENCY_RANGE)
            max = min + MAX_RECIEVE_FREQUENCY_RANGE;

        if(freqRec[0] == min && freqRec[1] == max)
            return;
        freqRec[0] = min;
        freqRec[1] = max;

        if(level.isClientSide) {
            CPacketSetRadioStationRecieveFrequency packet = new CPacketSetRadioStationRecieveFrequency(freqRec, this.worldPosition);
            ModPacketHandler.sendToServer(packet);
        } else {
            long time = System.currentTimeMillis();
            freqRecChangeList.add(new Pair<>(time, getRecFrequency()));
            while (freqRecChangeList.size() > MAX_FREQ_CHANGE_HISTORY)
                freqRecChangeList.removeFirst();
        }
    }

    public void setSendFrequency(int freq) {
        if(freqSend == freq)
            return;
        freqSend = freq;
        if(level.isClientSide) {
            CPacketSetRadioStationSendFrequency packet = new CPacketSetRadioStationSendFrequency(freqSend, this.worldPosition);
            ModPacketHandler.sendToServer(packet);
        } else {
            long time = System.currentTimeMillis();
            freqSendChangeList.add(new Pair<>(time, getSendFrequency()));
            while (freqSendChangeList.size() > MAX_FREQ_CHANGE_HISTORY)
                freqSendChangeList.removeFirst();
        }
    }

    public int[] getRecFrequency() {
        return freqRec.clone();
    }

    public int getSendFrequency() {
        return freqSend;
    }

    public boolean isAvaliableForWork() {
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean state) {
        enabled = state;

        if(this.hasLevel()) {
            this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(RadioStationBlock.ENABLED, state));
        }

        if(level.isClientSide) {
            CPacketSetRadioStationEnabled packet = new CPacketSetRadioStationEnabled(state, this.worldPosition);
            ModPacketHandler.sendToServer(packet);
        } else if(enabled) {
            initStationFreqHistory();
        }
    }

    public void initStationFreqHistory() {
        long enableTimeStamp = System.currentTimeMillis();
        freqRecChangeList.clear();
        freqRecChangeList.add(new Pair<>(enableTimeStamp, getRecFrequency()));
        freqSendChangeList.clear();
        freqSendChangeList.add(new Pair<>(enableTimeStamp, getSendFrequency()));
    }

    public long getEnableTimeStamp() {
        return freqRecChangeList.size() > 0 ? freqRecChangeList.get(0).getKey() : System.currentTimeMillis();
    }

    public List<RadioStationRecieveFrequencyHistoryItem> getFreqRecChangeHistory() {
        List<RadioStationRecieveFrequencyHistoryItem> list = new LinkedList<>();
        for(Pair<Long, int[]> p : freqRecChangeList) {
            list.add(new RadioStationRecieveFrequencyHistoryItem(p.getValue(), p.getKey()));
        }
        return list;
    }

    public List<RadioStationSendFrequencyHistoryItem> getFreqSendChangeHistory() {
        List<RadioStationSendFrequencyHistoryItem> list = new LinkedList<>();
        for(Pair<Long, Integer> p : freqSendChangeList) {
            list.add(new RadioStationSendFrequencyHistoryItem(p.getValue(), p.getKey()));
        }
        return list;
    }

    public void sendHistoryUpdateToClient(ServerPlayerEntity player) {
        List<RadioStationRecieveFrequencyHistoryItem> freqRecChangeHistory = getFreqRecChangeHistory();
        List<RadioStationSendFrequencyHistoryItem> freqSendChangeHistory = getFreqSendChangeHistory();
        List<IHistoryItem> historyList = new LinkedList<>();

        int[] zeroFreq = freqRecChangeHistory.size() > 0 ? freqRecChangeHistory.get(0).newFreq : getRecFrequency();
        long firstStamp = freqRecChangeHistory.size() > 1 ? freqRecChangeHistory.get(1).getTimestamp() : System.currentTimeMillis();

        historyList.addAll(
                RadioMessageRegistry.convertMessageToTextListAndCorrupt(
                        RadioMessageRegistry.getMessagesFromFreqRange(getEnableTimeStamp(), firstStamp, zeroFreq[0], zeroFreq[1]),
                        this.getBlockPos()
                ));

        if(freqRecChangeHistory.size() > 2) {
            for(int i = 2; i < freqRecChangeHistory.size(); ++i) {
                RadioStationRecieveFrequencyHistoryItem prev = freqRecChangeHistory.get(i - 1);
                RadioStationRecieveFrequencyHistoryItem cur = freqRecChangeHistory.get(i);

                historyList.addAll(
                        RadioMessageRegistry.convertMessageToTextListAndCorrupt(
                                RadioMessageRegistry.getMessagesFromFreqRange(prev.getTimestamp(), cur.getTimestamp(), prev.newFreq[0], prev.newFreq[1]),
                                this.getBlockPos()
                        ));
            }
        }

        if(freqRecChangeHistory.size() > 1) {
            RadioStationRecieveFrequencyHistoryItem last = freqRecChangeHistory.get(freqRecChangeHistory.size() - 1);

            historyList.addAll(
                    RadioMessageRegistry.convertMessageToTextListAndCorrupt(
                            RadioMessageRegistry.getMessagesFromFreqRange(last.getTimestamp(), Long.MAX_VALUE, last.newFreq[0], last.newFreq[1]),
                            this.getBlockPos()
                    ));
        }

        historyList.addAll(freqRecChangeHistory);
        historyList.addAll(freqSendChangeHistory);

        historyList = RadioMessageRegistry.sortAndLimitMessagesByTimestamps(historyList);

        SPacketSendRadioStationHistory packet = new SPacketSendRadioStationHistory(historyList);
        ModPacketHandler.sendTo(packet, player);
    }
}
