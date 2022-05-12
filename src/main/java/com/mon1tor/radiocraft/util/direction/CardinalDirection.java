package com.mon1tor.radiocraft.util.direction;

import com.mon1tor.radiocraft.util.MathUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum CardinalDirection {
    NORTH(0, "north", 337.5F),
    NORTHEAST(1, "northeast", 22.5F),
    EAST(2, "east", 67.5F),
    SOUTHEAST(3, "southeast", 112.5F),
    SOUTH(4, "south", 157.5F),
    SOUTHWEST(5, "southwest", 202.5F),
    WEST(6, "west", 247.5F),
    NORTHWEST(7, "northwest", 292.5F);

    final int index;
    final String name;
    final float cwAngleStart;

    CardinalDirection(int index, String name, float cwAngleStart) {
        this.index = index;
        this.name = name;
        this.cwAngleStart = cwAngleStart;
    }

    public ITextComponent getName() {
        return new TranslationTextComponent("screen.radiocraft.direction." + name);
    }

    public static CardinalDirection fromAngle(float angle) {
        CardinalDirection[] values = values();
        angle = MathUtils.normalizeAngle(angle);

        if(angle > values[values.length - 1].cwAngleStart && angle < values[0].cwAngleStart)
            return values[values.length - 1];

        for(int i = 0; i < values.length - 1; ++i) {
            if(angle > values[i].cwAngleStart && angle < values[i + 1].cwAngleStart)
                return values[i];
        }
        return CardinalDirection.NORTH;
    }
}
