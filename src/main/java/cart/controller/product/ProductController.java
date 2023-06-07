package cart.controller.product;

import cart.dto.product.ProductRequest;
import cart.dto.product.ProductResponse;
import cart.dto.sale.SaleProductRequest;
import cart.service.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAllProducts() {
        return ResponseEntity.ok(productService.findAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable final Long productId) {
        return ResponseEntity.ok(productService.findProductById(productId));
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody @Valid final ProductRequest productRequest) {
        long productId = productService.createProduct(productRequest);
        return ResponseEntity.created(URI.create("/products/" + productId)).build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable final Long productId,
                                              @RequestBody @Valid final ProductRequest productRequest) {
        productService.updateProduct(productId, productRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{productId}/sales")
    public ResponseEntity<Void> applySale(@PathVariable final Long productId,
                                          @RequestBody @Valid final SaleProductRequest request) {
        long id = productService.applySale(productId, request);
        return ResponseEntity.created(URI.create("/products/" + id)).build();
    }

    @DeleteMapping("/{productId}/sales")
    public ResponseEntity<Void> unapplySale(@PathVariable final Long productId) {
        long id = productService.unapplySale(productId);
        return ResponseEntity.ok().location(URI.create("/products/" + id)).build();
    }
}