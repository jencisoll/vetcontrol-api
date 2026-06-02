package com.vetcontrol.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalClientes;
    private Long totalMascotas;
    private Long citasHoy;
    private Double ingresosMes;
    private Long clientesNuevosEsteMes;
    private Long mascotasAtendidas;
}