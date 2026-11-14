package com.agroalerta.modules.gasto.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.gasto.dto.GastoRequest;
import com.agroalerta.modules.gasto.dto.GastoResponse;

public interface GastoService {

    PageResponse<GastoResponse> list(String q, Long idCampana, int page, int size);

    GastoResponse get(Long id);

    GastoResponse create(GastoRequest request);

    GastoResponse update(Long id, GastoRequest request);

    void delete(Long id);
}
