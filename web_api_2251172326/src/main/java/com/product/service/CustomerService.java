package com.product.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.product.model.Customer;
import com.product.repository.CustomerRepository;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer updateCustomer(Long id, Customer details) {
        Customer customer = getCustomerById(id);
        if (customer != null) {
            customer.setFullName(details.getFullName());
            customer.setPhoneNumber(details.getPhoneNumber());
            customer.setAddress(details.getAddress());
            customer.setCity(details.getCity());
            customer.setPostalCode(details.getPostalCode());
            return customerRepository.save(customer);
        }
        return null;
    }
}
