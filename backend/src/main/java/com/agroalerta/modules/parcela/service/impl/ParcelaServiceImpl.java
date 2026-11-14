package com.agroalerta.modules.parcela.service.impl;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.common.exception.ResourceNotFoundException;
import com.agroalerta.common.response.PageResponse;
import com.agroalerta.common.util.Pagination;
import com.agroalerta.modules.agricultor.repository.AgricultorRestRepository;
import com.agroalerta.modules.parcela.dto.ParcelaRequest;
import com.agroalerta.modules.parcela.dto.ParcelaResponse;
import com.agroalerta.modules.parcela.entity.Parcela;
import com.agroalerta.modules.parcela.mapper.ParcelaMapper;
import com.agroalerta.modules.parcela.repository.ParcelaRestRepository;
import com.agroalerta.modules.parcela.service.ParcelaService;
import org.springframework.stereotype.Service;

@Service
public class ParcelaServiceImpl implements ParcelaService {

    private final ParcelaRestRepository repository;
    private final AgricultorRestRepository agricultorRepository;

    public ParcelaServiceImpl(ParcelaRestRepository repository,
                              AgricultorRestRepository agricultorRepository) {
        this.repository = repository;
        this.agricultorRepository = agricultorRepository;
    }

    @Override
    public PageResponse<ParcelaResponse> list(String q, Long idAgricultor, int page, int size) {
        int p = Pagination.page(page);
        int s = Pagination.size(size);
        String query = (q == null || q.isBlank()) ? null : q.trim();
        PageResponse<Parcela> result = repository.search(query, idAgricultor, p, s);
        return PageResponse.of(
                result.content().stream().map(ParcelaMapper::toResponse).toList(),
                result.page(), result.size(), result.totalElements());
    }

    @Override
    public ParcelaResponse get(Long id) {
        return ParcelaMapper.toResponse(find(id));
    }

    @Override
    public ParcelaResponse create(ParcelaRequest request) {
        requireAgricultor(request.idAgricultor());
        return ParcelaMapper.toResponse(repository.insert(request));
    }

    @Override
    public ParcelaResponse update(Long id, ParcelaRequest request) {
        find(id);
        requireAgricultor(request.idAgricultor());
        return ParcelaMapper.toResponse(repository.update(id, request));
    }

    @Override
    public void delete(Long id) {
        find(id);
        if (repository.countCampanasByParcela(id) > 0) {
            throw BusinessException.conflict(
                    "No se puede eliminar: la parcela tiene campañas registradas");
        }
        repository.delete(id);
    }

    private void requireAgricultor(Long idAgricultor) {
        agricultorRepository.findById(idAgricultor)
                .orElseThrow(() -> BusinessException.of(
                        "El agricultor con id " + idAgricultor + " no existe"));
    }

    private Parcela find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Parcela", id));
    }
}
