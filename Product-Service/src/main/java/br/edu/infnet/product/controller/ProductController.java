package br.edu.infnet.product.controller;

import br.edu.infnet.product.dto.CreateProductRequest;
import br.edu.infnet.product.dto.ProductResponse;
import br.edu.infnet.product.integration.order.OrderItemDTO;
import br.edu.infnet.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable String id) {
        return productService.findById(id);
    }

    @PatchMapping("/update-stock")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reduceProductQuantityStock(@Valid @RequestBody List<OrderItemDTO> items) {
        productService.reduceProductQuantityStockBatch(items);
    }
}