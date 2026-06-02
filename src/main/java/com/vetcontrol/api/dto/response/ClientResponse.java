package com.vetcontrol.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long id;
    private String fullName;
    private String dni;
    private String phone;
    private String email;
    private String address;
    private int petsCount;
    private List<PetSummaryResponse> pets;
    private LocalDateTime createdAt;
}
