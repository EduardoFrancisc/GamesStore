package br.edu.infnet.product.repository;

import br.edu.infnet.product.domain.model.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}
