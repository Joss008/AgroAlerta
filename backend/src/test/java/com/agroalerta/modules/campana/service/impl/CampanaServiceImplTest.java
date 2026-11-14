package com.agroalerta.modules.campana.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.modules.campana.dto.CampanaRequest;
import com.agroalerta.modules.campana.dto.CampanaResponse;
import com.agroalerta.modules.campana.entity.Campana;
import com.agroalerta.modules.campana.repository.CampanaRestRepository;
import com.agroalerta.modules.cultivo.entity.Cultivo;
import com.agroalerta.modules.cultivo.repository.CultivoRestRepository;
import com.agroalerta.modules.parcela.entity.Parcela;
import com.agroalerta.modules.parcela.repository.ParcelaRestRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CampanaServiceImplTest {

    @Mock
    private CampanaRestRepository campanaRepository;

    @Mock
    private ParcelaRestRepository parcelaRepository;

    @Mock
    private CultivoRestRepository cultivoRepository;

    private CampanaServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CampanaServiceImpl(campanaRepository, parcelaRepository, cultivoRepository);

        Parcela parcela = new Parcela();
        parcela.setId(1L);
        parcela.setNombre("Parcela Norte");
        lenient().when(parcelaRepository.findById(1L)).thenReturn(Optional.of(parcela));

        Cultivo cultivo = new Cultivo();
        cultivo.setId(2L);
        cultivo.setNombre("Espárrago");
        lenient().when(cultivoRepository.findById(2L)).thenReturn(Optional.of(cultivo));
    }

    @Test
    void creaCampanaValida() {
        when(campanaRepository.insert(any(CampanaRequest.class))).thenAnswer(inv -> {
            CampanaRequest request = inv.getArgument(0);
            Campana campana = new Campana();
            campana.setId(7L);
            campana.setIdParcela(request.idParcela());
            campana.setIdCultivo(request.idCultivo());
            campana.setNombre(request.nombre());
            campana.setFechaInicio(request.fechaInicio());
            campana.setFechaFin(request.fechaFin());
            campana.setNombreParcela(
                    parcelaRepository.findById(request.idParcela()).orElseThrow().getNombre());
            campana.setNombreCultivo(
                    cultivoRepository.findById(request.idCultivo()).orElseThrow().getNombre());
            return campana;
        });

        CampanaResponse response = service.create(new CampanaRequest(1L, 2L, "Campaña 2026",
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 8, 31)));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.nombreParcela()).isEqualTo("Parcela Norte");
        assertThat(response.nombreCultivo()).isEqualTo("Espárrago");
    }

    @Test
    void rechazaFechaFinAnteriorAInicio() {
        assertThatThrownBy(() -> service.create(new CampanaRequest(1L, 2L, "Campaña inválida",
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 1))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("fecha fin");

        verify(campanaRepository, never()).insert(any());
    }

    @Test
    void aceptaCampanaSinFechaFin() {
        when(campanaRepository.insert(any(CampanaRequest.class))).thenAnswer(inv -> {
            CampanaRequest request = inv.getArgument(0);
            Campana campana = new Campana();
            campana.setId(7L);
            campana.setNombre(request.nombre());
            campana.setFechaInicio(request.fechaInicio());
            campana.setFechaFin(request.fechaFin());
            return campana;
        });

        CampanaResponse response = service.create(new CampanaRequest(1L, 2L, "Campaña abierta",
                LocalDate.of(2026, 5, 1), null));

        assertThat(response.fechaFin()).isNull();
    }

    @Test
    void bloqueaEliminacionConRegistrosAsociados() {
        Campana campana = new Campana();
        campana.setId(9L);
        when(campanaRepository.findById(9L)).thenReturn(Optional.of(campana));
        when(campanaRepository.countRiegosByCampana(9L)).thenReturn(2L);

        assertThatThrownBy(() -> service.delete(9L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("No se puede eliminar");

        verify(campanaRepository, never()).delete(any());
    }
}
