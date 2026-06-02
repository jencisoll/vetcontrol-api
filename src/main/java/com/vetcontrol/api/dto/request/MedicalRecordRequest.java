package com.vetcontrol.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class MedicalRecordRequest {

    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    @NotNull(message = "El ID del veterinario es obligatorio")
    private Long veterinarianId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    @Size(max = 200, message = "El diagnóstico no puede exceder 200 caracteres")
    private String diagnosis;

    @Size(max = 5000, message = "El tratamiento no puede exceder 5000 caracteres")
    private String treatment;

    @Size(max = 5000, message = "Las observaciones no pueden exceder 5000 caracteres")
    private String observations;

    @Size(max = 5000, message = "Las prescripciones no pueden exceder 5000 caracteres")
    private String prescriptions;
}
