package com.example.service;

import com.example.dao.OrderDAO;
import com.example.dao.ProductInDisplayDAO;
import com.example.dto.VendorOrdersResponse;
import com.example.dto.CustomProductsResponse;
import com.example.model.Product;
import com.example.model.ProductInDisplay;

import java.sql.SQLException;
import java.util.List;

public class ProductInDisplayService {

    private final ProductInDisplayDAO dao = new ProductInDisplayDAO();
    private final OrderDAO orderDAO= new OrderDAO();

    public void createProduct(int productId, int vendorId, double price, int quantity)
            throws SQLException {

        validateProductId(productId);
        validateVendorId(vendorId);
        validatePrice(price);
        validateQuantity(quantity);

        ProductInDisplay listing = new ProductInDisplay();
        listing.setProductId(productId);
        listing.setVendorId(vendorId);
        listing.setPrice(price);
        listing.setQuantity(quantity);

        dao.create(listing);
    }

    public List<CustomProductsResponse> getAllVendorProducts(int vendorId)
            throws SQLException {

        validateVendorId(vendorId);
        return dao.findByVendorId(vendorId);
    }

    public void updateProduct(
            int productInDisplayId,
            int vendorId,
            Double price,
            Integer quantity
    ) throws SQLException {

        validateProductInDisplayId(productInDisplayId);
        validateVendorId(vendorId);

        if (price == null && quantity == null) {
            throw new IllegalArgumentException(
                    "At least one field must be provided"
            );
        }

        if (price != null) {
            validatePrice(price);
        }

        if (quantity != null) {
            validateQuantity(quantity);
        }

        boolean updated = dao.update(
                productInDisplayId,
                vendorId,
                price,
                quantity
        );

        if (!updated) {
            throw new IllegalArgumentException(
                    "Product listing not found"
            );
        }
    }

    public void deleteVendorProduct(int productInDisplayId, int vendorId)
            throws SQLException {

        validateProductInDisplayId(productInDisplayId);
        validateVendorId(vendorId);

        boolean deleted = dao.delete(
                productInDisplayId,
                vendorId
        );

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Product listing not found"
            );
        }
    }

//    public List<OrderItems> getVendorOrders(int)

    public List<CustomProductsResponse> displayAllProduct(int productId) throws SQLException{
        validateProductId(productId);
        return dao.getAllProduct(productId);
    }

    public List<VendorOrdersResponse> getAllVendorOrder(int vendorId) throws SQLException{
        return orderDAO.getAllOrderOfVendor(vendorId);
    }

    public List<Product> getAllProducts() throws SQLException{
        return dao.getAllProductCategories();
    }

    private void validateProductId(int productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException(
                    "Product ID must be greater than 0"
            );
        }
    }

    private void validateProductInDisplayId(int productInDisplayId) {
        if (productInDisplayId <= 0) {
            throw new IllegalArgumentException(
                    "Product listing ID must be greater than 0"
            );
        }
    }

    private void validateVendorId(int vendorId) {
        if (vendorId <= 0) {
            throw new IllegalArgumentException(
                    "Vendor ID must be greater than 0"
            );
        }
    }

    private void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than 0"
            );
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than 0"
            );
        }
    }
}