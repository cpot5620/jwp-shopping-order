package cart.application.event;

import cart.domain.member.Member;
import cart.dto.request.OrderRequest;

public class DefaultMemberPointUpdateEvent implements MemberPointUpdateEvent {

    private final Member member;
    private final OrderRequest orderRequest;
    private final Long orderId;

    public DefaultMemberPointUpdateEvent(
            final Member member,
            final OrderRequest orderRequest,
            final Long orderId
    ) {
        this.member = member;
        this.orderRequest = orderRequest;
        this.orderId = orderId;
    }

    public Member getMember() {
        return member;
    }

    public OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public Long getOrderId() {
        return orderId;
    }
}
