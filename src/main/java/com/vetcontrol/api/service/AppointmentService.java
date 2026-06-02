package com.vetcontrol.api.service;

import com.vetcontrol.api.dto.request.AppointmentRequest;
import com.vetcontrol.api.dto.response.AppointmentResponse;
import com.vetcontrol.api.dto.response.PageResponse;
import com.vetcontrol.api.dto.response.PetSummaryResponse;
import com.vetcontrol.api.dto.response.UserSummaryResponse;
import com.vetcontrol.api.entity.Appointment;
import com.vetcontrol.api.entity.Pet;
import com.vetcontrol.api.entity.User;
import com.vetcontrol.api.entity.enums.AppointmentStatus;
import com.vetcontrol.api.exception.BusinessException;
import com.vetcontrol.api.exception.ResourceNotFoundException;
import com.vetcontrol.api.repository.AppointmentRepository;
import com.vetcontrol.api.repository.PetRepository;
import com.vetcontrol.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PageResponse<AppointmentResponse> getAllAppointments(
            LocalDate dateFrom, LocalDate dateTo, AppointmentStatus status,
            Long petId, Long veterinarianId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAllWithFilters(
                dateFrom, dateTo, status, petId, veterinarianId, pageable);
        return mapToPageResponse(page);
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        return mapToResponse(appointment);
    }

    public List<AppointmentResponse> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByAppointmentDate(date).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public long getPendingAppointmentsCountByDate(LocalDate date) {
        return appointmentRepository.countPendingByDate(date);
    }

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", request.getPetId()));

        User veterinarian = userRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", request.getVeterinarianId()));

        Appointment appointment = Appointment.builder()
                .pet(pet)
                .veterinarian(veterinarian)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reason(request.getReason())
                .status(request.getStatus() != null ? request.getStatus() : AppointmentStatus.PENDING)
                .notes(request.getNotes())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Cita creada: {} - Mascota: {} - Veterinario: {}",
                saved.getAppointmentDate(), pet.getName(), veterinarian.getFullName());
        return mapToResponse(saved);
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));

        if (appointment.getStatus() == AppointmentStatus.ATTENDED) {
            throw new BusinessException("No se puede modificar una cita ya atendida");
        }

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", request.getPetId()));

        User veterinarian = userRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", request.getVeterinarianId()));

        appointment.setPet(pet);
        appointment.setVeterinarian(veterinarian);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());

        Appointment updated = appointmentRepository.save(appointment);
        log.info("Cita actualizada: ID {}", updated.getId());
        return mapToResponse(updated);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));

        appointment.setStatus(status);
        Appointment updated = appointmentRepository.save(appointment);
        log.info("Estado de cita actualizado: ID {} -> {}", updated.getId(), status);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita", "id", id));
        appointmentRepository.delete(appointment);
        log.info("Cita eliminada: ID {}", id);
    }

    private PageResponse<AppointmentResponse> mapToPageResponse(Page<Appointment> page) {
        return PageResponse.<AppointmentResponse>builder()
                .content(page.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .pet(PetSummaryResponse.builder()
                        .id(appointment.getPet().getId())
                        .name(appointment.getPet().getName())
                        .species(appointment.getPet().getSpecies())
                        .breed(appointment.getPet().getBreed())
                        .age(appointment.getPet().getAge())
                        .build())
                .veterinarian(UserSummaryResponse.builder()
                        .id(appointment.getVeterinarian().getId())
                        .fullName(appointment.getVeterinarian().getFullName())
                        .role(appointment.getVeterinarian().getRole().name())
                        .build())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
