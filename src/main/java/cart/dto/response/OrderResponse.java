package cart.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    @JsonProperty("priceBeforeDiscount")
    private final Integer totalPrice;
    @JsonProperty("priceAfterDiscount")
    private final Integer finalPrice;
    private final LocalDateTime date;
    private final List<ProductInOrderResponse> products;

    public OrderResponse(
            final Integer totalPrice,
            final Integer finalPrice,
            final LocalDateTime date,
            final List<ProductInOrderResponse> products
    ) {
        this.totalPrice = totalPrice;
        this.finalPrice = finalPrice;
        this.date = date;
        this.products = products;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public Integer getFinalPrice() {
        return finalPrice;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<ProductInOrderResponse> getProducts() {
        return products;
    }
}
