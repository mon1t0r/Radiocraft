package com.mon1tor.radiocraft.util.direction;

public class DirectionRange {
    public static final DirectionRange ZERO = new DirectionRange(0, 0);

    public final float angleFrom; //North-Object bearing
    public final float angleTo; //North-Object bearing

    public DirectionRange(float angleFrom, float angleTo) {
        this.angleFrom = angleFrom;
        this.angleTo = angleTo;
    }

    public CardinalDirection getAverageDirection() {
        return CardinalDirection.fromAngle((this.angleFrom + this.angleTo) / 2.0F);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DirectionRange that = (DirectionRange) o;
        return Float.compare(that.angleFrom, angleFrom) == 0 && Float.compare(that.angleTo, angleTo) == 0;
    }
}
