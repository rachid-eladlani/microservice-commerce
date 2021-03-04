package com.ecommerce.microcommerce.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;

@Entity
public class Product implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Length(min=3, max=20)
    private String name;

    @Min(value = 1)
    private int price;

    private int priceBuying;

    public Product() {
    }

    public Product(String name, int price, int priceBuying){
        this.name = name;
        this.price = price;
        this.priceBuying = priceBuying;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public int getPriceBuying() {
        return priceBuying;
    }

    public void setPriceBuying(int priceBuying) {
        this.priceBuying = priceBuying;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", BuyingPrice=" + priceBuying +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        Product r2 = (Product) o;
        int prenomComp =  getName().compareTo(r2.getName());
        if (prenomComp != 0)
            return prenomComp;
        else
            return getName().compareTo(r2.getName());    }
}
