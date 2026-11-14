package com.agroalerta.modules.cosecha.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.cosecha.dto.CosechaRequest;
import com.agroalerta.modules.cosecha.dto.CosechaResponse;
import com.agroalerta.modules.cosecha.entity.Cosecha;
import com.agroalerta.modules.cosecha.mapper.CosechaMapper;
import com.agroalerta.modules.cosecha.repository.CosechaRestRepository;
import com.agroalerta.modules.cosecha.service.CosechaService;
import com.agroalerta.modules.venta.repository.VentaRestRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class CosechaServiceImpl implements CosechaService {

    private final CosechaRestRepository repository;
    private final VentaRestRepository ventaRepository;

    public CosechaServiceImpl(CosechaRestRepository repository, VentaRestRepository ventaRepository) {
        this.repository = repository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    public PageResponse<CosechaResponse> list(String q, Long idCampana, int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Cosecha> result = repository.search(query, idCampana, p, s);
        return PageResponse.of(
                result.content().stream().map(CosechaMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public CosechaResponse get(Long id) {
        return CosechaMapper.toResponse(find(id));
    }

    @Override
    public CosechaResponse create(CosechaRequest request) {
        requireCampana(request.idCampana());
        return CosechaMapper.toResponse(repository.insert(request));
    }

    @Override
    public CosechaResponse update(Long id, CosechaRequest request) {
        find(id);
        requireCampana(request.idCampana());
        validarVentasAcumuladas(id, request.cantidadKg());
        return CosechaMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        find(id);
        if (ventaRepository.countByCosecha(id) > 0) {
            throw BusinessException.conflict(
                    "No se puede eliminar: la cosecha tiene ventas registradas");
        }
        repository.delete(id);
    }

    private void validarVentasAcumuladas(Long idCosecha, BigDecimal cantidadKg) {
        BigDecimal vendido = ventaRepository.sumCantidadKgByCosecha(idCosecha);
        if (vendido.compareTo(cantidadKg) > 0) {
            throw BusinessException.conflict(
                    "No se puede reducir la cantidad cosechada: ya se vendieron "
                            + vendido.stripTrailingZeros().toPlainString() + " kg");
        }
    }

    private void requireCampana(Long id) {
        if (!repository.campanaExists(id)) {
            throw BusinessException.of("La campaña con id " + id + " no existe");
        }
    }

    private Cosecha find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Cosecha", id));
    }
}
