package com.agroalerta.modules.parcela.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.parcela.dto.ParcelaRequest;
import com.agroalerta.modules.parcela.dto.ParcelaResponse;
import com.agroalerta.modules.parcela.service.ParcelaService;
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
@RequestMapping("/api/v1/parcelas")
@Tag(name = "Parcelas", description = "Parcelas agrícolas por agricultor")
public class ParcelaController {

    private final ParcelaService service;

    public ParcelaController(ParcelaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar parcelas paginadas (filtro por agricultor y búsqueda)")
    @GetMapping
    public PageResponse<ParcelaResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idAgricultor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, idAgricultor, page, size);
    }

    @Operation(summary = "Obtener una parcela por id")
    @GetMapping("/{id}")
    public ParcelaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Crear una parcela")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParcelaResponse create(@Valid @RequestBody ParcelaRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar una parcela")
    @PutMapping("/{id}")
    public ParcelaResponse update(@PathVariable Long id, @Valid @RequestBody ParcelaRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar una parcela (409 si tiene campañas)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
