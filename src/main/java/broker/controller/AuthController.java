package broker.controller;

import broker.dto.request.CreateCustomerRequest;
import broker.dto.request.LoginRequest;
import broker.dto.response.AuthResponse;
import broker.dto.response.CustomerResponse;
import broker.model.Customer;
import broker.security.CustomUserDetails;
import broker.security.JwtTokenProvider;
import broker.service.ICustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ICustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        String token = tokenProvider.generateToken(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(new AuthResponse(
                token,
                userDetails.getCustomerId(),
                userDetails.getUsername(),
                userDetails.getRole()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@Valid @RequestBody CreateCustomerRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());

        Customer customer = customerService.createCustomer(
                request.name(),
                request.username(),
                encodedPassword,
                request.email(),
                "CUSTOMER"
        );

        return ResponseEntity.ok(CustomerResponse.fromEntity(customer));
    }
}