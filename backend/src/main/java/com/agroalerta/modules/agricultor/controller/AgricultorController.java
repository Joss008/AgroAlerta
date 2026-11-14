package com.agroalerta.modules.agricultor.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.agricultor.dto.AgricultorRequest;
import com.agroalerta.modules.agricultor.dto.AgricultorResponse;
import com.agroalerta.modules.agricultor.service.AgricultorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agricultores")
@Tag(name = "Agricultores", description = "Gestión de agricultores de Cañete")
public class AgricultorController {

    private final AgricultorService service;

    public AgricultorController(AgricultorService service) {
        this.service = service;
    }

    @Operation(summary = "Listar agricultores paginados con búsqueda opcional")
    @GetMapping
    public PageResponse<AgricultorResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, page, size);
    }

    @Operation(summary = "Obtener un agricultor por id")
    @GetMapping("/{id}")
    public AgricultorResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Crear un agricultor")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgricultorResponse create(@Valid @RequestBody AgricultorRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar un agricultor")
    @PutMapping("/{id}")
    public AgricultorResponse update(@PathVariable Long id, @Valid @RequestBody AgricultorRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un agricultor (409 si tiene parcelas)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
