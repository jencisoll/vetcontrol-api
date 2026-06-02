package com.vetcontrol.api.controller;

import com.vetcontrol.api.dto.request.PetRequest;
import com.vetcontrol.api.dto.response.PageResponse;
import com.vetcontrol.api.dto.response.PetResponse;
import com.vetcontrol.api.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mascotas", description = "Gestión de mascotas")
public class PetController {

    private final PetService petService;

    @GetMapping
    @Operation(summary = "Listar mascotas", description = "Obtiene todas las mascotas con filtros y paginación")
    public ResponseEntity<PageResponse<PetResponse>> getAllPets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) String species,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(petService.getAllPets(search, clientId, species, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mascota por ID")
    public ResponseEntity<PetResponse> getPetById(@PathVariable Long id) {
        return ResponseEntity.ok(petService.getPetById(id));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Listar mascotas por cliente")
    public ResponseEntity<List<PetResponse>> getPetsByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(petService.getPetsByClientId(clientId));
    }

    @GetMapping("/species")
    @Operation(summary = "Obtener todas las especies")
    public ResponseEntity<List<String>> getAllSpecies() {
        return ResponseEntity.ok(petService.getAllSpecies());
    }

    @PostMapping
    @Operation(summary = "Crear mascota")
    public ResponseEntity<PetResponse> createPet(@Valid @RequestBody PetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.createPet(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mascota")
    public ResponseEntity<PetResponse> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetRequest request) {
        return ResponseEntity.ok(petService.updatePet(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}
