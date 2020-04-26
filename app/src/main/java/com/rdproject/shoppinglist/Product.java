package com.rdproject.shoppinglist;


import com.rdproject.shoppinglist.adapters.RVAdapter;

import java.util.Objects;

public class Product {

        private String productId;
        private String name;
        private String quantity;
        private int status;

    public Product() {
    }

        public Product(String name, String quantity) {
            this.name = name;
            this.quantity = quantity;
            this.status = RVAdapter.STATUS_TODO;
        }

    public Product(String name, String quantity, int status) {
        this.name = name;
        this.quantity = quantity;
        this.status = status;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", quantity='" + quantity + '\'' +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(productId);
    }
}
