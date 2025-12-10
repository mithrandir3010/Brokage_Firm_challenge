package broker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
            "application", "Brokage Firm API",
            "version", "1.0.0",
            "status", "running",
            "documentation", "/swagger-ui.html",
            "endpoints", Map.of(
                "auth", Map.of(
                    "login", "POST /api/auth/login",
                    "register", "POST /api/auth/register"
                ),
                "customers", Map.of(
                    "list", "GET /api/customers",
                    "get", "GET /api/customers/{id}",
                    "create", "POST /api/customers",
                    "update", "PUT /api/customers/{id}",
                    "delete", "DELETE /api/customers/{id}"
                ),
                "orders", Map.of(
                    "list", "GET /api/orders",
                    "create", "POST /api/orders",
                    "cancel", "DELETE /api/orders/{id}",
                    "match", "POST /api/orders/{id}/match"
                ),
                "assets", Map.of(
                    "list", "GET /api/assets",
                    "deposit", "POST /api/assets/deposit",
                    "withdraw", "POST /api/assets/withdraw"
                )
            )
        );
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "message", "Service is healthy"
        );
    }
}
