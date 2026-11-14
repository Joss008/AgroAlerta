package com.agroalerta.modules.venta.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.cosecha.entity.Cosecha;
import com.agroalerta.modules.cosecha.repository.CosechaRestRepository;
import com.agroalerta.modules.venta.dto.VentaRequest;
import com.agroalerta.modules.venta.dto.VentaResponse;
import com.agroalerta.modules.venta.entity.Venta;
import com.agroalerta.modules.venta.mapper.VentaMapper;
import com.agroalerta.modules.venta.repository.VentaRestRepository;
import com.agroalerta.modules.venta.service.VentaService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRestRepository repository;
    private final CosechaRestRepository cosechaRepository;

    public VentaServiceImpl(VentaRestRepository repository, CosechaRestRepository cosechaRepository) {
        this.repository = repository;
        this.cosechaRepository = cosechaRepository;
    }

    @Override
    public PageResponse<VentaResponse> list(String q, Long idCosecha, int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Venta> result = repository.search(query, idCosecha, p, s);
        return PageResponse.of(
                result.content().stream().map(VentaMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public VentaResponse get(Long id) {
        return VentaMapper.toResponse(find(id));
    }

    @Override
    public VentaResponse create(VentaRequest request) {
        Cosecha cosecha = requireCosecha(request.idCosecha());
        validarCantidadDisponible(cosecha, request.cantidadKg(), null);
        return VentaMapper.toResponse(repository.insert(request));
    }

    @Override
    public VentaResponse update(Long id, VentaRequest request) {
        find(id);
        Cosecha cosecha = requireCosecha(request.idCosecha());
        validarCantidadDisponible(cosecha, request.cantidadKg(), id);
        return VentaMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        find(id);
        repository.delete(id);
    }

    /**
     * Regla de negocio: la cantidad vendida acumulada por cosecha no puede
     * superar la cantidad cosechada.
     */
    private void validarCantidadDisponible(Cosecha cosecha, BigDecimal nuevaCantidad, Long ventaExcluida) {
        BigDecimal vendido = repository.sumCantidadKgByCosecha(cosecha.getId());
        if (ventaExcluida != null) {
            Venta anterior = find(ventaExcluida);
            vendido = vendido.subtract(anterior.getCantidadKg());
        }
        BigDecimal disponible = cosecha.getCantidadKg().subtract(vendido);
        if (nuevaCantidad.compareTo(disponible) > 0) {
            throw BusinessException.conflict(
                    "La cantidad vendida supera la cantidad cosechada: "
                            + "disponible " + stripTrailing(disponible) + " kg de "
                            + stripTrailing(cosecha.getCantidadKg()) + " kg cosechados ("
                            + stripTrailing(vendido) + " kg ya vendidos)");
        }
    }

    private String stripTrailing(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private Cosecha requireCosecha(Long id) {
        return cosechaRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("La cosecha con id " + id + " no existe"));
    }

    private Venta find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Venta", id));
    }
}
