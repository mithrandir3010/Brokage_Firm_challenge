package broker.service;

import broker.exception.CustomerNotFoundException;
import broker.exception.DuplicateResourceException;
import broker.model.Customer;
import broker.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> getCustomerByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Customer createCustomer(String name, String username, String password, String email, String role) {
        if (customerRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Müşteri", "username", username);
        }

        if (customerRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Müşteri", "email", email);
        }

        Customer customer = new Customer();
        customer.setName(name);
        customer.setUsername(username);
        customer.setPassword(password);
        customer.setEmail(email);
        customer.setRole(role != null ? role : "CUSTOMER");

        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(Long id, String name, String email, String password) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        if (name != null && !name.isEmpty()) {
            customer.setName(name);
        }

        if (email != null && !email.isEmpty()) {
            if (!email.equals(customer.getEmail()) && customerRepository.existsByEmail(email)) {
                throw new DuplicateResourceException("Müşteri", "email", email);
            }
            customer.setEmail(email);
        }

        if (password != null && !password.isEmpty()) {
            customer.setPassword(password);
        }

        return customerRepository.save(customer);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }
}
