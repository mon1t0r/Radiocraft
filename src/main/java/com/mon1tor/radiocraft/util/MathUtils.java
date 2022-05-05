package com.mon1tor.radiocraft.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Random;

public class MathUtils {
    public static float getDistance(BlockPos pos1, BlockPos pos2) {
        return (float) Math.sqrt(pos1.distSqr(pos2));
    }

    public static int[] getUniqueIntsArray(Random random, int len, int min, int max) {
        if(len > max - min)
            len = max - min;

        ArrayList<Integer> list = new ArrayList<>(max - min);
        for (int i = min; i < max; ++i){
            list.add(i);
        }

        int[] res = new int[len];
        for (int i = 0; i < len; ++i) {
            int ind = (int) (random.nextDouble() * list.size());
            res[i] = list.get(ind);
            list.remove(ind);
        }
        return res;
    }
}
