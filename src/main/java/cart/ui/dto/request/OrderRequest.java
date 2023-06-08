package cart.ui.dto.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderRequest {

    private List<OrderProductRequest> products;
    private int totalProductAmount;
    private int deliveryAmount;
    private String address;
    private Long couponId;

    public OrderRequest() {
    }

    public OrderRequest(final List<OrderProductRequest> products, final int totalProductAmount,
        final int deliveryAmount, final String address,
        final Long couponId) {
        this.products = products;
        this.totalProductAmount = totalProductAmount;
        this.deliveryAmount = deliveryAmount;
        this.address = address;
        this.couponId = couponId;
    }

    public Map<Long, Integer> findProductIdAndQuantity() {
        return products.stream()
            .collect(Collectors
                .toMap(OrderProductRequest::getId, OrderProductRequest::getQuantity, (a, b) -> b, HashMap::new));
    }

    public List<OrderProductRequest> getProducts() {
        return products;
    }

    public int getTotalProductAmount() {
        return totalProductAmount;
    }

    public int getDeliveryAmount() {
        return deliveryAmount;
    }

    public String getAddress() {
        return address;
    }

    public Long getCouponId() {
        return couponId;
    }
}
