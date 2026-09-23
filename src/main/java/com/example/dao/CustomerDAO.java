package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {

    // =========================================================
    // CREATE CUSTOMER
    // =========================================================

    public int create(Customer customer) throws SQLException {

        String sql = """
                INSERT INTO UserAccount
                (Name, PhoneNo, Password)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setString(1, customer.getName());
            statement.setString(2, customer.getPhNo());
            statement.setString(3, customer.getPassword());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    int userId = resultSet.getInt(1);

                    // Create Customer role record
                    createCustomerRole(connection, userId);

                    return userId;
                }
            }
        }

        throw new SQLException("Failed to create customer");
    }


    public void createCustomerRole(
            Connection connection,
            int userId
    ) throws SQLException {

        String sql = """
                INSERT INTO users (UserId)
                VALUES (?)
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }


    // =========================================================
    // FIND ACCOUNT BY PHONE + ROLE
    // =========================================================

    public Customer findByPhoneNumber(
            String phoneNumber,
            String role
    ) throws SQLException {

        return switch (role.toUpperCase()) {

            case "CUSTOMER" ->
                    findCustomerByPhone(phoneNumber);

            case "VENDOR" ->
                    findVendorByPhone(phoneNumber);

            case "DELIVERYPARTNER",
                 "DELIVERY_PARTNER",
                 "DELIVERY_PERSON" ->
                    findDeliveryPartnerByPhone(phoneNumber);

            default ->
                    throw new IllegalArgumentException(
                            "Invalid role: " + role
                    );
        };
    }


    // =========================================================
    // CUSTOMER
    // =========================================================

    private Customer findCustomerByPhone(
            String phoneNumber
    ) throws SQLException {

        String sql = """
                SELECT
                    ua.UserId,
                    ua.Name,
                    ua.PhoneNo,
                    ua.Password
                FROM UserAccount ua
                JOIN users c
                    ON c.UserId = ua.UserId
                WHERE ua.PhoneNo = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, phoneNumber);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return createCustomerFromResultSet(
                        resultSet,
                        "CUSTOMER"
                );
            }
        }
    }


    // =========================================================
    // VENDOR
    // =========================================================

    private Customer findVendorByPhone(
            String phoneNumber
    ) throws SQLException {

        String sql = """
                SELECT
                    ua.UserId,
                    ua.Name,
                    ua.PhoneNo,
                    ua.Password
                FROM UserAccount ua
                JOIN Vendor v
                    ON v.VendorId = ua.UserId
                WHERE ua.PhoneNo = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, phoneNumber);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return createCustomerFromResultSet(
                        resultSet,
                        "VENDOR"
                );
            }
        }
    }


    // =========================================================
    // DELIVERY PARTNER
    // =========================================================

    private Customer findDeliveryPartnerByPhone(
            String phoneNumber
    ) throws SQLException {

        String sql = """
                SELECT
                    ua.UserId,
                    ua.Name,
                    ua.PhoneNo,
                    ua.Password
                FROM UserAccount ua
                JOIN DeliveryPartner dp
                    ON dp.UserId = ua.UserId
                WHERE ua.PhoneNo = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, phoneNumber);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return createCustomerFromResultSet(
                        resultSet,
                        "DELIVERYPARTNER"
                );
            }
        }
    }


    // =========================================================
    // CREATE CUSTOMER OBJECT
    // =========================================================

    private Customer createCustomerFromResultSet(
            ResultSet resultSet,
            String role
    ) throws SQLException {

        Customer customer = new Customer();

        customer.setCustId(
                resultSet.getInt("UserId")
        );

        customer.setName(
                resultSet.getString("Name")
        );

        customer.setPhNo(
                resultSet.getString("PhoneNo")
        );

        customer.setPassword(
                resultSet.getString("Password")
        );

        customer.setRole(role);

        return customer;
    }


    // =========================================================
    // CHECK WHETHER PHONE EXISTS FOR A PARTICULAR ROLE
    // =========================================================

    public boolean existsByPhoneNumber(
            String phoneNumber,
            String role
    ) throws SQLException {

        return findByPhoneNumber(phoneNumber, role) != null;
    }


    // =========================================================
    // GET CUSTOMER NAME
    // =========================================================

    public String getCustomerName(
            int custId
    ) throws SQLException {

        String sql = """
                SELECT Name
                FROM UserAccount
                WHERE UserId = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, custId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getString("Name");
                }
            }
        }

        return "X";
    }
}