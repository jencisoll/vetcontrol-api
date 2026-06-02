package com.vetcontrol.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PetRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;

    @NotBlank(message = "La especie es obligatoria")
    @Size(max = 50, message = "La especie no puede exceder 50 caracteres")
    private String species;

    @Size(max = 50, message = "La raza no puede exceder 50 caracteres")
    private String breed;

    @Min(value = 0, message = "La edad no puede ser negativa")
    @Max(value = 50, message = "La edad no puede exceder 50 años")
    private Integer age;

    @Pattern(regexp = "^(MACHO|HEMBRA|Male|Female|male|female)$", message = "El género debe ser MACHO o HEMBRA")
    private String gender;

    @Size(max = 5000, message = "El historial médico no puede exceder 5000 caracteres")
    private String medicalHistory;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clientId;
}
