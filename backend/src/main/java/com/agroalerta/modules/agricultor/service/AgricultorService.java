package com.agroalerta.modules.agricultor.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.agricultor.dto.AgricultorRequest;
import com.agroalerta.modules.agricultor.dto.AgricultorResponse;

public interface AgricultorService {

    PageResponse<AgricultorResponse> list(String q, int page, int size);

    AgricultorResponse get(Long id);

    AgricultorResponse create(AgricultorRequest request);

    AgricultorResponse update(Long id, AgricultorRequest request);

    void delete(Long id);
}
