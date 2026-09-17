package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.model.Delivery;
import com.example.model.DeliveryStatus;

import javax.xml.crypto.Data;
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
    public Delivery getDelivery(int deliveryId) throws SQLException{
        String sql = """
                SELECT * FROM Delivery
                WHERE DeliveryId = ?""";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ){
            statement.setInt(1,deliveryId);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    Delivery delivery = new Delivery();
                    delivery.setDeliveryId(resultSet.getInt("DeliveryId"));
                    delivery.setDeliveryPartnerId(resultSet.getInt("DeliveryPartnerId"));
                    delivery.setDeliveryStatus(resultSet.getString("DeliveryStatus"));
                    delivery.setExpectedDate(resultSet.getTimestamp("ExpectedDate").toLocalDateTime().toLocalDate());

                    return delivery;
                }
            }
        }
        return null;
    }
}