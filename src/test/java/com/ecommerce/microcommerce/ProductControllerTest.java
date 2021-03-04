package com.ecommerce.microcommerce;

import com.ecommerce.microcommerce.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllProducts() throws Exception {
        mockMvc.perform( MockMvcRequestBuilders
                .get("/Products")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    public void should_return_product_with_id_13() throws Exception {
        mockMvc.perform( MockMvcRequestBuilders
                .get("/Products/{id}", 22)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void should_insert_one_more_product() throws Exception {
        Product p =new Product("Tv Samsung", 500, 200);
        mockMvc.perform( MockMvcRequestBuilders
                .post("/Products")
                .content(asJsonString(p))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_update_product_aspirateur_with_id_13() throws Exception {
        Product p = new Product("L'aspirateur idéal", 14, 3); p.setId(13);
        mockMvc.perform( MockMvcRequestBuilders
                .put("/Products")
                .content(asJsonString(p))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_delete_product_13()throws Exception{
        mockMvc.perform( MockMvcRequestBuilders.delete("/Products/{id}", 13) )
                .andExpect(status().isAccepted());
    }

    @Test
    public void should_return_products_with_name_aspirateur()throws Exception{
        MvcResult mvcResult = mockMvc.perform( MockMvcRequestBuilders.get("/Products/search/{name}", "Aspirateur")).andReturn();
        List<Product> productList = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Product>>(){});
        assertEquals(1,  productList.size());
    }

    @Test
    public void should_return_products_with_price_greater_than_500()throws Exception{
        MvcResult mvcResult = mockMvc.perform( MockMvcRequestBuilders.get("/Products/filterGT/{priceLimit}", 500)).andReturn();
        List<Product> listproducts = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Product>>(){});
        assertEquals(2,  listproducts.size());
    }

    @Test
    public void should_return_products_sorted()throws Exception{
        MvcResult mvcResult = mockMvc.perform( MockMvcRequestBuilders.get("/ProductsSorted")).andReturn();
        List<Product> listproducts = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Product>>(){});
        boolean isSorted = IntStream.range(1, listproducts.size()).map(index -> listproducts.get(index - 1).compareTo(listproducts.get(index))).allMatch(order -> order <= 0);
        assertTrue(isSorted);
    }

    @Test
    public void should_return_margins_of_each_products()throws Exception{
        MvcResult mvcResult = mockMvc.perform( MockMvcRequestBuilders.get("/Products/Margin")).andReturn();
        Map<String, Integer> map = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<Map<String,Integer>>(){});
        List<Product> listProducts = map.keySet().stream().map(ProductControllerTest::asProduct).collect(Collectors.toList());
        Map<Product, Integer> mapProducts = new HashMap<>();
        AtomicInteger i = new AtomicInteger();
        listProducts.forEach(p ->{ mapProducts.put(p, (Integer) map.values().toArray()[i.get()]); i.getAndIncrement();});
        boolean isIncoherent = mapProducts.entrySet().stream().anyMatch(e -> (e.getKey().getPrice() - e.getKey().getPriceBuying()) != e.getValue());
        assertFalse(isIncoherent);
    }

    public static Product asProduct(String p){
        try {
            return new ObjectMapper().readValue(p, Product.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
