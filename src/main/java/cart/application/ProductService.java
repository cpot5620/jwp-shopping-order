package cart.application;

import cart.domain.Product;
import cart.dao.ProductDao;
import cart.dto.ProductRequest;
import cart.dto.ProductResponse;
import cart.exception.NoSuchIdsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productDao.getAllProducts();
        return products.stream().map(ProductResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsById(List<Long> ids) {
        final List<Product> products = productDao.getProductsByIds(ids);
        if (products.size() != ids.size()) {
            throw new NoSuchIdsException(ids);
        }

        return products.stream().map(ProductResponse::of).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productDao.getProductById(productId);
        return ProductResponse.of(product);
    }

    public Long createProduct(ProductRequest productRequest) {
        Product product = new Product(productRequest.getName(), productRequest.getPrice(), productRequest.getImageUrl());
        return productDao.createProduct(product);
    }

    public void updateProduct(Long productId, ProductRequest productRequest) {
        Product product = new Product(productRequest.getName(), productRequest.getPrice(), productRequest.getImageUrl());
        productDao.updateProduct(productId, product);
    }

    public void deleteProduct(Long productId) {
        productDao.deleteProduct(productId);
    }
}
