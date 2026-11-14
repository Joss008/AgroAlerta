package com.agroalerta.modules.cultivo.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.cultivo.dto.CultivoRequest;
import com.agroalerta.modules.cultivo.dto.CultivoResponse;

public interface CultivoService {

    PageResponse<CultivoResponse> list(String q, int page, int size);

    CultivoResponse get(Long id);

    CultivoResponse create(CultivoRequest request);

    CultivoResponse update(Long id, CultivoRequest request);

    void delete(Long id);
}
