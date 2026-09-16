package com.example.service;

import com.example.dao.CartDAO;
import com.example.dao.ProductInDisplayDAO;
import com.example.model.CartItem;
import com.example.model.ProductInDisplay;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private final CartDAO cartDAO = new CartDAO();
    private final ProductInDisplayDAO productInDisplayDAO = new ProductInDisplayDAO();

    public List<ProductInDisplay> getAllCartProducts(int custId) throws SQLException {
        List<CartItem> cartItems =  cartDAO.viewAllCartItem(custId);
        List<ProductInDisplay> productInDisplays = new ArrayList<>();
        for(CartItem i:cartItems){
            ProductInDisplay prod = productInDisplayDAO.getProduct(i.getProductInDisplayId());
            productInDisplays.add(prod);
        }
        return productInDisplays;
    }

    public boolean AddProduct(int custId,int productInDisId) throws SQLException{

        return cartDAO.AddToCart(custId,productInDisId);
    }
}
