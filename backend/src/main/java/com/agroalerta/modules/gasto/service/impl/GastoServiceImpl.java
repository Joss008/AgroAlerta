package com.agroalerta.modules.gasto.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.gasto.dto.GastoRequest;
import com.agroalerta.modules.gasto.dto.GastoResponse;
import com.agroalerta.modules.gasto.entity.Gasto;
import com.agroalerta.modules.gasto.mapper.GastoMapper;
import com.agroalerta.modules.gasto.repository.GastoRestRepository;
import com.agroalerta.modules.gasto.service.GastoService;
import org.springframework.stereotype.Service;

@Service
public class GastoServiceImpl implements GastoService {

    private final GastoRestRepository repository;

    public GastoServiceImpl(GastoRestRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<GastoResponse> list(String q, Long idCampana, int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Gasto> result = repository.search(query, idCampana, p, s);
        return PageResponse.of(
                result.content().stream().map(GastoMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public GastoResponse get(Long id) {
        return GastoMapper.toResponse(find(id));
    }

    @Override
    public GastoResponse create(GastoRequest request) {
        requireCampana(request.idCampana());
        return GastoMapper.toResponse(repository.insert(request));
    }

    @Override
    public GastoResponse update(Long id, GastoRequest request) {
        find(id);
        requireCampana(request.idCampana());
        return GastoMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        find(id);
        repository.delete(id);
    }

    private void requireCampana(Long id) {
        if (!repository.campanaExists(id)) {
            throw BusinessException.of("La campaña con id " + id + " no existe");
        }
    }

    private Gasto find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Gasto", id));
    }
}
