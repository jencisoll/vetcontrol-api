package com.vetcontrol.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordResponse {
    private Long id;
    private PetSummaryResponse pet;
    private UserSummaryResponse veterinarian;
    private String diagnosis;
    private String treatment;
    private String observations;
    private String prescriptions;
    private LocalDateTime createdAt;
}
