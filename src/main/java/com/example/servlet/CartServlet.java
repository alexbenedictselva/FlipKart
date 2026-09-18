package com.example.servlet;

import com.example.dto.CartItemsResponse;
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
    protected void doGet(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException{
        setJsonResponse(res);
        try{
            int custId = getAuthenticatedUserId(req);
            List<CartItemsResponse> cartItemsResponses = cartService.getAllCartProducts(custId);
            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(),cartItemsResponses);
        }catch(SQLException e){
            e.printStackTrace();
            sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Database error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req,HttpServletResponse res) throws ServletException,IOException{
        setJsonResponse(res);
        try{
            int custId = getAuthenticatedUserId(req);
            JsonNode body = objectMapper.readTree(req.getInputStream());

            if(body.get("productInDisplayId") == null){
                sendError(res,HttpServletResponse.SC_BAD_REQUEST,"productInDisplayId is required");
                return;
            }

            int productInDisId = body.get("productInDisplayId").asInt();
            cartService.AddProduct(custId,productInDisId);

            res.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(res.getWriter(),Map.of("message","Added Successfully!"));
        }catch(SQLException e){
            e.printStackTrace();
            sendError(res,HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"Database error");
        }
    }

    private int getAuthenticatedUserId(HttpServletRequest req){
        Object userId = req.getAttribute("userId");

        if(userId == null){
            throw new IllegalStateException("Authenticated user ID not found");
        }

        return (Integer)userId;
    }

    private void setJsonResponse(HttpServletResponse res){
        res.setCharacterEncoding("UTF-8");
        res.setContentType("application/json");
    }

    private void sendError(HttpServletResponse res,int status,String message) throws IOException{
        res.setStatus(status);
        objectMapper.writeValue(res.getWriter(),Map.of("error",message));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String cartItemIdParam = req.getParameter("CartItemId");

        if (cartItemIdParam == null || cartItemIdParam.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("CartItemId is required");
            return;
        }

        try {

            int cartItemId = Integer.parseInt(cartItemIdParam);

            cartService.deleteCartItem(cartItemId);

            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            objectMapper.writeValue(resp.getWriter(),Map.of("message","Deleted Successfully"));

        } catch (NumberFormatException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("CartItemId must be a number");

        } catch (IllegalArgumentException e) {

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(e.getMessage());

        } catch (SQLException e) {

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Failed to delete cart item");
        }
    }
}