package com.mon1tor.radiocraft.util;

import com.mon1tor.radiocraft.radio.history.HistoryItemType;
import com.mon1tor.radiocraft.radio.history.IHistoryItem;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector2f;

import java.util.LinkedList;
import java.util.List;

public class PacketBufferUtils {
    public static void writeIntArray(PacketBuffer buf, int[] arr) {
        buf.writeInt(arr.length);
        for(int i = 0; i < arr.length; ++i) {
            buf.writeInt(arr[i]);
        }
    }

    public static int[] readIntArray(PacketBuffer buf) {
        int len = buf.readInt();
        int[] arr = new int[len];
        for(int i = 0; i < len; ++i) {
            arr[i] = buf.readInt();
        }
        return arr;
    }

    public static void writeMessageHistory(PacketBuffer buf, List<IHistoryItem> arr) {
        buf.writeInt(arr.size());
        for(int i = 0; i < arr.size(); ++i) {
            writeHistoryItem(buf, arr.get(i));
        }
    }

    public static List<IHistoryItem> readMessageHistory(PacketBuffer buf) {
        int len = buf.readInt();
        List<IHistoryItem> arr = new LinkedList<>();
        for(int i = 0; i < len; ++i) {
            arr.add(readHistoryItem(buf));
        }
        return arr;
    }

    public static void writeHistoryItem(PacketBuffer buf, IHistoryItem item) {
        HistoryItemType type = item.getType();
        buf.writeEnum(type);
        type.writeToBuffer(item, buf);
    }

    public static IHistoryItem readHistoryItem(PacketBuffer buf) {
        HistoryItemType type = buf.readEnum(HistoryItemType.class);
        IHistoryItem item = type.readFromBuffer(buf);
        return item;
    }

    public static void writeVector2f(PacketBuffer buf, Vector2f vec) {
        buf.writeFloat(vec.x);
        buf.writeFloat(vec.y);
    }

    public static Vector2f readVector2f(PacketBuffer buf) {
        float x = buf.readFloat();
        float y = buf.readFloat();
        return new Vector2f(x, y);
    }
}
