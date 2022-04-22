package com.mon1tor.radiocraft.util;

import net.minecraft.network.PacketBuffer;

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
}
