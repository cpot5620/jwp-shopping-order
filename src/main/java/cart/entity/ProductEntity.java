package cart.entity;

import cart.domain.Money;
import cart.domain.Product;

import java.math.BigDecimal;

public class ProductEntity {

    private final Long id;
    private final String name;
    private final String imageUrl;
    private final BigDecimal price;

    public ProductEntity(final String name, final String imageUrl, final BigDecimal price) {
        this(null, name, imageUrl, price);
    }

    public ProductEntity(final Long id, final String name, final String imageUrl, final BigDecimal price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public static ProductEntity from(final Product product) {
        return new ProductEntity(product.getId(), product.getName(), product.getImageUrl(), product.getPrice().getValue());
    }

    public Product toDomain() {
        return new Product(id, name, imageUrl, new Money(price));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
