package com.example.dao;

import com.example.model.DeliveryStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;

public class DeliveryDAO {

    public int createDelivery(
            Connection connection,
            int deliveryPartnerId
    ) throws SQLException {

        String sql = """
                INSERT INTO Delivery(
                    DeliveryPartnerId,
                    DeliveryStatus,
                    ExpectedDate
                )
                VALUES(?,?,?)
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            statement.setInt(1, deliveryPartnerId);

            statement.setString(
                    2,
                    DeliveryStatus.PENDING.name()
            );

            statement.setDate(
                    3,
                    Date.valueOf(LocalDate.now().plusDays(3))
            );

            statement.executeUpdate();

            try (ResultSet resultSet =
                         statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to create delivery");
    }
}