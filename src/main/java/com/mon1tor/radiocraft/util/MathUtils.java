package com.mon1tor.radiocraft.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;

public class MathUtils {
    public static float getDistance(BlockPos pos1, BlockPos pos2) {
        return (float) Math.sqrt(pos1.distSqr(pos2));
    }

    public static int[] getUniqueIntsArray(int len) {
        ArrayList<Integer> list = new ArrayList<>(len);
        for (int i = 0; i < len; ++i){
            list.add(i);
        }
        Collections.shuffle(list);
        return list.stream().mapToInt(i -> i).toArray();
    }
}
