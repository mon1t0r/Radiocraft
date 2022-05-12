package com.mon1tor.radiocraft.radio;

import net.minecraft.util.math.MathHelper;

public class FrequencyConstants {
    public static final int MIN_FREQUENCY = 0;
    public static final int MAX_FREQUENCY = 100000;

    public static int clampFreq(int freq) {
        return MathHelper.clamp(freq, MIN_FREQUENCY, MAX_FREQUENCY);
    }

    public static int[] clampFreq(int[] freq) {
        return new int[] { clampFreq(freq[0]), clampFreq(freq[1]) };
    }
}
