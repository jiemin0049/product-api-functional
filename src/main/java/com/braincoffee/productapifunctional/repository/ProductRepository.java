package com.braincoffee.productapifunctional.repository;

import com.braincoffee.productapifunctional.model.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
