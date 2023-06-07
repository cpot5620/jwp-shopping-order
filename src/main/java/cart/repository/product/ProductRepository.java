package cart.repository.product;

import cart.dao.product.ProductDao;
import cart.domain.product.Product;
import cart.entity.product.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final ProductDao productDao;

    public ProductRepository(final ProductDao productDao) {
        this.productDao = productDao;
    }

    public Product findProductById(final long id) {
        ProductEntity productEntity = productDao.findById(id);

        if (productEntity.getSalePrice() == null) {
            return new Product(productEntity.getId(), productEntity.getName(), productEntity.getPrice(), productEntity.getImageUrl(), 0);
        }

        return new Product(productEntity.getId(), productEntity.getName(), productEntity.getPrice(), productEntity.getImageUrl(), productEntity.getSalePrice());
    }

    public List<Product> findAllProducts() {
        List<ProductEntity> productEntities = productDao.getAllProducts();

        return productEntities.stream()
                .map(entity -> new Product(entity.getId(), entity.getName(), entity.getPrice(), entity.getImageUrl(), entity.getSalePrice()))
                .collect(Collectors.toList());
    }

    public Long createProduct(final Product product) {
        return productDao.createProduct(product);
    }

    public void updateProduct(final Long productId, final Product product) {
        productDao.updateProduct(productId, product);
    }

    public void deleteProduct(final Long productId) {
        productDao.deleteProduct(productId);
    }

    public void applySale(final long productId, final int salePrice) {
        productDao.updateSaleAmount(productId, salePrice);
    }

    public void unapplySale(final long productId) {
        productDao.unApplySale(productId);
    }
}