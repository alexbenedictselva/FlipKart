package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.ProductInDisplay;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductInDisplayDAO {

    public void create(ProductInDisplay listing) throws SQLException {

        String sql = """
                INSERT INTO ProductInDisplay
                (ProductId, VendorId, Quantity, Price)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, listing.getProductId());
            statement.setInt(2, listing.getVendorId());
            statement.setInt(3, listing.getQuantity());
            statement.setDouble(4, listing.getPrice());

            statement.executeUpdate();
        }
    }

    public List<ProductInDisplay> findByVendorId(int vendorId)
            throws SQLException {

        String sql = """
                SELECT
                    ProductInDisplayId,
                    ProductId,
                    VendorId,
                    Quantity,
                    Price
                FROM ProductInDisplay
                WHERE VendorId = ?
                """;

        List<ProductInDisplay> listings = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, vendorId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    ProductInDisplay listing = new ProductInDisplay();

                    listing.setProductInDisplayId(
                            resultSet.getInt("ProductInDisplayId")
                    );
                    listing.setProductId(
                            resultSet.getInt("ProductId")
                    );
                    listing.setVendorId(
                            resultSet.getInt("VendorId")
                    );
                    listing.setQuantity(
                            resultSet.getInt("Quantity")
                    );
                    listing.setPrice(
                            resultSet.getDouble("Price")
                    );

                    listings.add(listing);
                }
            }
        }

        return listings;
    }

    public List<ProductInDisplay> getAllProduct(int productId) throws SQLException{
        String sql = """
                SELECT * FROM ProductInDisplay 
                WHERE ProductId = ?""";
        List<ProductInDisplay> listings = new ArrayList<>();
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setInt(1,productId);
            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    ProductInDisplay productInDisplay = new ProductInDisplay();
                    productInDisplay.setProductId(resultSet.getInt("ProductId"));
                    productInDisplay.setVendorId(resultSet.getInt("VendorId"));
                    productInDisplay.setPrice(resultSet.getDouble("Price"));
                    productInDisplay.setQuantity(resultSet.getInt("Quantity"));
                    productInDisplay.setProductInDisplayId(resultSet.getInt("ProductInDisplayId"));
                    listings.add(productInDisplay);
                }
            }
        }
        return listings;
    }

    public boolean update(
            int productInDisplayId,
            int vendorId,
            Double price,
            Integer quantity
    ) throws SQLException {

        StringBuilder sql =
                new StringBuilder("UPDATE ProductInDisplay SET ");

        List<Object> parameters = new ArrayList<>();

        if (price != null) {
            sql.append("Price = ?");
            parameters.add(price);
        }

        if (quantity != null) {
            if (!parameters.isEmpty()) {
                sql.append(", ");
            }

            sql.append("Quantity = ?");
            parameters.add(quantity);
        }

        sql.append("""
                 WHERE ProductInDisplayId = ?
                 AND VendorId = ?
                """);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql.toString())
        ) {
            int parameterIndex = 1;

            for (Object parameter : parameters) {

                if (parameter instanceof Double) {
                    statement.setDouble(
                            parameterIndex++,
                            (Double) parameter
                    );
                } else if (parameter instanceof Integer) {
                    statement.setInt(
                            parameterIndex++,
                            (Integer) parameter
                    );
                }
            }

            statement.setInt(parameterIndex++, productInDisplayId);
            statement.setInt(parameterIndex, vendorId);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        }
    }

    public boolean delete(int productInDisplayId, int vendorId)
            throws SQLException {

        String sql = """
                DELETE FROM ProductInDisplay
                WHERE ProductInDisplayId = ?
                AND VendorId = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, productInDisplayId);
            statement.setInt(2, vendorId);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        }
    }

    public ProductInDisplay getProduct(int prodInDisId) throws SQLException{
        String sql = """
                SELECT * FROM ProductInDisplay
                WHERE ProductInDisplayId = ?""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setInt(1,prodInDisId);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    ProductInDisplay productInDisplay = new ProductInDisplay();
                    productInDisplay.setProductInDisplayId(resultSet.getInt("ProductInDisplayId"));
                    productInDisplay.setVendorId(resultSet.getInt("VendorId"));
                    productInDisplay.setProductId(resultSet.getInt("ProductId"));
                    productInDisplay.setQuantity(resultSet.getInt("Quantity"));
                    productInDisplay.setPrice(resultSet.getDouble("Price"));
                    return productInDisplay;
                }
                return null;
            }
        }
    }
}