package com.agroalerta.modules.parcela.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.parcela.dto.ParcelaRequest;
import com.agroalerta.modules.parcela.dto.ParcelaResponse;

public interface ParcelaService {

    PageResponse<ParcelaResponse> list(String q, Long idAgricultor, int page, int size);

    ParcelaResponse get(Long id);

    ParcelaResponse create(ParcelaRequest request);

    ParcelaResponse update(Long id, ParcelaRequest request);

    void delete(Long id);
}
