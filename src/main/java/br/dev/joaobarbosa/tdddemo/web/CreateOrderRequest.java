package br.dev.joaobarbosa.tdddemo.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name must not be blank")
        String customerName,

        @NotBlank(message = "Customer email must not be blank")
        @Email(message = "Customer email must be valid")
        String customerEmail,

        @NotBlank(message = "Product SKU must not be blank")
        String productSku,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity,

        @DecimalMin(value = "0.01", message = "Total must be greater than zero")
        BigDecimal total
) {}
