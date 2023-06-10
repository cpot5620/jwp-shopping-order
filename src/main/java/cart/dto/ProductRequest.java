package cart.dto;

import jdk.jfr.BooleanFlag;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public class ProductRequest {
    @NotEmpty
    private final String name;
    @Positive
    private final int price;
    private final String imageUrl;
    @PositiveOrZero
    private final Double pointRatio;
    @BooleanFlag
    private final boolean pointAvailable;

    public ProductRequest(String name, int price, String imageUrl, Double pointRatio, boolean pointAvailable) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.pointRatio = pointRatio;
        this.pointAvailable = pointAvailable;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getPointRatio() {
        return pointRatio;
    }

    public boolean getPointAvailable() {
        return pointAvailable;
    }
}
