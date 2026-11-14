package com.agroalerta.modules.campana.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.campana.dto.CampanaRequest;
import com.agroalerta.modules.campana.dto.CampanaResponse;
import com.agroalerta.modules.campana.service.CampanaService;
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
@RequestMapping("/api/v1/campanas")
@Tag(name = "Campañas", description = "Campañas agrícolas por parcela y cultivo")
public class CampanaController {

    private final CampanaService service;

    public CampanaController(CampanaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar campañas paginadas (filtros por parcela, cultivo y búsqueda)")
    @GetMapping
    public PageResponse<CampanaResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idParcela,
            @RequestParam(required = false) Long idCultivo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, idParcela, idCultivo, page, size);
    }

    @Operation(summary = "Obtener una campaña por id")
    @GetMapping("/{id}")
    public CampanaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Crear una campaña")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CampanaResponse create(@Valid @RequestBody CampanaRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar una campaña")
    @PutMapping("/{id}")
    public CampanaResponse update(@PathVariable Long id, @Valid @RequestBody CampanaRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar una campaña (409 si tiene riegos, gastos o cosechas)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
