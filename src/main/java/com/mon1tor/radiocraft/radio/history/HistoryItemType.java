package com.mon1tor.radiocraft.radio.history;

import net.minecraft.network.PacketBuffer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public enum HistoryItemType {
    RADIO_STATION_RECEIVE_FREQUENCY_CHANGE(RadioStationRecieveFrequencyHistoryItem::write, RadioStationRecieveFrequencyHistoryItem::read),
    RADIO_STATION_SEND_FREQUENCY_CHANGE(RadioStationSendFrequencyHistoryItem::write, RadioStationSendFrequencyHistoryItem::read),
    RADIO_STATION_TEXT(RadioStationTextHistoryItem::write, RadioStationTextHistoryItem::read),
    RADIO_TEXT(RadioTextHistoryItem::write, RadioTextHistoryItem::read),
    DIRECTION_FINDER_TEXT(DirectionFinderTextHistoryItem::write, DirectionFinderTextHistoryItem::read),
    RADIO_FREQUENCY_CHANGE(RadioFrequencyChangeHistoryItem::write, RadioFrequencyChangeHistoryItem::read);

    private final BiConsumer<IHistoryItem, PacketBuffer> encoder;
    private final Function<PacketBuffer, IHistoryItem> decoder;

    <T extends IHistoryItem> HistoryItemType(BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer, T> decoder) {
        this.encoder = (BiConsumer<IHistoryItem, PacketBuffer>) encoder;
        this.decoder = (Function<PacketBuffer, IHistoryItem>) decoder;
    }

    public void writeToBuffer(IHistoryItem item, PacketBuffer buf) {
        encoder.accept(item, buf);
    }

    public IHistoryItem readFromBuffer(PacketBuffer buf) {
        return decoder.apply(buf);
    }
}
