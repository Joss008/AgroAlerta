package com.agroalerta.modules.campana.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.campana.dto.CampanaRequest;
import com.agroalerta.modules.campana.dto.CampanaResponse;

public interface CampanaService {

    PageResponse<CampanaResponse> list(String q, Long idParcela, Long idCultivo, int page, int size);

    CampanaResponse get(Long id);

    CampanaResponse create(CampanaRequest request);

    CampanaResponse update(Long id, CampanaRequest request);

    void delete(Long id);
}
