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

    public static float normalizeAngle(float angle) {
        while(angle > 360)
            angle -= 360;
        while (angle < 0)
            angle += 360;
        return angle;
    }

    public static float distBetweenAngles(float a1, float a2) {
        float phi = Math.abs(a1 - a2) % 360;
        return phi > 180.0f ? 360.0f - phi : phi;
    }

    public static boolean isAngleBetween(float angle, float angle1, float angle2) {
        angle = normalizeAngle(angle);
        angle1 = normalizeAngle(angle1);
        angle2 = normalizeAngle(angle2);

        if (angle1 < angle2)
            return angle1 <= angle && angle <= angle2;
        return angle1 <= angle || angle <= angle2;
    }
}
