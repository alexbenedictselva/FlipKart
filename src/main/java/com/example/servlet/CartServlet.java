package com.example.servlet;

import com.example.model.ProductInDisplay;
import com.example.service.CartService;
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

public class CartServlet extends HttpServlet {
    private final CartService cartService = new CartService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        setJsonResponse(res);

        try{
            String custIdStr = req.getParameter("customerId");
            if(custIdStr == null){
                sendError(res,HttpServletResponse.SC_BAD_REQUEST,"Provide Customer Id");
                return;
            }
            int custId = Integer.parseInt(custIdStr);
            List<ProductInDisplay> productInDisplayList = cartService.getAllCartProducts(custId);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(),productInDisplayList);
        }catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Customer ID must be a valid number"
            );
        }catch (SQLException e){
            e.printStackTrace();
            sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException{
        setJsonResponse(res);
        JsonNode body = objectMapper.readTree(req.getInputStream());
        int custId = body.get("customerId").asInt();
        int productInDisId = body.get("productInDisplayId").asInt();

        try{
            cartService.AddProduct(custId,productInDisId);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(),Map.of("message","Added Successfully!"));
        }catch (NumberFormatException e) {
            sendError(
                    res,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Customer ID must be a valid number"
            );
        }catch (SQLException e){
            e.printStackTrace();
            sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Database error");
        }
    }

    private void setJsonResponse(HttpServletResponse res){
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
    }

    private void sendError(HttpServletResponse res,int status,String messgae) throws IOException {
        res.setStatus(status);
        objectMapper.writeValue(res.getWriter(), Map.of("error",messgae));
    }
}
