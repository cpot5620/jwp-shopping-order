package cart.application;

import cart.application.dto.request.ProductRequest;
import cart.application.dto.response.ProductResponse;
import cart.domain.product.Product;
import cart.persistence.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAllProducts().stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(final Long productId) {
        final Product product = productRepository.findProductById(productId);
        return ProductResponse.of(product);
    }

    @Transactional
    public Long create(final ProductRequest productRequest) {
        final Product product = new Product(
                productRequest.getName(),
                productRequest.getPrice(),
                productRequest.getImageUrl()
        );
        return productRepository.createProduct(product);
    }

    @Transactional
    public void update(final Long productId, final ProductRequest productRequest) {
        final Product product = new Product(
                productId,
                productRequest.getName(),
                productRequest.getPrice(),
                productRequest.getImageUrl()
        );
        productRepository.updateProduct(product);
    }

    @Transactional
    public void delete(final Long productId) {
        productRepository.deleteProduct(productId);
    }
}
