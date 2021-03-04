package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProductNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RestController
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @RequestMapping(value = "/Products", method = RequestMethod.GET)
    public List<Product> getAllProduct() {
        return productDao.findAll();
    }

    @RequestMapping(value = "/ProductsSorted", method = RequestMethod.GET)
    public List<Product> getAllProductSorted() {
        List<Product> products = productDao.findAll();
        Collections.sort(products);
        return products;
    }

    @GetMapping(value = "/Products/Margin")
    public Map<String, Integer> computeMarginOfProducts() throws JsonProcessingException {
        List<Product> products = productDao.findAll();
        Map<String, Integer> map = new HashMap<>();
        for (Product p : products){
            map.put(new ObjectMapper().writeValueAsString(p), p.getPrice()-p.getPriceBuying());
        }
        return map;
    }

    @GetMapping(value="/Products/{id}")
    public Product getProductById(@PathVariable int id) {
        Optional<Product> p = productDao.findById(id);
        if(!(p.isPresent()))
            throw new ProductNotFoundException("Product with id: " + id + " is not found.");
        return p.get();
    }

    @PostMapping(value = "/Products")
    public ResponseEntity<Void> addProduct(@Valid @RequestBody Product product) {
        Product productAdded =  productDao.save(product);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "/Products/filterGT/{priceLimit}")
    public List<Product> getProduitsGreaterThan(@PathVariable int priceLimit) {
        return productDao.findByPriceGreaterThan(priceLimit);
    }

    @GetMapping(value = "/Products/search/{name}")
    public List<Product> getProductBySearch(@PathVariable String name) {
        return productDao.findByNameLike("%"+name+"%");
    }

    @DeleteMapping (value = "/Products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productDao.deleteById(id);
        return ResponseEntity.accepted().build();
    }

    @PutMapping (value = "/Products")
    public ResponseEntity<Void> updateProduct(@RequestBody Product product) {
        productDao.save(product);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
