package cart.service;

import cart.dao.ProductDao;
import cart.domain.Product;
import cart.dto.product.ProductCreateResponse;
import cart.dto.product.ProductRequest;
import cart.dto.product.ProductResponse;
import cart.exception.product.ProductException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productDao.getAllProducts();
        return products.stream().map(ProductResponse::of).collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productDao.getProductById(productId)
                .orElseThrow(ProductException.NotExistProduct::new);
        return ProductResponse.of(product);
    }

    public ProductCreateResponse createProduct(ProductRequest productRequest) {
        Product product = new Product(productRequest.getName(), productRequest.getPrice(), productRequest.getImageUrl());
        Long productId = productDao.createProduct(product);
        return new ProductCreateResponse(productId);
    }

    public void updateProduct(Long productId, ProductRequest productRequest) {
        Product product = new Product(productRequest.getName(), productRequest.getPrice(), productRequest.getImageUrl());
        productDao.updateProduct(productId, product);
    }

    public void deleteProduct(Long productId) {
        productDao.deleteProduct(productId);
    }
}
