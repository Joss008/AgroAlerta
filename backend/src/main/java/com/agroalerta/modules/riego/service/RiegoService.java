package com.agroalerta.modules.riego.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.riego.dto.RiegoRequest;
import com.agroalerta.modules.riego.dto.RiegoResponse;

public interface RiegoService {

    PageResponse<RiegoResponse> list(String q, Long idCampana, int page, int size);

    RiegoResponse get(Long id);

    RiegoResponse create(RiegoRequest request);

    RiegoResponse update(Long id, RiegoRequest request);

    void delete(Long id);
}
