package com.example.dao;

import com.example.database.DatabaseConnection;
import com.example.dto.CustomerOrderItemViewing;
import com.example.dto.CustomerOrderViewingResponse;
import com.example.dto.OrderItemsRequest;
import com.example.dto.VendorOrdersResponse;
import com.example.model.DeliveryStatus;
import com.example.model.Order;
import com.example.model.OrderItems;
import com.example.model.ProductInDisplay;
import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.awt.image.DataBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDAO {

    private final ProductInDisplayDAO productInDisplayDAO =
            new ProductInDisplayDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private final DeliveryPartnerDAO deliveryPartnerDAO =
            new DeliveryPartnerDAO();

    private final DeliveryDAO deliveryDAO =
            new DeliveryDAO();

    public boolean createCustomerOrder(
            List<OrderItemsRequest> orderItems,
            int custId
    ) throws SQLException {

        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must contain at least one item"
            );
        }

        String createOrderSql = """
                INSERT INTO `Order`(CustId, TotalAmount)
                VALUES(?, ?)
                """;

        String createOrderItemSql = """
                INSERT INTO OrderItems(
                    OrderId,
                    ProductInDisplayId,
                    DeliveryId,
                    Price,
                    Quantity
                )
                VALUES(?,?,?,?,?)
                """;

        String updateStockSql = """
                UPDATE ProductInDisplay
                SET Quantity = Quantity - ?
                WHERE ProductInDisplayId = ?
                """;

        String updateOrderTotalSql = """
                UPDATE `Order`
                SET TotalAmount = ?
                WHERE OrderId = ?
                """;

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();

            connection.setAutoCommit(false);

            int orderId;

            // 1. Create Order
            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    createOrderSql,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {
                statement.setInt(1, custId);
                statement.setDouble(2, 0);

                statement.executeUpdate();

                try (ResultSet resultSet =
                             statement.getGeneratedKeys()) {

                    if (!resultSet.next()) {
                        throw new SQLException(
                                "Failed to create order"
                        );
                    }

                    orderId = resultSet.getInt(1);
                }
            }

            /*
             * VendorId -> DeliveryId
             *
             * This ensures that all products
             * from the same vendor use the
             * same delivery.
             */
            Map<Integer, Integer> vendorDeliveryMap =
                    new HashMap<>();

            double totalAmount = 0;

            // 2. Process every order item
            try (
                    PreparedStatement orderItemStatement =
                            connection.prepareStatement(
                                    createOrderItemSql
                            );

                    PreparedStatement stockStatement =
                            connection.prepareStatement(
                                    updateStockSql
                            )
            ) {

                for (OrderItemsRequest item : orderItems) {

                    if (item.getQuantity() <= 0) {
                        throw new IllegalArgumentException(
                                "Quantity must be greater than 0"
                        );
                    }

                    /*
                     * Get product listing using
                     * the same transaction connection.
                     */
                    ProductInDisplay product =
                            productInDisplayDAO.getProduct(
                                    connection,
                                    item.getProductInDisplayId()
                            );

                    if (product == null) {
                        throw new IllegalArgumentException(
                                "Product listing not found"
                        );
                    }

                    /*
                     * Make sure the requested vendor
                     * actually owns this listing.
                     */
                    if (product.getVendorId()
                            != item.getVendorId()) {

                        throw new IllegalArgumentException(
                                "Product does not belong to the given vendor"
                        );
                    }

                    /*
                     * Check available stock.
                     */
                    if (product.getQuantity()
                            < item.getQuantity()) {

                        throw new IllegalArgumentException(
                                "Insufficient stock for product "
                                        + item.getProductInDisplayId()
                        );
                    }

                    int deliveryId;

                    /*
                     * Check whether we already created
                     * a delivery for this vendor.
                     */
                    if (vendorDeliveryMap.containsKey(
                            item.getVendorId())) {

                        deliveryId =
                                vendorDeliveryMap.get(
                                        item.getVendorId()
                                );

                    } else {

                        // Find an existing partner
                        int deliveryPartnerId =
                                deliveryPartnerDAO.findPartner(
                                        connection
                                );

                        if (deliveryPartnerId == 0) {
                            throw new SQLException(
                                    "No delivery partner available"
                            );
                        }

                        // Create delivery
                        deliveryId =
                                deliveryDAO.createDelivery(
                                        connection,
                                        deliveryPartnerId
                                );

                        // Remember delivery for this vendor
                        vendorDeliveryMap.put(
                                item.getVendorId(),
                                deliveryId
                        );
                    }

                    /*
                     * Save the current price in OrderItem.
                     *
                     * This is important because the product
                     * price may change later.
                     */
                    double itemTotal =
                            product.getPrice()
                                    * item.getQuantity();

                    totalAmount += itemTotal;

                    orderItemStatement.setInt(
                            1,
                            orderId
                    );

                    orderItemStatement.setInt(
                            2,
                            item.getProductInDisplayId()
                    );

                    orderItemStatement.setInt(
                            3,
                            deliveryId
                    );

                    orderItemStatement.setDouble(
                            4,
                            product.getPrice()
                    );

                    orderItemStatement.setInt(
                            5,
                            item.getQuantity()
                    );

                    orderItemStatement.executeUpdate();

                    /*
                     * Reduce stock.
                     */
                    stockStatement.setInt(
                            1,
                            item.getQuantity()
                    );

                    stockStatement.setInt(
                            2,
                            item.getProductInDisplayId()
                    );

                    stockStatement.executeUpdate();
                }
            }

            // 3. Update final order total
            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    updateOrderTotalSql
                            )
            ) {
                statement.setDouble(1, totalAmount);
                statement.setInt(2, orderId);

                statement.executeUpdate();
            }

            // 4. Everything succeeded
            connection.commit();

            return true;

        } catch (Exception e) {

            if (connection != null) {
                connection.rollback();
            }

            throw e;

        } finally {

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public List<VendorOrdersResponse> getAllOrderOfVendor(int vendorId) throws SQLException{
        String sql = """
                SELECT o.*
                FROM OrderItems AS o
                JOIN ProductInDisplay AS p
                ON o.ProductInDisplayId = p.ProductInDisplayId
                WHERE p.VendorId = ?""";

        List<VendorOrdersResponse> vendorOrder = new ArrayList<>();

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ){
            preparedStatement.setInt(1,vendorId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    VendorOrdersResponse vendorOrdersResponse = new VendorOrdersResponse();
                    vendorOrdersResponse.setPrice(resultSet.getDouble("Price"));
                    vendorOrdersResponse.setQuantity(resultSet.getInt("Quantity"));
                    ProductInDisplay productInDisplay = productInDisplayDAO.getProduct(resultSet.getInt("ProductInDisplayId"));
                    vendorOrdersResponse.setProductName(productInDisplayDAO.getProductName(productInDisplay.getProductId()));
                    Order order = getOrder(resultSet.getInt("OrderId"));
                    vendorOrdersResponse.setOrderAt(order.getPurchasedDate());
                    vendorOrdersResponse.setCustomerName(customerDAO.getCustomerName(order.getCustId()));

                    vendorOrder.add(vendorOrdersResponse);
                }
            }
        }
        return vendorOrder;
    }


    public List<CustomerOrderViewingResponse> getAllCustomerOrders(int custId) throws SQLException{
        String sql = """
                SELECT * FROM `Order`
                WHERE CustId = ?""";

        List<CustomerOrderViewingResponse> customerOrderViewingResponses = new ArrayList<>();
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setInt(1,custId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    CustomerOrderViewingResponse customerOrderViewingResponse = new CustomerOrderViewingResponse();
                    List<CustomerOrderItemViewing> customerOrderItemViewing = GetAllOrderItems(resultSet.getInt("OrderId"));
                    customerOrderViewingResponse.setCustomerOrderItemViewings(customerOrderItemViewing);
                    customerOrderViewingResponse.setOrderId(resultSet.getInt("OrderId"));
                    customerOrderViewingResponse.setPurchasedAt(resultSet.getTimestamp("PurchasedAt").toString());
                    customerOrderViewingResponses.add(customerOrderViewingResponse);
                }
            }
        }
        return customerOrderViewingResponses;

    }
    public List<CustomerOrderItemViewing>  GetAllOrderItems(int orderId) throws SQLException{
        String sql = """
                SELECT * FROM OrderItems
                WHERE OrderId = ?""";
        List<CustomerOrderItemViewing> customerOrderItemViewings = new ArrayList<>();
        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ){
            preparedStatement.setInt(1,orderId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    CustomerOrderItemViewing customerOrderItemViewing = new CustomerOrderItemViewing();
                    customerOrderItemViewing.setPrice(resultSet.getDouble("Price"));
                    String productName = productInDisplayDAO.getProductName(productInDisplayDAO.getProductIdFromProdInDisId(resultSet.getInt("ProductInDisplayId")));
                    customerOrderItemViewing.setProductName(productName);
                    customerOrderItemViewing.setQuantity(resultSet.getInt("Quantity"));
//                    DeliveryStatus deliveryStatus = ;
                    customerOrderItemViewing.setStatus(DeliveryStatus.valueOf(deliveryDAO.getDelivery(resultSet.getInt("DeliveryId")).getDeliveryStatus()));
                    customerOrderItemViewing.setVendorName(productInDisplayDAO.getVendorNameFromProductInDisplayId(resultSet.getInt("ProductInDisplayId"))); //have to work
                    customerOrderItemViewings.add(customerOrderItemViewing);
                }
            }
        }
        return customerOrderItemViewings;
    }
    public Order getOrder(int orderId) throws SQLException{
        String sql = """
                SELECT * FROM `Order`
                WHERE OrderId = ?""";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ){
            preparedStatement.setInt(1,orderId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    Order order = new Order();
                    order.setCustId(resultSet.getInt("CustId"));
                    order.setTotalAmount(resultSet.getDouble("TotalAmount"));
                    order.setPurchasedDate(resultSet.getTimestamp("PurchasedAt").toString());
                    order.setOrderId(resultSet.getInt("OrderId"));
                    return order;
                }
            }
        }
        return null;
    }


}