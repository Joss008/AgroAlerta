package com.agroalerta.modules.cultivo.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.cultivo.dto.CultivoRequest;
import com.agroalerta.modules.cultivo.dto.CultivoResponse;
import com.agroalerta.modules.cultivo.service.CultivoService;
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
@RequestMapping("/api/v1/cultivos")
@Tag(name = "Cultivos", description = "Catálogo de cultivos y variedades")
public class CultivoController {

    private final CultivoService service;

    public CultivoController(CultivoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar cultivos paginados con búsqueda opcional")
    @GetMapping
    public PageResponse<CultivoResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, page, size);
    }

    @Operation(summary = "Obtener un cultivo por id")
    @GetMapping("/{id}")
    public CultivoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Crear un cultivo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CultivoResponse create(@Valid @RequestBody CultivoRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar un cultivo")
    @PutMapping("/{id}")
    public CultivoResponse update(@PathVariable Long id, @Valid @RequestBody CultivoRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un cultivo (409 si tiene campañas)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
