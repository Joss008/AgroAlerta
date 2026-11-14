package com.agroalerta.modules.cosecha.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.cosecha.dto.CosechaRequest;
import com.agroalerta.modules.cosecha.dto.CosechaResponse;

public interface CosechaService {

    PageResponse<CosechaResponse> list(String q, Long idCampana, int page, int size);

    CosechaResponse get(Long id);

    CosechaResponse create(CosechaRequest request);

    CosechaResponse update(Long id, CosechaRequest request);

    void delete(Long id);
}
