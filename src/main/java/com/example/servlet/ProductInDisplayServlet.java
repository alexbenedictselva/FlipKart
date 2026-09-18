package com.example.servlet;

import com.example.dto.CustomProductsResponse;
import com.example.dto.ProductInDisplayUpdateRequest;
import com.example.model.ProductInDisplay;
import com.example.service.ProductInDisplayService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ProductInDisplayServlet extends HttpServlet {

    private final ProductInDisplayService service = new ProductInDisplayService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, res);
            return;
        }

        super.service(req, res);
    }

    @Override
    protected void doPost(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        setJsonResponse(res);

        try {
            ProductInDisplay listing =
                    objectMapper.readValue(
                            req.getInputStream(),
                            ProductInDisplay.class
                    );

            int vendorId = getAuthenticatedUserId(req);

            service.createProduct(
                    listing.getProductId(),
                    vendorId,
                    listing.getPrice(),
                    listing.getQuantity()
            );

            res.setStatus(HttpServletResponse.SC_CREATED);

            writeMessage(
                    res,
                    "Product posted successfully"
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

    @Override
    protected void doGet(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        setJsonResponse(res);

        String path = req.getPathInfo();

        if (path == null) {
            sendError(
                    res,
                    HttpServletResponse.SC_NOT_FOUND,
                    "End Point not found"
            );
            return;
        }

        try {
            int vendorId = getAuthenticatedUserId(req);

            List<CustomProductsResponse> listings =
                    service.getAllVendorProducts(vendorId);

            res.setStatus(HttpServletResponse.SC_OK);

            objectMapper.writeValue(
                    res.getWriter(),
                    listings
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

    private void doPatch(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws IOException {

        setJsonResponse(res);

        try {
            String listingIdParameter =
                    req.getParameter("productInDisplayId");

            if (listingIdParameter == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "productInDisplayId is required"
                );
                return;
            }

            int productInDisplayId =
                    Integer.parseInt(listingIdParameter);

            int vendorId = getAuthenticatedUserId(req);

            ProductInDisplayUpdateRequest request =
                    objectMapper.readValue(
                            req.getInputStream(),
                            ProductInDisplayUpdateRequest.class
                    );

            service.updateProduct(
                    productInDisplayId,
                    vendorId,
                    request.getPrice(),
                    request.getQuantity()
            );

            res.setStatus(HttpServletResponse.SC_OK);

            writeMessage(
                    res,
                    "Product updated successfully"
            );

        } catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "productInDisplayId must be a valid number"
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

    @Override
    protected void doDelete(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        setJsonResponse(res);

        try {
            String listingIdParameter =
                    req.getParameter("productInDisplayId");

            if (listingIdParameter == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "productInDisplayId is required"
                );
                return;
            }

            int productInDisplayId =
                    Integer.parseInt(listingIdParameter);

            int vendorId = getAuthenticatedUserId(req);

            service.deleteVendorProduct(
                    productInDisplayId,
                    vendorId
            );

            res.setStatus(HttpServletResponse.SC_OK);

            writeMessage(
                    res,
                    "Product deleted successfully"
            );

        } catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "productInDisplayId must be a valid number"
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

    private int getAuthenticatedUserId(HttpServletRequest req) {
        Object userId = req.getAttribute("userId");

        if (userId == null) {
            throw new IllegalStateException(
                    "Authenticated user ID not found"
            );
        }

        return (Integer) userId;
    }

    private void setJsonResponse(
            HttpServletResponse res
    ) {
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
    }

    private void writeMessage(
            HttpServletResponse res,
            String message
    ) throws IOException {

        objectMapper.writeValue(
                res.getWriter(),
                Map.of("message", message)
        );
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