package com.vetcontrol.api.service;

import com.vetcontrol.api.dto.response.DashboardStatsResponse;
import com.vetcontrol.api.entity.enums.InvoiceStatus;
import com.vetcontrol.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ClientRepository clientRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardStatsResponse getStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long totalClients = clientRepository.count();
        long totalPets = petRepository.count();
        long totalAppointments = appointmentRepository.count();
        long pendingAppointments = appointmentRepository.countPendingByDate(today);
        long todayAppointments = appointmentRepository.findByAppointmentDate(today).size();
        long totalProducts = productRepository.count();
        long lowStockProducts = productRepository.findByActiveTrue().stream()
                .filter(p -> p.getStock() <= 5).count();

        BigDecimal totalRevenue = invoiceRepository.findAllWithFilters(
                null, InvoiceStatus.PAID, null, null, null, org.springframework.data.domain.Pageable.unpaged())
                .getContent().stream()
                .map(i -> i.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal todayRevenue = invoiceRepository.findAllWithFilters(
                null, InvoiceStatus.PAID, startOfDay, endOfDay, null, org.springframework.data.domain.Pageable.unpaged())
                .getContent().stream()
                .map(i -> i.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardStatsResponse.builder()
                .totalClientes(0L)
                .totalMascotas(0L)
                .citasHoy(0L)
                .ingresosMes(0.0)
                .clientesNuevosEsteMes(0L)
                .mascotasAtendidas(0L)
                .build();
    }
}
