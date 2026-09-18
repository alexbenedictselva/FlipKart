package com.example.service;

import com.example.dao.CartDAO;
import com.example.dao.ProductInDisplayDAO;
import com.example.dto.CartItemsResponse;
import com.example.model.CartItem;
import com.example.model.ProductInDisplay;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private final CartDAO cartDAO = new CartDAO();
    private final ProductInDisplayDAO productInDisplayDAO = new ProductInDisplayDAO();

    public List<CartItemsResponse> getAllCartProducts(int custId) throws SQLException {
        return  cartDAO.viewAllCartItem(custId);

    }

    public boolean AddProduct(int custId,int productInDisId) throws SQLException{

        return cartDAO.AddToCart(custId,productInDisId);
    }
}
