package com.vetcontrol.api.controller;

import com.vetcontrol.api.dto.request.ClientRequest;
import com.vetcontrol.api.dto.response.ClientResponse;
import com.vetcontrol.api.dto.response.PageResponse;
import com.vetcontrol.api.service.ClientService;
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

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Clientes", description = "Gestión de dueños de mascotas")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Obtiene todos los clientes con búsqueda y paginación")
    public ResponseEntity<PageResponse<ClientResponse>> getAllClients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean hasPets,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(clientService.getAllClients(search, hasPets, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Buscar cliente por DNI")
    public ResponseEntity<ClientResponse> getClientByDni(@PathVariable String dni) {
        return ResponseEntity.ok(clientService.getClientByDni(dni));
    }

    @PostMapping
    @Operation(summary = "Crear cliente")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
