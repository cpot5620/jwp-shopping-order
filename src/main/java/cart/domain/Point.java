package cart.domain;

import cart.exception.ErrorMessage;
import cart.exception.MemberException;
import java.util.Objects;

public class Point {
    private static final double ACCUMULATION_RATE = 0.1;
    private final int value;

    public Point(final int value) {
        validate(value);
        this.value = value;
    }

    public static Point fromTotalPrice(int totalPrice) {
        return new Point((int) Math.floor(totalPrice * ACCUMULATION_RATE));
    }

    private void validate(int value) {
        if (value < 0) {
            throw new MemberException(ErrorMessage.INVALID_POINT);
        }
    }

    public Point accumulate(Point point) {
        return new Point(this.value + point.value);
    }

    public Point use(Point point) {
        return new Point(this.value - point.value);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Point point = (Point) o;
        return value == point.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
