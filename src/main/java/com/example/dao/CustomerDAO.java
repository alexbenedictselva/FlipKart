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
                INSERT INTO Customer
                (Name, Address, PhNo, Password)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getAddress());
            statement.setString(3, customer.getPhNo());
            statement.setString(4, customer.getPassword());

            statement.executeUpdate();
        }
    }

    public Customer findByPhoneNumber(String phoneNumber)
            throws SQLException {

        String sql = """
                SELECT CustId, Name, Address, PhNo, Password
                FROM Customer
                WHERE PhNo = ?
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

                customer.setCustId(resultSet.getInt("CustId"));
                customer.setName(resultSet.getString("Name"));
                customer.setAddress(resultSet.getString("Address"));
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
                FROM Customer
                WHERE PhNo = ?
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
}