package com.braincoffee.productapifunctional;

import com.braincoffee.productapifunctional.handler.ProductHandler;
import com.braincoffee.productapifunctional.model.Product;
import com.braincoffee.productapifunctional.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class ProductApiFunctionalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApiFunctionalApplication.class, args);
    }


    @Bean
    CommandLineRunner init(ProductRepository repository) {
        return args -> {
            Flux<Product> productFlux = Flux.just(
                    new Product(null, "Big Latte", 2.99),
                    new Product(null, "Big Decaf", 2.49),
                    new Product(null, "Green Tea", 1.99))
                    .flatMap(p -> repository.save(p));

            productFlux
                    .thenMany(repository.findAll())
                    .subscribe(System.out::println);
        };
    }

    @Bean
    RouterFunction<ServerResponse> routes(ProductHandler handler) {
        /*
        return route(GET("/products").and(accept(APPLICATION_JSON)), handler::getAllProducts)
                .andRoute(POST("/products").and(contentType(APPLICATION_JSON)), handler::saveProduct)
                .andRoute(DELETE("/products").and(accept(APPLICATION_JSON)), handler::deleteAllProducts)
                .andRoute(GET("/products/events").and(accept(TEXT_EVENT_STREAM)), handler::getProductEvents)
                .andRoute(GET("/products/{id}").and(accept(APPLICATION_JSON)), handler::getProduct)
                .andRoute(PUT("/products/{id}").and(contentType(APPLICATION_JSON)), handler::updateProduct)
                .andRoute(DELETE("/products/{id}").and(accept(APPLICATION_JSON)), handler::deleteProduct);
         */
        return nest(path("/products"),
                nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON)).or(accept(TEXT_EVENT_STREAM)),
                        route(GET("/"), handler::getAllProducts)
                                .andRoute(method(POST), handler::saveProduct)
                                .andRoute(DELETE("/"), handler::deleteAllProducts)
                                .andRoute(GET("/events"), handler::getProductEvents)
                                .andNest(path("/{id}"),
                                        route(method(GET), handler::getProduct)
                                                .andRoute(method(PUT), handler::updateProduct)
                                                .andRoute(method(DELETE), handler::deleteProduct)
                                )
                )
        );
    }
}
