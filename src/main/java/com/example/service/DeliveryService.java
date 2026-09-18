package com.example.service;

import com.example.dao.DeliveryDAO;
import com.example.dao.DeliveryPartnerDAO;
import com.example.dto.DeliveryPartnerOrderResponse;
import com.example.model.DeliveryPartner;

import java.sql.SQLException;
import java.util.Set;
import java.util.List;

public class DeliveryService {

    private final DeliveryPartnerDAO deliveryPartnerDAO = new DeliveryPartnerDAO();
    private static final Set<String> ALLOWED_UPDATE_STATUSES =
            Set.of("OUT_FOR_DELIVERY", "DELIVERED", "FAILED");

    public void changeDeliveryStatus(int deliveryId, int deliveryPartnerId, String status)
            throws SQLException {

        if (deliveryId <= 0) {
            throw new IllegalArgumentException("Invalid deliveryId");
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }
        if (!ALLOWED_UPDATE_STATUSES.contains(status)) {
            throw new IllegalArgumentException(
                    "Status must be OUT_FOR_DELIVERY, DELIVERED, or FAILED"
            );
        }

        deliveryPartnerDAO.changeDeliveryStatus(deliveryId, deliveryPartnerId, status);
    }

    // DeliveryService

    public List<DeliveryPartnerOrderResponse> getCurrentDeliveries(int deliveryPartnerId)
            throws SQLException {

        if (deliveryPartnerId <= 0) {
            throw new IllegalArgumentException("Invalid deliveryPartnerId");
        }

        return deliveryPartnerDAO.getCurrentDeliveries(deliveryPartnerId);
    }
}