package com.agroalerta.modules.venta.controller;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.venta.dto.VentaRequest;
import com.agroalerta.modules.venta.dto.VentaResponse;
import com.agroalerta.modules.venta.service.VentaService;
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
@RequestMapping("/api/v1/ventas")
@Tag(name = "Ventas", description = "Ventas de la producción cosechada")
public class VentaController {

    private final VentaService service;

    public VentaController(VentaService service) {
        this.service = service;
    }

    @Operation(summary = "Listar ventas paginadas (filtro por cosecha y búsqueda por comprador)")
    @GetMapping
    public PageResponse<VentaResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long idCosecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(q, idCosecha, page, size);
    }

    @Operation(summary = "Obtener una venta por id")
    @GetMapping("/{id}")
    public VentaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Registrar una venta (409 si supera la cantidad cosechada)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse create(@Valid @RequestBody VentaRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Actualizar una venta")
    @PutMapping("/{id}")
    public VentaResponse update(@PathVariable Long id, @Valid @RequestBody VentaRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Eliminar una venta")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
