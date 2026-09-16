package com.example.servlet;

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

public class ProductInDisplayServlet
        extends HttpServlet {
    private final ProductInDisplayService service = new ProductInDisplayService();

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void service(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws ServletException, IOException {

        if ("PATCH".equalsIgnoreCase(
                req.getMethod()
        )) {
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
            service.createProduct(
                    listing.getProductId(),
                    listing.getVendorId(),
                    listing.getPrice(),
                    listing.getQuantity()
            );
            res.setStatus(
                    HttpServletResponse.SC_CREATED
            );
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

        if(path == null){
            sendError(res,HttpServletResponse.SC_NOT_FOUND,"End Point not found");
            return;
        }

        if (path.startsWith("/getAllProducts/")) {
            getAllProd(req, res);
            return;
        }
        try {
            String vendorIdParameter =
                    req.getParameter("vendorId");
            if (vendorIdParameter == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "vendorId is required"
                );
                return;
            }
            int vendorId =
                    Integer.parseInt(
                            vendorIdParameter
                    );
            List<ProductInDisplay> listings =
                    service.getAllVendorProducts(
                            vendorId
                    );
            res.setStatus(
                    HttpServletResponse.SC_OK
            );
            objectMapper.writeValue(
                    res.getWriter(),
                    listings
            );
        } catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "vendorId must be a valid number"
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
    private void getAllProd(HttpServletRequest req,HttpServletResponse res) throws  IOException{
        try {
            String productId = req.getParameter("productId");
            if(productId == null){
                sendError(res,HttpServletResponse.SC_BAD_REQUEST,"ProductId not provided");
                return;
            }
            int prodId = Integer.parseInt(req.getParameter("productId"));

            List<ProductInDisplay> productInDisplays = service.displayAllProduct(prodId);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(),productInDisplays);
        }catch (SQLException e){
            e.printStackTrace();
            sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Database Error");
        }
    }

    private void doPatch(
            HttpServletRequest req,
            HttpServletResponse res
    ) throws IOException {
        setJsonResponse(res);
        try {
            String listingIdParameter =
                    req.getParameter(
                            "productInDisplayId"
                    );
            String vendorIdParameter =
                    req.getParameter("vendorId");
            if (listingIdParameter == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "productInDisplayId is required"
                );
                return;
            }
            if (vendorIdParameter == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "vendorId is required"
                );
                return;
            }
            int productInDisplayId =
                    Integer.parseInt(
                            listingIdParameter
                    );
            int vendorId =
                    Integer.parseInt(
                            vendorIdParameter
                    );
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
            res.setStatus(
                    HttpServletResponse.SC_OK
            );
            writeMessage(
                    res,
                    "Product updated successfully"
            );
        } catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "IDs must be valid numbers"
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
                    req.getParameter(
                            "productInDisplayId"
                    );

            String vendorIdParameter =
                    req.getParameter("vendorId");
            if (listingIdParameter == null ||
                    vendorIdParameter == null) {
                sendError(
                        res,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "productInDisplayId and vendorId are required"
                );
                return;
            }
            int productInDisplayId =
                    Integer.parseInt(
                            listingIdParameter
                    );
            int vendorId =
                    Integer.parseInt(
                            vendorIdParameter
                    );
            service.deleteVendorProduct(
                    productInDisplayId,
                    vendorId
            );
            res.setStatus(
                    HttpServletResponse.SC_OK
            );
            writeMessage(
                    res,
                    "Product deleted successfully"
            );
        } catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "IDs must be valid numbers"
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