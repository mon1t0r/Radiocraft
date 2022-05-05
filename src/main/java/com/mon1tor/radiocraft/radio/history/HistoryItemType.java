package com.mon1tor.radiocraft.radio.history;

import net.minecraft.network.PacketBuffer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public enum HistoryItemType {
    RADIO_FREQUENCY_CHANGE(RadioFrequencyHistoryItem::write, RadioFrequencyHistoryItem::read),
    RADIO_STATION_RECIEVE_FREQUENCY_CHANGE(RadioStationRecieveFrequencyHistoryItem::write, RadioStationRecieveFrequencyHistoryItem::read),
    RADIO_STATION_SEND_FREQUENCY_CHANGE(RadioStationSendFrequencyHistoryItem::write, RadioStationSendFrequencyHistoryItem::read),
    TEXT(TextHistoryItem::write, TextHistoryItem::read);

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
