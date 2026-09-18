package com.example.servlet;

//import com.example.dto.DeliveryStatusRequest;DeliveryStatusRequest
import com.example.dto.DeliveryPartnerOrderResponse;
import com.example.service.DeliveryService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DeliveryServlet extends HttpServlet {

    private final DeliveryService deliveryService = new DeliveryService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPut(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        setJsonResponse(res);

        try {
            Object userId = req.getAttribute("userId");

            if (userId == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "User authentication required"
                );
                return;
            }

            int deliveryPartnerId = (Integer) userId;
            JsonNode body = objectMapper.readTree(req.getInputStream());

            int deliveryId = body.get("deliveryId").asInt();
            String status = body.get("status").asText();

            deliveryService.changeDeliveryStatus(
                    deliveryId,
                    deliveryPartnerId,
                    status
            );

            res.setStatus(HttpServletResponse.SC_OK);

            objectMapper.writeValue(
                    res.getWriter(),
                    Map.of("message", "Delivery status updated successfully")
            );

        } catch (IllegalArgumentException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    // DeliveryServlet

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        setJsonResponse(res);

        try {
            Object userId = req.getAttribute("userId");

            if (userId == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "User authentication required"
                );
                return;
            }

            int deliveryPartnerId = (Integer) userId;

            List<DeliveryPartnerOrderResponse> deliveries =
                    deliveryService.getCurrentDeliveries(deliveryPartnerId);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(), deliveries);

        } catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "deliveryPartnerId must be a valid number"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(
                    res,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database error"
            );
        }
    }

    private void setJsonResponse(HttpServletResponse res) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
    }

    private void sendError(
            HttpServletResponse res,
            int status,
            String message
    ) throws IOException {

        res.setStatus(status);
        objectMapper.writeValue(
                res.getWriter(),
                Map.of("error", message)
        );
    }
}