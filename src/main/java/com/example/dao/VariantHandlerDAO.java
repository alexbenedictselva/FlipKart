package com.example.dao;

import com.example.database.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class VariantHandlerDAO {

    public int getAttributeId(String str, Connection connection) throws SQLException {

        String sql = """
                SELECT AttributeId FROM `Attribute` WHERE LOWER(Name) = ?""";

        try(
                PreparedStatement statement = connection.prepareStatement(sql);
                ){
            statement.setString(1,str.trim().toLowerCase());
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getInt(1);
                }
            }
        }
        return postAttribute(str,connection);
    }
    public int postAttribute(String str, Connection connection) throws SQLException{
        String sql = """
                INSERT INTO Attribute(Name)
                VALUES(?)
                """;

        try(
                PreparedStatement statement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
        ){
            statement.setString(1,str);

            statement.executeUpdate();
            try(ResultSet resultSet = statement.getGeneratedKeys()){
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }

    public int getAttributeValueId(int attributeId,String value, Connection connection) throws SQLException{
//        int attributeId = getAttributeId(key,connection);

        String sql = """
                SELECT * FROM AttributeValue
                WHERE AttributeId = ? AND LOWER(Value) = ?""";

        try(
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setInt(1,attributeId);
            statement.setString(2,value);
            try(ResultSet resultSet = statement.executeQuery()){
                if(resultSet.next()){
                    return resultSet.getInt(1);
                }
            }
        }
        return postAttributeValue(attributeId,value,connection);
    }
    public int postAttributeValue(int attributeId,String value, Connection connection) throws SQLException{
        String sql = """
                INSERT INTO AttributeValue(AttributeId,Value)
                VALUES(?,?)
                """;

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt(1,attributeId);
            preparedStatement.setString(2,value);
            preparedStatement.executeUpdate();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }

    public int postAttributeWithValue(String attribute,String value,Connection connection) throws  SQLException{
//        Connection connection = DatabaseConnection.getConnection();
        int attributeId = getAttributeId(attribute,connection);
        int attributeValueId = getAttributeValueId(attributeId,value,connection);
        return attributeValueId;
    }

    public int postVariant(int productId,Connection connection) throws SQLException{
        String sql = """
                INSERT INTO ProductVariantt(ProductId,SKU)
                VALUES(?,"XX")""";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt(1,productId);
            preparedStatement.executeUpdate();
            try(ResultSet resultSet = preparedStatement.getGeneratedKeys()){
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return -1;
    }

    public void postVariantAttribute(int variantId,int attributeValueId,Connection connection) throws SQLException{
        String sql = """
                INSERT INTO VariantAttribute(VariantId,AttributeValueId)
                VALUES(?,?)""";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ){
            preparedStatement.setInt(1,variantId);
            preparedStatement.setInt(2,attributeValueId);
            preparedStatement.executeUpdate();

        }
    }


    public int postProductVariant(Map<String, String> mpp, int productId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {

            try {
                connection.setAutoCommit(false);
                int variantId = postVariant(productId, connection);
                for (Map.Entry<String, String> entry : mpp.entrySet()) {
                    String attribute = entry.getKey().toLowerCase();
                    String value = entry.getValue().toLowerCase();
                    int attributeValueId =
                            postAttributeWithValue(
                                    attribute,
                                    value,
                                    connection
                            );
                    postVariantAttribute(
                            variantId,
                            attributeValueId,
                            connection
                    );
                }
                connection.commit();
                return variantId;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }
//    public int checkIfVariantExist(Map<String,String> mpp,int productId,Connection connection) throws SQLException{
//        Connection connection = DatabaseConnection.getConnection();
//        List<Integer> list = getAllVariantIdsFromProductId(productId,connection);
//        for(int i=0;i<list.size();i++){
//            Map<String,String> m = getAllAttributeWithVariantId(list.get(i));
//            if(m.equals(mpp)){
//                return list.get(i);
//            }
//        }
//
//        return -1;
//    }
//    public List<Integer> getAllVariantIdsFromProductId(int productId,Connection connection) throws SQLException{
//        String sql = """
//                SELECT DISTINCT va.variantId FROM ProductVariantt as v JOIN VariantAttribute as va
//                ON v.VariantId = va.VariantId WHERE v.productId = ?""";
//
//
//        try(
//                PreparedStatement preparedStatement = connection.prepareStatement(sql);
//        ){
//            preparedStatement.setInt(1,productId);
//            try(ResultSet resultSet = preparedStatement.executeQuery()){
//                List<Integer> list = new ArrayList<>();
//                while (resultSet.next()){
//                    list.add(resultSet.getInt(1));
//                }
//                return list;
//            }
//        }
//    }
//    public Map<String,String> getAllAttributeWithVariantId(int id) throws SQLException{
//        String sql = """
//                SELECT a.Name, av.Value FROM VariantAttribute as va JOIN AttributeValue av
//                ON va.AttributeValueId = av.AttributeValueId JOIN Attribute a
//                ON av.AttributeId = a.AttributeId WHERE va.VariantId = ?""";
//
//        try(
//                Connection connection = DatabaseConnection.getConnection();
//                PreparedStatement preparedStatement = connection.prepareStatement(sql);){
//            preparedStatement.setInt(1,id);
//            try(ResultSet resultSet = preparedStatement.executeQuery()){
//                Map<String,String> mpp = new HashMap<>();
//                while(resultSet.next()){
//                    mpp.put(resultSet.getString(1).toLowerCase(),resultSet.getString(2).toLowerCase());
//                }
//                return mpp;
//            }
//        }
//    }

    public Map<String,String> getAllVariantDetails(int variantId,Connection connection) throws  SQLException{
        String sql = """
                SELECT a.Name, av.Value FROM VariantAttribute as va LEFT JOIN AttributeValue av
                ON va.AttributeValueId = av.AttributeValueId LEFT JOIN Attribute a
                ON av.AttributeId = a.AttributeId WHERE va.VariantId = ?""";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ){
            preparedStatement.setInt(1,variantId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                Map<String,String> mp = new HashMap<>();
                while(resultSet.next()){
                    mp.put(resultSet.getString(1),resultSet.getString(2));
                }
                return mp;
            }
        }
    }

}
