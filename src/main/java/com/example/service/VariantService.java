package com.example.service;

import com.example.dao.VariantHandlerDAO;

import java.sql.SQLException;
import java.util.Map;

public class VariantService {
    private final VariantHandlerDAO variantHandlerDAO = new VariantHandlerDAO();

    public int postVariant(Map<String,String> mpp,int productId) throws SQLException {
        return variantHandlerDAO.postProductVariant(mpp,productId);
    }
}
