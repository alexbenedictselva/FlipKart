package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.dto.DeliveryPartnerOrderResponse;
import com.example.dto.ProductName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeliveryPartnerDAO {

    public int findPartner(Connection connection) throws SQLException {

        String sql = """
        SELECT dp.UserId
        FROM deliveryPartner AS dp
        JOIN userAccount AS ua
            ON ua.UserId = dp.UserId
        ORDER BY COALESCE(dp.NumberOfOrdersDelivered, 0) ASC,
                 dp.UserId ASC
        LIMIT 1
        """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt("UserId");
            }
        }

        return 0;
    }
    public void changeDeliveryStatus(int deliveryId,int deliveryPersonId,String status) throws  SQLException{
        String sql = """
            UPDATE Delivery
            SET DeliveryStatus = ?
            WHERE DeliveryId = ? AND DeliveryPartnerId = ?
            """;
        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
        ) {
            statement.setString(1, status);
            statement.setInt(2, deliveryId);
            statement.setInt(3,deliveryPersonId);
            int rowsAffected = statement.executeUpdate();
        }
    }

    // DeliveryDAO

    public List<DeliveryPartnerOrderResponse> getCurrentDeliveries(int deliveryPartnerId) throws SQLException {

        String sql = """
            
                SELECT
                            d.DeliveryId,
                            p.Name AS ProductName,
                            oi.ProductInDisplayId,
                            c.Name AS CustomerName,
                            c.PhNo AS CustomerPhNo,
                            d.DeliveryStatus
                        FROM Delivery d
                        JOIN OrderItems oi
                            ON d.DeliveryId = oi.DeliveryId
                        JOIN ProductInDisplay pid
                            ON oi.ProductInDisplayId = pid.ProductInDisplayId
                        JOIN Product p
                            ON pid.ProductId = p.ProductId
                        JOIN `Order` o
                            ON oi.OrderId = o.OrderId
                        JOIN users AS c
                            ON o.CustId = c.UserId
                        WHERE d.DeliveryPartnerId = ?
                        AND d.DeliveryStatus IN ('PENDING', 'ASSIGNED', 'PICKED_UP', 'OUT_FOR_DELIVERY')
                        ORDER BY d.DeliveryId
            """;



        List<DeliveryPartnerOrderResponse> responses = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, deliveryPartnerId);

            try (ResultSet resultSet = statement.executeQuery()) {

                Map<Integer, DeliveryPartnerOrderResponse> deliveryMap = new LinkedHashMap<>();

                while (resultSet.next()) {

                    int deliveryId = resultSet.getInt("DeliveryId");

                    DeliveryPartnerOrderResponse response = deliveryMap.get(deliveryId);

                    if (response == null) {
                        response = new DeliveryPartnerOrderResponse();
                        response.setDeliveryId(deliveryId);
                        response.setProductNames(new ArrayList<>());
                        response.setName(resultSet.getString("CustomerName"));
                        response.setPhNo(resultSet.getString("CustomerPhNo"));
                        response.setStatus(resultSet.getString("DeliveryStatus"));

                        deliveryMap.put(deliveryId, response);
                    }

                    ProductName productName = new ProductName();
                    productName.setProductInDisplayId(
                            resultSet.getInt("ProductInDisplayId")
                    );
                    productName.setProductName(
                            resultSet.getString("ProductName")
                    );

                    response.getProductNames().add(productName);
                }

                responses.addAll(deliveryMap.values());
            }
        }

        return responses;
    }

}