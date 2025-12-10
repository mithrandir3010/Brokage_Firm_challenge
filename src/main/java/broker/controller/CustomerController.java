package broker.controller;

import broker.dto.request.CreateCustomerRequest;
import broker.dto.request.UpdateCustomerRequest;
import broker.dto.response.CustomerResponse;
import broker.model.Customer;
import broker.security.CustomUserDetails;
import broker.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final ICustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        List<CustomerResponse> response = customers.stream()
                .map(CustomerResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (!userDetails.getRole().equals("ADMIN") && !userDetails.getCustomerId().equals(id)) {
            id = userDetails.getCustomerId();
        }
        
        return customerService.getCustomerById(id)
                .map(CustomerResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return customerService.getCustomerById(userDetails.getCustomerId())
                .map(CustomerResponse::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());
        
        Customer customer = customerService.createCustomer(
                request.name(),
                request.username(),
                encodedPassword,
                request.email(),
                request.role()
        );
        return ResponseEntity.ok(CustomerResponse.fromEntity(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        if (!userDetails.getRole().equals("ADMIN") && !userDetails.getCustomerId().equals(id)) {
            id = userDetails.getCustomerId();
        }
        
        String encodedPassword = null;
        if (request.password() != null && !request.password().isEmpty()) {
            encodedPassword = passwordEncoder.encode(request.password());
        }
        
        Customer customer = customerService.updateCustomer(id, request.name(), request.email(), encodedPassword);
        return ResponseEntity.ok(CustomerResponse.fromEntity(customer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
