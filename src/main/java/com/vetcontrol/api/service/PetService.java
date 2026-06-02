package com.vetcontrol.api.service;

import com.vetcontrol.api.dto.request.PetRequest;
import com.vetcontrol.api.dto.response.ClientSummaryResponse;
import com.vetcontrol.api.dto.response.PageResponse;
import com.vetcontrol.api.dto.response.PetResponse;
import com.vetcontrol.api.entity.Client;
import com.vetcontrol.api.entity.Pet;
import com.vetcontrol.api.exception.ResourceNotFoundException;
import com.vetcontrol.api.repository.ClientRepository;
import com.vetcontrol.api.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final ClientRepository clientRepository;
    public PageResponse<PetResponse> getAllPets(
            String search, Long clientId, String species, Pageable pageable) {

        // ✅ null → "" para evitar error de tipo en PostgreSQL
        String searchParam  = search  != null ? search  : "";
        String speciesParam = species != null ? species : "";

        Page<Pet> page = petRepository.findAllWithFilters(
                searchParam, clientId, speciesParam, pageable);

        return PageResponse.<PetResponse>builder()
                .content(page.getContent().stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList()))
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }

    public PetResponse getPetById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", id));
        return mapToResponse(pet);
    }

    public List<PetResponse> getPetsByClientId(Long clientId) {
        return petRepository.findByClientId(clientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<String> getAllSpecies() {
        return petRepository.findAllSpecies();
    }

    @Transactional
    public PetResponse createPet(PetRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClientId()));

        Pet pet = Pet.builder()
                .name(request.getName())
                .species(request.getSpecies())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(request.getGender())
                .medicalHistory(request.getMedicalHistory())
                .client(client)
                .build();

        Pet saved = petRepository.save(pet);
        log.info("Mascota creada: {} - Cliente: {}", saved.getName(), client.getFullName());
        return mapToResponse(saved);
    }

    @Transactional
    public PetResponse updatePet(Long id, PetRequest request) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", id));

        if (!pet.getClient().getId().equals(request.getClientId())) {
            Client client = clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.getClientId()));
            pet.setClient(client);
        }

        pet.setName(request.getName());
        pet.setSpecies(request.getSpecies());
        pet.setBreed(request.getBreed());
        pet.setAge(request.getAge());
        pet.setGender(request.getGender());
        pet.setMedicalHistory(request.getMedicalHistory());

        Pet updated = petRepository.save(pet);
        log.info("Mascota actualizada: {}", updated.getName());
        return mapToResponse(updated);
    }

    @Transactional
    public void deletePet(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota", "id", id));
        petRepository.delete(pet);
        log.info("Mascota eliminada: {}", pet.getName());
    }

    private PageResponse<PetResponse> mapToPageResponse(Page<Pet> page) {
        return PageResponse.<PetResponse>builder()
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

    private PetResponse mapToResponse(Pet pet) {
        return PetResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .gender(pet.getGender())
                .medicalHistory(pet.getMedicalHistory())
                .client(ClientSummaryResponse.builder()
                        .id(pet.getClient().getId())
                        .fullName(pet.getClient().getFullName())
                        .phone(pet.getClient().getPhone())
                        .build())
                .medicalRecordsCount(pet.getMedicalRecords() != null ? pet.getMedicalRecords().size() : 0)
                .appointmentsCount(pet.getAppointments() != null ? pet.getAppointments().size() : 0)
                .createdAt(pet.getCreatedAt())
                .build();
    }
}
