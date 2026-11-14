package com.agroalerta.modules.cultivo.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.cultivo.dto.CultivoRequest;
import com.agroalerta.modules.cultivo.dto.CultivoResponse;
import com.agroalerta.modules.cultivo.entity.Cultivo;
import com.agroalerta.modules.cultivo.mapper.CultivoMapper;
import com.agroalerta.modules.cultivo.repository.CultivoRestRepository;
import com.agroalerta.modules.cultivo.service.CultivoService;
import org.springframework.stereotype.Service;

@Service
public class CultivoServiceImpl implements CultivoService {

    private final CultivoRestRepository repository;

    public CultivoServiceImpl(CultivoRestRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<CultivoResponse> list(String q, int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Cultivo> result = repository.search(query, p, s);
        return PageResponse.of(
                result.content().stream().map(CultivoMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public CultivoResponse get(Long id) {
        return CultivoMapper.toResponse(find(id));
    }

    @Override
    public CultivoResponse create(CultivoRequest request) {
        return CultivoMapper.toResponse(repository.insert(request));
    }

    @Override
    public CultivoResponse update(Long id, CultivoRequest request) {
        find(id);
        return CultivoMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        find(id);
        if (repository.countCampanasByCultivo(id) > 0) {
            throw BusinessException.conflict(
                    "No se puede eliminar: el cultivo tiene campañas registradas");
        }
        repository.delete(id);
    }

    private Cultivo find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Cultivo", id));
    }
}
