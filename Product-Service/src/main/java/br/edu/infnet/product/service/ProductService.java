package br.edu.infnet.product.service;

import br.edu.infnet.product.domain.model.Product;
import br.edu.infnet.product.dto.CreateProductRequest;
import br.edu.infnet.product.dto.ProductResponse;
import br.edu.infnet.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse create(CreateProductRequest request) {
        // Converte DTO -> Entity
        Product product = new Product();
        product.setTitle(request.name()); // Mapeando 'name' do DTO para 'title' da Entity
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setPlatform(request.platform());
        product.setStockQuantity(request.stockQuantity());
        product.setReleaseDate(request.releaseDate());

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    public ProductResponse findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id)); // Futuramente crie a ProductNotFoundException

        return toResponse(product);
    }

    public List<ProductResponse> findAll() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getPrice(),
                p.getPlatform(),
                p.getStockQuantity(),
                p.getReleaseDate()
        );
    }
}