package com.ecommerce.microcommerce.dao;
import com.ecommerce.microcommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDao extends JpaRepository<Product, Integer> {
    List<Product> findByPriceGreaterThan(int prixLimit);
    List<Product> findByNameLike(String search);
    void deleteById(int id);
}