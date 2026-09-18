package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.dto.CustomProductsResponse;
import com.example.model.ProductInDisplay;

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

    public List<CustomProductsResponse> findByVendorId(int vendorId)
            throws SQLException {

        String sql = """
                SELECT *
                FROM ProductInDisplay
                WHERE VendorId = ?
                """;

        List<CustomProductsResponse> listings = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, vendorId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    CustomProductsResponse listing = new CustomProductsResponse();

                    String productName = getProductName(resultSet.getInt("ProductId"));
                    listing.setProductInDisplayId(resultSet.getInt("ProductInDisplayId"));
                    listing.setProductName(productName);
                    listing.setVendorName(getVendorName(vendorId));
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

    public List<CustomProductsResponse> getAllProduct(int productId) throws SQLException{
        String sql = """
                SELECT * FROM ProductInDisplay 
                WHERE ProductId = ?""";
        List<CustomProductsResponse> listings = new ArrayList<>();
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setInt(1,productId);
            try(ResultSet resultSet = statement.executeQuery()){
                while(resultSet.next()){
                    CustomProductsResponse customProductsResponse = new CustomProductsResponse();
                    customProductsResponse.setProductInDisplayId(resultSet.getInt("ProductInDisplayId"));
                    customProductsResponse.setPrice(resultSet.getInt("Price"));
                    customProductsResponse.setQuantity(resultSet.getInt("Quantity"));
                    String name = getProductName(resultSet.getInt("ProductId"));
                    customProductsResponse.setProductName(name);
                    customProductsResponse.setVendorName(getVendorName(resultSet.getInt("VendorId")));
                    listings.add(customProductsResponse);
                }
            }
        }
        return listings;
    }

    public String getVendorName(int vendorid) throws  SQLException{
        String sql = """
                SELECT Name FROM Vendor
                WHERE VendorId = ?""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1,vendorid);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getString("Name");
                }
            }
            return "Nil";
        }
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

    public String getProductName(int productId) throws  SQLException{
        String sql = """
                SELECT Name FROM Product
                WHERE ProductId = ?""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1,productId);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getString("Name");
                }
            }
            return "Nil";
        }
    }

    public String getVendorNameFromProductInDisplayId(int productInDisId) throws SQLException{
        String sql = """
                SELECT Name FROM ProductInDisplay as p LEFT JOIN Vendor as v
                ON p.VendorId = v.VendorId
                WHERE p.ProductInDisplayId = ?""";
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1,productInDisId);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getString("Name");
                }
            }
        }
        return "X";

    }
    public int getProductIdFromProdInDisId(int productInDisId) throws  SQLException{
        String sql = """
                SELECT ProductId FROM ProductInDisplay
                WHERE ProductInDisplayId = ?""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1,productInDisId);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getInt("ProductId");
                }
            }
            return 0;
        }
    }

    public ProductInDisplay getProduct(
            Connection connection,
            int productInDisplayId
    ) throws SQLException {

        String sql = """
            SELECT ProductInDisplayId,
                   ProductId,
                   VendorId,
                   Quantity,
                   Price
            FROM ProductInDisplay
            WHERE ProductInDisplayId = ?
            FOR UPDATE
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, productInDisplayId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                ProductInDisplay product =
                        new ProductInDisplay();

                product.setProductInDisplayId(
                        resultSet.getInt("ProductInDisplayId")
                );

                product.setProductId(
                        resultSet.getInt("ProductId")
                );

                product.setVendorId(
                        resultSet.getInt("VendorId")
                );

                product.setQuantity(
                        resultSet.getInt("Quantity")
                );

                product.setPrice(
                        resultSet.getDouble("Price")
                );

                return product;
            }
        }
    }

}