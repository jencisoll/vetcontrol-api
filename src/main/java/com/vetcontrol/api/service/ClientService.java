package com.vetcontrol.api.service;

import com.vetcontrol.api.dto.request.ClientRequest;
import com.vetcontrol.api.dto.response.ClientResponse;
import com.vetcontrol.api.dto.response.PageResponse;
import com.vetcontrol.api.dto.response.PetSummaryResponse;
import com.vetcontrol.api.entity.Client;
import com.vetcontrol.api.entity.Pet;
import com.vetcontrol.api.exception.DuplicateResourceException;
import com.vetcontrol.api.exception.ResourceNotFoundException;
import com.vetcontrol.api.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public PageResponse<ClientResponse> getAllClients(String search, Boolean hasPets, Pageable pageable) {
        Page<Client> page = clientRepository.findAllWithFilters(search, hasPets, pageable);
        return mapToPageResponse(page);
    }

    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return mapToResponse(client);
    }

    public ClientResponse getClientByDni(String dni) {
        Client client = clientRepository.findByDni(dni)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "dni", dni));
        return mapToResponse(client);
    }

    @Transactional
    public ClientResponse createClient(ClientRequest request) {
        if (clientRepository.existsByDni(request.getDni())) {
            throw new DuplicateResourceException("Ya existe un cliente con DNI: " + request.getDni());
        }

        Client client = Client.builder()
                .fullName(request.getFullName())
                .dni(request.getDni())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();

        Client saved = clientRepository.save(client);
        log.info("Cliente creado: {} - DNI: {}", saved.getFullName(), saved.getDni());
        return mapToResponse(saved);
    }

    @Transactional
    public ClientResponse updateClient(Long id, ClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        if (!client.getDni().equals(request.getDni()) && clientRepository.existsByDni(request.getDni())) {
            throw new DuplicateResourceException("Ya existe un cliente con DNI: " + request.getDni());
        }

        client.setFullName(request.getFullName());
        client.setDni(request.getDni());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        client.setAddress(request.getAddress());

        Client updated = clientRepository.save(client);
        log.info("Cliente actualizado: {} - DNI: {}", updated.getFullName(), updated.getDni());
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        clientRepository.delete(client);
        log.info("Cliente eliminado: {} - DNI: {}", client.getFullName(), client.getDni());
    }

    private PageResponse<ClientResponse> mapToPageResponse(Page<Client> page) {
        return PageResponse.<ClientResponse>builder()
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

    private ClientResponse mapToResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .dni(client.getDni())
                .phone(client.getPhone())
                .email(client.getEmail())
                .address(client.getAddress())
                .petsCount(client.getPets() != null ? client.getPets().size() : 0)
                .pets(client.getPets() != null ? client.getPets().stream()
                        .map(this::mapPetSummary)
                        .collect(Collectors.toList()) : null)
                .createdAt(client.getCreatedAt())
                .build();
    }

    private PetSummaryResponse mapPetSummary(Pet pet) {
        return PetSummaryResponse.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .build();
    }
}
