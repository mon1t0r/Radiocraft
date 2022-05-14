package com.mon1tor.radiocraft.radio;

import com.mon1tor.radiocraft.util.MathUtils;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class RadioMessageCorrupter {
    private static final char[] CORRUPT_SYMBOLS = new char[] { '@', '#', '$', '%', '&', '*' };

    public static String corruptMessageFromDist(String msg, float distance, SenderType sender, long corruptSeed) {
        Random random = new Random(corruptSeed);
        if(distance < sender.minCorruptDist)
            return msg;
        if(msg.length() <= 0 || distance > sender.maxCorruptDist)
            return "";
        distance -= sender.minCorruptDist;

        StringBuilder result = new StringBuilder(msg);

        int corruptedCharsCount = Math.round(result.length() * (distance / (sender.maxCorruptDist - sender.minCorruptDist)));
        int[] indArr = MathUtils.getUniqueIntsArray(random, corruptedCharsCount, 0, result.length());

        for(int i = 0; i < corruptedCharsCount; ++i) {
            result.setCharAt(indArr[i], getRandomCorruptSymbol(random));
        }
        return result.toString();
    }

    public static String corruptMessageFromDist(String msg, float distance, SenderType sender) {
        return corruptMessageFromDist(msg, distance, sender, -1);
    }

    public static String corruptMessageFromDist(String msg, BlockPos pos1, BlockPos pos2, SenderType sender) {
        return corruptMessageFromDist(msg, MathUtils.getDistance(pos1, pos2), sender);
    }
    public static String corruptMessageFromDist(String msg, BlockPos pos1, BlockPos pos2, SenderType sender, long seed) {
        return corruptMessageFromDist(msg, MathUtils.getDistance(pos1, pos2), sender, seed);
    }

    private static char getRandomCorruptSymbol(Random random) {
        return CORRUPT_SYMBOLS[random.nextInt(CORRUPT_SYMBOLS.length)];
    }

    public enum SenderType {
        RADIO(400, 1000),
        RADIO_STATION(1200, 2500);

        final float minCorruptDist;
        final float maxCorruptDist;

        SenderType(float minCorruptDist, float maxCorruptDist) {
            this.minCorruptDist = minCorruptDist;
            this.maxCorruptDist = maxCorruptDist;
        }

    }
}
