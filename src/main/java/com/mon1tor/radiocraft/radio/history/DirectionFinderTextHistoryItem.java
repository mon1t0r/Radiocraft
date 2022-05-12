package com.mon1tor.radiocraft.radio.history;

import com.mon1tor.radiocraft.util.PacketBufferUtils;
import com.mon1tor.radiocraft.util.TimeUtils;
import com.mon1tor.radiocraft.util.direction.DirectionRange;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DirectionFinderTextHistoryItem implements IHistoryItem {
    public final String sender;
    public final String message;
    public final BlockPos recievePos;
    public final DirectionRange recieveDirection;
    private final long timestamp;

    public DirectionFinderTextHistoryItem(String sender, String message, BlockPos recievePos, DirectionRange recieveDirection, long timestamp) {
        this.sender = sender;
        this.message = message;
        this.recievePos = recievePos;
        this.recieveDirection = recieveDirection;
        this.timestamp = timestamp;
    }

    @Override
    public ITextComponent getDisplayText() {
        return new StringTextComponent("<" + sender + "-" + TimeUtils.timestampToString(timestamp) + "> " + message);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public HistoryItemType getType() {
        return HistoryItemType.DIRECTION_FINDER_TEXT;
    }

    public static void write(DirectionFinderTextHistoryItem item, PacketBuffer buf) {
        buf.writeUtf(item.sender);
        buf.writeUtf(item.message);
        buf.writeBlockPos(item.recievePos);
        PacketBufferUtils.writeDirectionRange(buf, item.recieveDirection);
        buf.writeLong(item.timestamp);
    }

    public static DirectionFinderTextHistoryItem read(PacketBuffer buf) {
        String s = buf.readUtf();
        String m = buf.readUtf();
        BlockPos p = buf.readBlockPos();
        DirectionRange d = PacketBufferUtils.readDirectionRange(buf);
        long t = buf.readLong();
        return new DirectionFinderTextHistoryItem(s, m, p, d, t);
    }
}