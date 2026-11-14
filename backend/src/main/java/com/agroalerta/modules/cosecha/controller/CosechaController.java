package com.agroalerta.modules.cosecha.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.cosecha.dto.CosechaRequest;
import com.agroalerta.modules.cosecha.dto.CosechaResponse;
import com.agroalerta.modules.cosecha.service.CosechaService;
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
@RequestMapping("/api/v1/cosechas")
@Tag(name = "Cosechas", description = "Cosechas por campaña")
public class CosechaController {

    private final CosechaService service;

    public CosechaController(CosechaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar cosechas paginadas (filtro por campaña y búsqueda)")
    @GetMapping
    public PageResponse<CosechaResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idCampana,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, idCampana, page, size);
    }

    @Operation(summary = "Obtener una cosecha por id")
    @GetMapping("/{id}")
    public CosechaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Registrar una cosecha")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CosechaResponse create(@Valid @RequestBody CosechaRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar una cosecha")
    @PutMapping("/{id}")
    public CosechaResponse update(@PathVariable Long id, @Valid @RequestBody CosechaRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar una cosecha (409 si tiene ventas)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
