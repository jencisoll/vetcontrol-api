package com.vetcontrol.api.service;

import com.vetcontrol.api.dto.response.DashboardStatsResponse;
import com.vetcontrol.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ClientRepository clientRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;

    public DashboardStatsResponse getStats() {
        LocalDate today = LocalDate.now();

        long totalClientes = clientRepository.count();
        long totalMascotas = petRepository.count();
        long citasHoy = appointmentRepository.findByAppointmentDate(today).size();

        // Campos que no calculamos aún (ponemos 0)
        long clientesNuevosEsteMes = 0;
        long mascotasAtendidas = 0;
        double ingresosMes = 0.0;

        return DashboardStatsResponse.builder()
                .totalClientes(totalClientes)
                .totalMascotas(totalMascotas)
                .citasHoy(citasHoy)
                .ingresosMes(ingresosMes)
                .clientesNuevosEsteMes(clientesNuevosEsteMes)
                .mascotasAtendidas(mascotasAtendidas)
                .build();
    }
}