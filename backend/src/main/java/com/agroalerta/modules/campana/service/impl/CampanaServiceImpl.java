package com.agroalerta.modules.campana.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.campana.dto.CampanaRequest;
import com.agroalerta.modules.campana.dto.CampanaResponse;
import com.agroalerta.modules.campana.entity.Campana;
import com.agroalerta.modules.campana.mapper.CampanaMapper;
import com.agroalerta.modules.campana.repository.CampanaRestRepository;
import com.agroalerta.modules.campana.service.CampanaService;
import com.agroalerta.modules.cultivo.repository.CultivoRestRepository;
import com.agroalerta.modules.parcela.repository.ParcelaRestRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class CampanaServiceImpl implements CampanaService {

    private final CampanaRestRepository repository;
    private final ParcelaRestRepository parcelaRepository;
    private final CultivoRestRepository cultivoRepository;

    public CampanaServiceImpl(CampanaRestRepository repository,
                              ParcelaRestRepository parcelaRepository,
                              CultivoRestRepository cultivoRepository) {
        this.repository = repository;
        this.parcelaRepository = parcelaRepository;
        this.cultivoRepository = cultivoRepository;
    }

    @Override
    public PageResponse<CampanaResponse> list(String q, Long idParcela, Long idCultivo,
                                              int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Campana> result = repository.search(query, idParcela, idCultivo, p, s);
        return PageResponse.of(
                result.content().stream().map(CampanaMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public CampanaResponse get(Long id) {
        return CampanaMapper.toResponse(find(id));
    }

    @Override
    public CampanaResponse create(CampanaRequest request) {
        validateFechas(request.fechaInicio(), request.fechaFin());
        requireParcela(request.idParcela());
        requireCultivo(request.idCultivo());
        return CampanaMapper.toResponse(repository.insert(request));
    }

    @Override
    public CampanaResponse update(Long id, CampanaRequest request) {
        validateFechas(request.fechaInicio(), request.fechaFin());
        find(id);
        requireParcela(request.idParcela());
        requireCultivo(request.idCultivo());
        return CampanaMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        find(id);
        if (repository.countRiegosByCampana(id) > 0
                || repository.countGastosByCampana(id) > 0
                || repository.countCosechasByCampana(id) > 0) {
            throw BusinessException.conflict(
                    "No se puede eliminar: la campaña tiene riegos, gastos o cosechas registrados");
        }
        repository.delete(id);
    }

    private void validateFechas(LocalDate inicio, LocalDate fin) {
        if (fin != null && fin.isBefore(inicio)) {
            throw BusinessException.of("La fecha fin no puede ser anterior a la fecha de inicio");
        }
    }

    private void requireParcela(Long id) {
        parcelaRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("La parcela con id " + id + " no existe"));
    }

    private void requireCultivo(Long id) {
        cultivoRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("El cultivo con id " + id + " no existe"));
    }

    private Campana find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Campaña", id));
    }
}
