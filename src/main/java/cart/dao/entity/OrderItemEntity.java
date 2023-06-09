package cart.dao.entity;

import cart.domain.Product;
import cart.domain.order.OrderItem;

public class OrderItemEntity {
    private final Long id;
    private final String name;
    private final Long price;
    private final String imageUrl;
    private final Long quantity;
    private final Long productId;
    private final Long orderId;

    public OrderItemEntity(String name, Long price, String imageUrl, Long quantity, Long productId, Long orderId) {
        this(null, name, price, imageUrl, quantity, productId, orderId);
    }

    public OrderItemEntity(Long id, String name, Long price, String imageUrl, Long quantity, Long productId, Long orderId) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.productId = productId;
        this.orderId = orderId;
    }

    public static OrderItemEntity toEntity(OrderItem orderItem, Long orderId) {
        return new OrderItemEntity(
                orderItem.getProduct().getName(),
                orderItem.getProduct().getPrice(),
                orderItem.getProduct().getImageUrl(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                orderId);
    }

    public Product toProduct() {
        return new Product(productId, name, price, imageUrl);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
