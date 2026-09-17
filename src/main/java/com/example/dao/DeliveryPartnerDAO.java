package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeliveryPartnerDAO {

    public int findPartner(Connection connection) throws SQLException {

        String sql = """
                SELECT DeliveryPartnerId
                FROM DeliveryPartner
                ORDER BY NumberOfOrdersDelivered ASC
                LIMIT 1
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return resultSet.getInt("DeliveryPartnerId");
            }
        }

        return 0;
    }


}