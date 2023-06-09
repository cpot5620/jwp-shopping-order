package cart.application;

import cart.domain.product.ImageUrl;
import cart.domain.product.Name;
import cart.domain.product.Price;
import cart.domain.product.Product;
import cart.dto.request.ProductRequest;
import cart.dto.response.ProductResponse;
import cart.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        return products.stream().map(ProductResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.getProductById(productId);
        return ProductResponse.of(product);
    }

    public Long createProduct(ProductRequest productRequest) {
        Product product = new Product(
                new Name(productRequest.getName()),
                new ImageUrl(productRequest.getImageUrl()),
                new Price(productRequest.getPrice())
        );
        return productRepository.createProduct(product);
    }

    public void updateProduct(Long productId, ProductRequest productRequest) {
        Product product = new Product(
                new Name(productRequest.getName()),
                new ImageUrl(productRequest.getImageUrl()),
                new Price(productRequest.getPrice())
        );
        productRepository.updateProduct(productId, product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteProduct(productId);
    }
}
