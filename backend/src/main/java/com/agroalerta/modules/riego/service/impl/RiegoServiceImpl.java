package com.agroalerta.modules.riego.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.campana.repository.CampanaRestRepository;
import com.agroalerta.modules.riego.dto.RiegoRequest;
import com.agroalerta.modules.riego.dto.RiegoResponse;
import com.agroalerta.modules.riego.entity.Riego;
import com.agroalerta.modules.riego.mapper.RiegoMapper;
import com.agroalerta.modules.riego.repository.RiegoRestRepository;
import com.agroalerta.modules.riego.service.RiegoService;
import org.springframework.stereotype.Service;

@Service
public class RiegoServiceImpl implements RiegoService {

    private final RiegoRestRepository repository;
    private final CampanaRestRepository campanaRepository;

    public RiegoServiceImpl(RiegoRestRepository repository,
                            CampanaRestRepository campanaRepository) {
        this.repository = repository;
        this.campanaRepository = campanaRepository;
    }

    @Override
    public PageResponse<RiegoResponse> list(String q, Long idCampana, int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Riego> result = repository.search(query, idCampana, p, s);
        return PageResponse.of(
                result.content().stream().map(RiegoMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public RiegoResponse get(Long id) {
        return RiegoMapper.toResponse(find(id));
    }

    @Override
    public RiegoResponse create(RiegoRequest request) {
        requireCampana(request.idCampana());
        return RiegoMapper.toResponse(repository.insert(request));
    }

    @Override
    public RiegoResponse update(Long id, RiegoRequest request) {
        find(id);
        requireCampana(request.idCampana());
        return RiegoMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        repository.delete(find(id).getId());
    }

    private void requireCampana(Long id) {
        campanaRepository.findById(id)
                .orElseThrow(() -> BusinessException.of("La campaña con id " + id + " no existe"));
    }

    private Riego find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Riego", id));
    }
}
