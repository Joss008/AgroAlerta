package com.agroalerta.modules.agricultor.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.agricultor.dto.AgricultorRequest;
import com.agroalerta.modules.agricultor.dto.AgricultorResponse;
import com.agroalerta.modules.agricultor.entity.Agricultor;
import com.agroalerta.modules.agricultor.mapper.AgricultorMapper;
import com.agroalerta.modules.agricultor.repository.AgricultorRestRepository;
import com.agroalerta.modules.agricultor.service.AgricultorService;
import org.springframework.stereotype.Service;

@Service
public class AgricultorServiceImpl implements AgricultorService {

    private final AgricultorRestRepository repository;

    public AgricultorServiceImpl(AgricultorRestRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResponse<AgricultorResponse> list(String q, int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Agricultor> result = repository.search(query, p, s);
        return PageResponse.of(
                result.content().stream().map(AgricultorMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public AgricultorResponse get(Long id) {
        return AgricultorMapper.toResponse(find(id));
    }

    @Override
    public AgricultorResponse create(AgricultorRequest request) {
        return AgricultorMapper.toResponse(repository.insert(request));
    }

    @Override
    public AgricultorResponse update(Long id, AgricultorRequest request) {
        find(id);
        return AgricultorMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        find(id);
        if (repository.countParcelasByAgricultor(id) > 0) {
            throw BusinessException.conflict(
                    "No se puede eliminar: el agricultor tiene parcelas registradas");
        }
        repository.delete(id);
    }

    private Agricultor find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Agricultor", id));
    }
}
