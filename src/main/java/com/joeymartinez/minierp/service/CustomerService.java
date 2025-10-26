package com.joeymartinez.minierp.service;

import com.joeymartinez.minierp.model.Customer;
import com.joeymartinez.minierp.dto.CustomerUpdateDTO;
import com.joeymartinez.minierp.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import static com.joeymartinez.minierp.util.UpdateUtils.updateIfPresent;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, CustomerUpdateDTO customerUpdateDTO) {
        Customer existingCustomer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("No Customer Found"));
        updateIfPresent(customerUpdateDTO.getFirstName(), existingCustomer::setFirstName);
        updateIfPresent(customerUpdateDTO.getLastName(), existingCustomer::setLastName);
        updateIfPresent(customerUpdateDTO.getEmail(), existingCustomer::setEmail);
        updateIfPresent(customerUpdateDTO.getAddress(), existingCustomer::setAddress);
        updateIfPresent(customerUpdateDTO.getCity(), existingCustomer::setCity);
        updateIfPresent(customerUpdateDTO.getState(), existingCustomer::setState);
        updateIfPresent(customerUpdateDTO.getPhoneNumber(), existingCustomer::setPhoneNumber);
        existingCustomer.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("No customer found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
