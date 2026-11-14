package com.agroalerta.modules.dashboard.service;

import com.agroalerta.modules.dashboard.dto.GastosResumen;
import com.agroalerta.modules.dashboard.dto.ProduccionRow;
import com.agroalerta.modules.dashboard.dto.ResumenResponse;
import com.agroalerta.modules.dashboard.dto.RentabilidadRow;
import com.agroalerta.modules.dashboard.dto.VentasRow;
import java.util.List;

public interface DashboardService {

    ResumenResponse resumen();

    List<ProduccionRow> produccion();

    List<VentasRow> ventas();

    GastosResumen gastos();

    List<RentabilidadRow> rentabilidad();
}
