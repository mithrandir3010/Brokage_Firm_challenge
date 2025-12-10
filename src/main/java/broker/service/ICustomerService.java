package broker.service;

import broker.model.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Optional<Customer> getCustomerByUsername(String username);

    Customer createCustomer(String name, String username, String password, String email, String role);

    Customer updateCustomer(Long id, String name, String email, String password);

    void deleteCustomer(Long id);
}
