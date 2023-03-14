package com.team29.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team29.backend.exception.ProductNotFoundException;
import com.team29.backend.model.Product;
import com.team29.backend.repository.ProductRepository;

@RestController
// TO DO , place exact url of frontend server when ready to deploy
@RequestMapping
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    
    
    @PostMapping("/api/product")
    Product newProduct(@RequestBody Product newProduct){
        return productRepository.save(newProduct);
    }

    @GetMapping("/products")
    List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @GetMapping("/product/{id}")
    Product geProductById(@PathVariable Long id){
        return productRepository.findById(id)
                .orElseThrow(()->new ProductNotFoundException(id));
    }

    @PutMapping("/api/product/{id}")
    Product updateProduct(@RequestBody Product newProduct, @PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(newProduct.getName());
                    product.setPrice(newProduct.getPrice());
                    product.setImage(newProduct.getImage());
                    product.setImages(newProduct.getImages());
                    product.setDescription(newProduct.getDescription());
                    product.setCategory(newProduct.getCategory());
                    product.setSize(newProduct.getSize());
                    product.setQuantity(newProduct.getQuantity());
                    return productRepository.save(product);
                }).orElseThrow(() -> new ProductNotFoundException(id));
    }
    
  

    @DeleteMapping("api/product/{id}")
    String deleteUser(@PathVariable Long id){
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
        return "Product with id: "+id+" has been deleted!";
    }
}
