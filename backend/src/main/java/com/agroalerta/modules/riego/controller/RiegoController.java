package com.agroalerta.modules.riego.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.riego.dto.RiegoRequest;
import com.agroalerta.modules.riego.dto.RiegoResponse;
import com.agroalerta.modules.riego.service.RiegoService;
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
@RequestMapping("/api/v1/riegos")
@Tag(name = "Riegos", description = "Registro de riegos por campaña")
public class RiegoController {

    private final RiegoService service;

    public RiegoController(RiegoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar riegos paginados (filtro por campaña y búsqueda)")
    @GetMapping
    public PageResponse<RiegoResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idCampana,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, idCampana, page, size);
    }

    @Operation(summary = "Obtener un riego por id")
    @GetMapping("/{id}")
    public RiegoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Registrar un riego")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RiegoResponse create(@Valid @RequestBody RiegoRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar un riego")
    @PutMapping("/{id}")
    public RiegoResponse update(@PathVariable Long id, @Valid @RequestBody RiegoRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar un riego")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
