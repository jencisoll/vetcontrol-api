package com.vetcontrol.api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clientId;

    private Long appointmentId;

    @NotEmpty(message = "Debe incluir al menos un ítem")
    @Valid
    private List<InvoiceItemRequest> items;
}
