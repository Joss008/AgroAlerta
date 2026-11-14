package com.agroalerta.modules.gasto.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.gasto.dto.GastoRequest;
import com.agroalerta.modules.gasto.dto.GastoResponse;
import com.agroalerta.modules.gasto.service.GastoService;
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
@RequestMapping("/api/v1/gastos")
@Tag(name = "Gastos", description = "Gastos por campaña")
public class GastoController {

    private final GastoService service;

    public GastoController(GastoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar gastos paginados (filtro por campaña y búsqueda)")
    @GetMapping
    public PageResponse<GastoResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idCampana,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, idCampana, page, size);
    }

    @Operation(summary = "Obtener un gasto por id")
    @GetMapping("/{id}")
    public GastoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Registrar un gasto")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GastoResponse create(@Valid @RequestBody GastoRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar un gasto")
    @PutMapping("/{id}")
    public GastoResponse update(@PathVariable Long id, @Valid @RequestBody GastoRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un gasto")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
