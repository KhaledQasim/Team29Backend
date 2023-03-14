package com.team29.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team29.backend.model.Product;
import com.team29.backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, allowCredentials = "true")
public class NotificationController {
    private final ProductRepository productRepository;
   

    @GetMapping("/low-stock")
    ArrayList<Product> getLowStock() {
        ArrayList<Product> LowStock = new ArrayList<>();
        for (Product temp : productRepository.findAll()) {
            if (temp.getQuantity() <= 5 && temp.getQuantity() > 0) {
                LowStock.add(temp);

            }
        }
        return LowStock;
    }
    
    @GetMapping("/no-stock")
    ArrayList<Product> getNoStock() {
        ArrayList<Product> NoStock = new ArrayList<>();
        for (Product temp : productRepository.findAll()) {
            if (temp.getQuantity() <= 0) {
                NoStock.add(temp);
            }
        }
        return NoStock;
    }
}
