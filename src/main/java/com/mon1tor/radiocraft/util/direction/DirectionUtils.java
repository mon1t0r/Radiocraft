package com.mon1tor.radiocraft.util.direction;

import com.mon1tor.radiocraft.util.MathUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class DirectionUtils {
    private static final float DEGREE_ERROR_MAX = 10.0F; //Degrees
    private static final float DEGREE_ERROR_MAX_DIST = 100F; //Blocks
    private static final float DEGREE_ERROR_RAND_RANGE_MIN = 0.8f;
    private static final float DEGREE_ERROR_RAND_RANGE_MAX = 1.2f;
    private static final Random random = new Random();

    public static DirectionRange getRandomDirectionRangeFromPos(BlockPos curPos, BlockPos targetPos) {
        if(curPos.equals(targetPos)) {
            return DirectionRange.ZERO;
        }
        double theta = Math.atan2(targetPos.getZ() - curPos.getZ(), targetPos.getX() - curPos.getX());
        theta += Math.PI / 2.0D;

        float angle = (float) Math.toDegrees(theta);

        if (angle < 0)
            angle += 360;
        if(angle > 360)
            angle -= 360;

        float dist = MathUtils.getDistance(curPos, targetPos);
        if(dist > DEGREE_ERROR_MAX_DIST)
            dist = DEGREE_ERROR_MAX_DIST;

        float error = DEGREE_ERROR_MAX * (dist / DEGREE_ERROR_MAX_DIST);
        float errorCCW = error * MathHelper.nextFloat(random, DEGREE_ERROR_RAND_RANGE_MIN, DEGREE_ERROR_RAND_RANGE_MAX);
        float errorCW = error * MathHelper.nextFloat(random, DEGREE_ERROR_RAND_RANGE_MIN, DEGREE_ERROR_RAND_RANGE_MAX);

        return new DirectionRange(angle - errorCCW, angle + errorCW);
    }
}
