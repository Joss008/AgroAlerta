package com.agroalerta.modules.agricultor.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agroalerta.common.exception.BusinessException;
import com.agroalerta.modules.agricultor.dto.AgricultorRequest;
import com.agroalerta.modules.agricultor.dto.AgricultorResponse;
import com.agroalerta.modules.agricultor.entity.Agricultor;
import com.agroalerta.modules.agricultor.repository.AgricultorRestRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AgricultorServiceImplTest {

    @Mock
    private AgricultorRestRepository repository;

    @Test
    void bloqueaEliminacionSiTieneParcelas() {
        AgricultorServiceImpl service = new AgricultorServiceImpl(repository);
        Agricultor agricultor = new Agricultor();
        agricultor.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(agricultor));
        when(repository.countParcelasByAgricultor(1L)).thenReturn(3L);

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("parcelas registradas");

        verify(repository, never()).delete(any());
    }

    @Test
    void creaAgricultorNormalizandoEspacios() {
        AgricultorServiceImpl service = new AgricultorServiceImpl(repository);
        AgricultorRequest request =
                new AgricultorRequest("  Juan  ", "Pérez López", "999 888 777", "71234567", null);
        when(repository.insert(any(AgricultorRequest.class))).thenAnswer(inv -> {
            Agricultor agricultor = new Agricultor();
            agricultor.setId(1L);
            return agricultor;
        });

        AgricultorResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(1L);
        verify(repository).insert(request);
    }
}
