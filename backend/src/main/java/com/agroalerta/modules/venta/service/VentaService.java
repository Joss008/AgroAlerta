package com.agroalerta.modules.venta.service;

import com.agroalerta.common.response.PageResponse;
import com.agroalerta.modules.venta.dto.VentaRequest;
import com.agroalerta.modules.venta.dto.VentaResponse;

public interface VentaService {

    PageResponse<VentaResponse> list(String q, Long idCosecha, int page, int size);

    VentaResponse get(Long id);

    VentaResponse create(VentaRequest request);

    VentaResponse update(Long id, VentaRequest request);

    void delete(Long id);
}
