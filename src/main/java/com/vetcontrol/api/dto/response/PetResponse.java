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
public class PetResponse {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private String medicalHistory;
    private ClientSummaryResponse client;
    private int medicalRecordsCount;
    private int appointmentsCount;
    private LocalDateTime createdAt;
}
