package cart.domain;

import cart.exception.IllegalMemberException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Order {

    private final Long id;
    private final Member member;
    private final List<OrderItem> orderItems;
    private final Point usedPoint;
    private final Point savedPoint;
    private final LocalDateTime orderedAt;

    public Order(
            final Member member,
            final List<OrderItem> orderItems,
            final Point usedPoint,
            final Point savedPoint
    ) {
        this(null, member, orderItems, usedPoint, savedPoint, null);
    }

    public Order(
            final Long id,
            final Member member,
            final List<OrderItem> orderItems,
            final int usedPoint,
            final int savedPoint,
            final LocalDateTime orderedAt
    ) {
        this(id, member, orderItems, new Point(usedPoint), new Point(savedPoint), orderedAt);
    }

    public Order(
            final Long id,
            final Member member,
            final List<OrderItem> orderItems,
            final Point usedPoint,
            final Point savedPoint,
            final LocalDateTime orderedAt
    ) {
        this.id = id;
        this.member = member;
        this.orderItems = orderItems;
        this.usedPoint = usedPoint;
        this.savedPoint = savedPoint;
        this.orderedAt = orderedAt;
    }

    public void checkOwner(final Member member) {
        if(this.member.equals(member)) {
            return;
        }
        throw new IllegalMemberException("다른 사용자의 주문 정보를 조회할 수 없습니다");
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public int getUsedPoint() {
        return usedPoint.getValue();
    }

    public int getSavedPoint() {
        return savedPoint.getValue();
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
