package cart.application;

import cart.dao.ProductDao;
import cart.domain.Product;
import cart.dto.request.ProductCreateRequest;
import cart.dto.request.ProductUpdateRequest;
import cart.dto.response.ProductResponse;
import cart.exception.ProductNotExistException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ProductService {

    private final ProductDao productDao;

    public ProductService(final ProductDao productDao) {
        this.productDao = productDao;
    }

    public List<ProductResponse> findAllProducts() {
        final List<Product> products = productDao.findAllProducts();
        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findProductById(final Long productId) {
        final Product product = productDao.findProductById(productId)
                .orElseThrow(() -> new ProductNotExistException(productId));

        return ProductResponse.of(product);
    }

    public List<ProductResponse> findProductById(final List<Long> productIds) {
        final List<Product> products = productDao.findByIds(productIds);

        return products.stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long addProduct(final ProductCreateRequest productCreateRequest) {
        final Product product = new Product(productCreateRequest.getName(), productCreateRequest.getPrice(),
                productCreateRequest.getImageUrl());
        return productDao.saveProduct(product);
    }

    @Transactional
    public void updateProduct(final Long productId, final ProductUpdateRequest request) {
        final Product product = new Product(request.getName(), request.getPrice(), request.getImageUrl());

        productDao.updateProduct(productId, product);
    }

    @Transactional
    public void removeProduct(final Long productId) {
        productDao.deleteProduct(productId);
    }
}
