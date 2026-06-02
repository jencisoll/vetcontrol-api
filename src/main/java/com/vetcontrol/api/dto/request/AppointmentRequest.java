package com.vetcontrol.api.dto.request;

import com.vetcontrol.api.entity.enums.AppointmentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {

    @NotNull(message = "El ID de la mascota es obligatorio")
    private Long petId;

    @NotNull(message = "El ID del veterinario es obligatorio")
    private Long veterinarianId;

    @NotNull(message = "La fecha de la cita es obligatoria")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro")
    private LocalDate appointmentDate;

    @NotNull(message = "La hora de la cita es obligatoria")
    private LocalTime appointmentTime;

    @NotBlank(message = "El motivo de la cita es obligatorio")
    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String reason;

    private AppointmentStatus status;

    @Size(max = 2000, message = "Las notas no pueden exceder 2000 caracteres")
    private String notes;
}
