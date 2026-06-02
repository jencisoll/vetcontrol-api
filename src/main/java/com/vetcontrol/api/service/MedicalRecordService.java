package com.vetcontrol.api.service;

import com.vetcontrol.api.dto.request.MedicalRecordRequest;
import com.vetcontrol.api.dto.response.MedicalRecordResponse;
import com.vetcontrol.api.dto.response.PageResponse;
import com.vetcontrol.api.dto.response.PetSummaryResponse;
import com.vetcontrol.api.dto.response.UserSummaryResponse;
import com.vetcontrol.api.entity.MedicalRecord;
import com.vetcontrol.api.entity.Pet;
import com.vetcontrol.api.entity.User;
import com.vetcontrol.api.exception.ResourceNotFoundException;
import com.vetcontrol.api.repository.MedicalRecordRepository;
import com.vetcontrol.api.repository.PetRepository;
import com.vetcontrol.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    public PageResponse<MedicalRecordResponse> getAllMedicalRecords(
            Long petId, Long veterinarianId, LocalDateTime dateFrom, LocalDateTime dateTo,
            String search, Pageable pageable) {
        Page<MedicalRecord> page = medicalRecordRepository.findAllWithFilters(
                petId, veterinarianId, dateFrom, dateTo, search, pageable);
        return mapToPageResponse(page);
    }

    public MedicalRecordResponse getMedicalRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro médico", "id", id));
        return mapToResponse(record);
    }

    public List<MedicalRecordResponse> getMedicalRecordsByPetId(Long petId) {
        return medicalRecordRepository.findByPetIdOrderByCreatedAtDesc(petId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", request.getPetId()));

        User veterinarian = userRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", request.getVeterinarianId()));

        MedicalRecord record = MedicalRecord.builder()
                .pet(pet)
                .veterinarian(veterinarian)
                .diagnosis(request.getDiagnosis())
                .treatment(request.getTreatment())
                .observations(request.getObservations())
                .prescriptions(request.getPrescriptions())
                .build();

        MedicalRecord saved = medicalRecordRepository.save(record);
        log.info("Registro médico creado: Mascota: {} - Diagnóstico: {}",
                pet.getName(), saved.getDiagnosis());
        return mapToResponse(saved);
    }

    @Transactional
    public MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro médico", "id", id));

        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", request.getPetId()));

        User veterinarian = userRepository.findById(request.getVeterinarianId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinario", "id", request.getVeterinarianId()));

        record.setPet(pet);
        record.setVeterinarian(veterinarian);
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatment(request.getTreatment());
        record.setObservations(request.getObservations());
        record.setPrescriptions(request.getPrescriptions());

        MedicalRecord updated = medicalRecordRepository.save(record);
        log.info("Registro médico actualizado: ID {}", updated.getId());
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteMedicalRecord(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro médico", "id", id));
        medicalRecordRepository.delete(record);
        log.info("Registro médico eliminado: ID {}", id);
    }

    private PageResponse<MedicalRecordResponse> mapToPageResponse(Page<MedicalRecord> page) {
        return PageResponse.<MedicalRecordResponse>builder()
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

    private MedicalRecordResponse mapToResponse(MedicalRecord record) {
        return MedicalRecordResponse.builder()
                .id(record.getId())
                .pet(PetSummaryResponse.builder()
                        .id(record.getPet().getId())
                        .name(record.getPet().getName())
                        .species(record.getPet().getSpecies())
                        .breed(record.getPet().getBreed())
                        .age(record.getPet().getAge())
                        .build())
                .veterinarian(UserSummaryResponse.builder()
                        .id(record.getVeterinarian().getId())
                        .fullName(record.getVeterinarian().getFullName())
                        .role(record.getVeterinarian().getRole().name())
                        .build())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .observations(record.getObservations())
                .prescriptions(record.getPrescriptions())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
