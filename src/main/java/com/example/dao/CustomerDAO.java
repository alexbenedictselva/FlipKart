package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    public void create(Customer customer) throws SQLException {

        String sql = """
                INSERT INTO Users
                (Name,PhNo, Password, Role)
                VALUES (?,?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getPhNo());
            statement.setString(3, customer.getPassword());
            statement.setString(4, "CUSTOMER");

            statement.executeUpdate();
        }
    }

    public Customer findByPhoneNumber(String phoneNumber)
            throws SQLException {

        String sql = """
                SELECT Name, UserId, PhNo, Password
                FROM Users
                WHERE PhNo = ? AND Role = 'CUSTOMER'
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, phoneNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                Customer customer = new Customer();
                customer.setName(resultSet.getString("Name"));
                customer.setCustId(resultSet.getInt("UserId"));
                customer.setPhNo(resultSet.getString("PhNo"));
                customer.setPassword(resultSet.getString("Password"));

                return customer;
            }
        }
    }

    public boolean existsByPhoneNumber(String phoneNumber)
            throws SQLException {

        String sql = """
                SELECT 1
                FROM Users
                WHERE PhNo = ? AND Role = 'CUSTOMER'
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, phoneNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public String getCustomerName(int custId) throws SQLException{
        String sql = """
                SELECT Name FROM Users
                WHERE UserId = ?""";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1,custId);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getString("Name");
                }
            }
        }
        return "X";
    }
}