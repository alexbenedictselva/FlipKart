package com.example.service;

import com.example.dao.CustomerDAO;
import com.example.model.Customer;

import java.sql.SQLException;

public class CustomerService {

    private final CustomerDAO customerDAO = new CustomerDAO();

    public void register(Customer customer) throws SQLException {

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer data is required"
            );
        }
        if (isBlank(customer.getName())) {
            throw new IllegalArgumentException(
                    "Name is required"
            );
        }
        if (isBlank(customer.getPhNo())) {
            throw new IllegalArgumentException(
                    "Phone number is required"
            );
        }
        if (isBlank(customer.getPassword())) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }
        if (customerDAO.existsByPhoneNumber(customer.getPhNo())) {
            throw new IllegalArgumentException(
                    "Phone number already registered"
            );
        }
        customerDAO.create(customer);
    }

    public Customer login(String phoneNumber, String password)
            throws SQLException {
        if (isBlank(phoneNumber)) {
            throw new IllegalArgumentException(
                    "Phone number is required"
            );
        }
        if (isBlank(password)) {
            throw new IllegalArgumentException(
                    "Password is required"
            );
        }
        Customer customer =
                customerDAO.findByPhoneNumber(phoneNumber);

        if (customer == null) {
            return null;
        }
        if (!customer.getPassword().equals(password)) {
            return null;
        }
        return customer;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}