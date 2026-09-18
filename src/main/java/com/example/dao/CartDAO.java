package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.dto.CartItemsResponse;
import com.example.model.Cart;
import com.example.model.CartItem;

import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private final ProductInDisplayDAO productInDisplayDAO = new ProductInDisplayDAO();
    public Cart checkExistingCart(int custId) throws SQLException {
        String sql = """
               SELECT * FROM Cart
               WHERE CustomerId = ?""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ){
            preparedStatement.setInt(1,custId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(!resultSet.next()) {
                    return createCart(custId);
                }

                Cart cart = new Cart();
                cart.setCartId(resultSet.getInt("CartId"));
                cart.setCustId(resultSet.getInt("CustomerId"));

                return cart;
            }
        }
    }
    public Cart createCart(int custId) throws SQLException{
        String sql = """
                INSERT INTO Cart(CustomerId) VALUES(?)""";
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ){
            statement.setInt(1,custId);
            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    Cart cart = new Cart();

                    cart.setCartId(resultSet.getInt(1));
                    cart.setCustId(custId);

                    return cart;
                }
            }
        }
        throw new SQLException("Failed to create new Cart");
    }
    public boolean checkForExistingCartItem(int cartId,int productInDisId) throws SQLException{
        String sql = """
                SELECT * FROM CartItem
                WHERE CartId = ? AND ProductInDisplayId = ?""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setInt(1,cartId);
            statement.setInt(2,productInDisId);

            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return true;
                }
            }

        }
        return false;
    }
    public boolean AddToCart(int customerId,int productDisplayId)throws SQLException{
        Cart cart = checkExistingCart(customerId);
        boolean checkExistingProduct = checkForExistingCartItem(cart.getCartId(),productDisplayId);
        if(checkExistingProduct) return false;
        String sql = """
                INSERT INTO CartItem(ProductInDisplayId,CartId,CreatedTime,Quantity)
                VALUES(?,?,?,1)""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setInt(1,productDisplayId);
            statement.setInt(2,cart.getCartId());
            statement.setTimestamp(3,Timestamp.valueOf(LocalDateTime.now()));

            int val = statement.executeUpdate();
            return val == 1;
        }
    }
    public List<CartItemsResponse> viewAllCartItem(int custId) throws SQLException{
        Cart cart = checkExistingCart(custId);
        String sql = """
                SELECT * FROM CartItem
                WHERE CartId = ?""";
        List<CartItemsResponse> cartItems = new ArrayList<>();
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setInt(1,cart.getCartId());
            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    CartItemsResponse cartItemsResponse = new CartItemsResponse();
                    int productInDisp = resultSet.getInt("ProductInDisplayId");
                    cartItemsResponse.setName(productInDisplayDAO.getProductName(productInDisplayDAO.getProductIdFromProdInDisId(productInDisp)));
                    cartItemsResponse.setProductInDisplayId(productInDisp);
                    cartItemsResponse.setCartItemId(resultSet.getInt("CartItemId"));
                    cartItems.add(cartItemsResponse);
                }
            }
        }
        return cartItems;
    }
    public void deleteCartItem(int cartItemId) throws SQLException{
        String sql = """
                DELETE FROM CartItem
                WHERE CartItemId = ?""";
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1,cartItemId);
            int deletedRows = statement.executeUpdate();
        }
    }

}
