package com.example.model;

public class Variant {
    int variantId;
    int productId;
    String sku;
    String color;
    String storage;

    public Variant() {
    }

    public Variant(int variantId, String sku, String color, String storage, int productId) {
        this.variantId = variantId;
        this.sku = sku;
        this.color = color;
        this.storage = storage;
        this.productId = productId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
