package com.mon1tor.radiocraft.radio;

import com.mon1tor.radiocraft.util.MathUtils;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class RadioMessageCorrupter {
    public static final float MIN_CORRUPT_DIST = 20;
    public static final float MAX_CORRUPT_DIST = 120;
    private static final char[] CORRUPT_SYMBOLS = new char[] { '@', '#', '$', '%', '&', '*' };
    private static final float CORRUPT_RANGE = MAX_CORRUPT_DIST - MIN_CORRUPT_DIST;
    private static final Random random = new Random();

    public static String corruptMessageFromDist(String msg, float distance) {
        if(distance < MIN_CORRUPT_DIST)
            return msg;
        if(msg.length() <= 0 || distance > MAX_CORRUPT_DIST)
            return "";
        distance -= MIN_CORRUPT_DIST;

        StringBuilder result = new StringBuilder(msg);

        int corruptedCharsCount = Math.round(result.length() * (distance / CORRUPT_RANGE));
        int[] indArr = MathUtils.getUniqueIntsArray(corruptedCharsCount, 0, result.length());

        for(int i = 0; i < corruptedCharsCount; ++i) {
            result.setCharAt(indArr[i], getRandomCorruptSymbol());
        }
        return result.toString();
    }

    public static String corruptMessageFromDist(String msg, BlockPos pos1, BlockPos pos2) {
        return corruptMessageFromDist(msg, MathUtils.getDistance(pos1, pos2));
    }

    public static boolean isDistanceReachable(BlockPos pos1, BlockPos pos2) {
        return MathUtils.getDistance(pos1, pos2) <= MAX_CORRUPT_DIST;
    }

    private static char getRandomCorruptSymbol() {
        return CORRUPT_SYMBOLS[random.nextInt(CORRUPT_SYMBOLS.length)];
    }
}
