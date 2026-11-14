package com.agroalerta.modules.venta.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.modules.cosecha.entity.Cosecha;
import com.agroalerta.modules.cosecha.repository.CosechaRestRepository;
import com.agroalerta.modules.venta.dto.VentaRequest;
import com.agroalerta.modules.venta.dto.VentaResponse;
import com.agroalerta.modules.venta.entity.Venta;
import com.agroalerta.modules.venta.repository.VentaRestRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRestRepository ventaRepository;

    @Mock
    private CosechaRestRepository cosechaRepository;

    private VentaServiceImpl service;

    private Cosecha cosecha;

    @BeforeEach
    void setUp() {
        service = new VentaServiceImpl(ventaRepository, cosechaRepository);
        cosecha = new Cosecha();
        cosecha.setId(10L);
        cosecha.setCantidadKg(new BigDecimal("100.00"));
        when(cosechaRepository.findById(10L)).thenReturn(Optional.of(cosecha));
    }

    @Test
    void creaVentaDentroDelDisponible() {
        when(ventaRepository.sumCantidadKgByCosecha(10L)).thenReturn(new BigDecimal("80.00"));
        when(ventaRepository.insert(any(VentaRequest.class))).thenAnswer(inv -> {
            VentaRequest req = inv.getArgument(0);
            Venta venta = new Venta();
            venta.setId(1L);
            venta.setIdCosecha(req.idCosecha());
            venta.setFechaVenta(req.fechaVenta());
            venta.setCantidadKg(req.cantidadKg());
            venta.setPrecioTotal(req.precioTotal());
            venta.setComprador(req.comprador());
            return venta;
        });

        VentaResponse response = service.create(request(new BigDecimal("20.00")));

        assertThat(response.cantidadKg()).isEqualByComparingTo("20.00");
        assertThat(response.idCosecha()).isEqualTo(10L);
        verify(ventaRepository).insert(any(VentaRequest.class));
    }

    @Test
    void rechazaVentaQueSuperaLaCantidadCosechada() {
        when(ventaRepository.sumCantidadKgByCosecha(10L)).thenReturn(new BigDecimal("90.00"));

        assertThatThrownBy(() -> service.create(request(new BigDecimal("20.00"))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("supera la cantidad cosechada");

        verify(ventaRepository, never()).insert(any(VentaRequest.class));
    }

    @Test
    void alActualizarDescuentaLaVentaAnterior() {
        Venta anterior = new Venta();
        anterior.setId(5L);
        anterior.setCantidadKg(new BigDecimal("50.00"));
        when(ventaRepository.findById(5L)).thenReturn(Optional.of(anterior));
        when(ventaRepository.sumCantidadKgByCosecha(10L)).thenReturn(new BigDecimal("50.00"));
        when(ventaRepository.update(eq(5L), any(VentaRequest.class))).thenAnswer(inv -> {
            VentaRequest req = inv.getArgument(1);
            Venta venta = new Venta();
            venta.setId(5L);
            venta.setIdCosecha(req.idCosecha());
            venta.setFechaVenta(req.fechaVenta());
            venta.setCantidadKg(req.cantidadKg());
            venta.setPrecioTotal(req.precioTotal());
            venta.setComprador(req.comprador());
            return venta;
        });

        VentaResponse response = service.update(5L, request(new BigDecimal("50.00")));

        assertThat(response.cantidadKg()).isEqualByComparingTo("50.00");
        verify(ventaRepository).update(eq(5L), any(VentaRequest.class));
    }

    private VentaRequest request(BigDecimal cantidad) {
        return new VentaRequest(10L, LocalDate.of(2026, 5, 10), cantidad,
                new BigDecimal("350.00"), "Cooperativa Cañete");
    }
}
