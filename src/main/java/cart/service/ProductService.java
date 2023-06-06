package cart.service;

import cart.domain.product.Product;
import cart.domain.product.repository.ProductRepository;
import cart.dto.ProductRequest;
import cart.dto.ProductResponse;
import cart.exception.ExceptionType;
import cart.exception.ProductException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ExceptionType.NOT_FOUND_PRODUCT));
        return ProductResponse.of(product);
    }

    public Long create(ProductRequest productRequest) {
        Product product = new Product(
                productRequest.getName(),
                productRequest.getPrice(),
                productRequest.getImageUrl()
        );
        return productRepository.save(product).getId();
    }

    public void updateProduct(Long productId, ProductRequest productRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ExceptionType.NOT_FOUND_PRODUCT));
        product.update(productRequest.getName(), productRequest.getPrice(), productRequest.getImageUrl());
        productRepository.update(product);
    }

    public void deleteById(Long productId) {
        productRepository.deleteProduct(productId);
    }
}