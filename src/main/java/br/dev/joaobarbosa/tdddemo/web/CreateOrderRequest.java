package br.dev.joaobarbosa.tdddemo.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name must not be blank")
        String customerName,

        @DecimalMin(value = "0.01", message = "Total must be greater than zero")
        BigDecimal total
) {}

