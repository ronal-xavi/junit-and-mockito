package com.ronal.services;

import static com.ronal.utils.DatosUtils.EXAMENES;
import static com.ronal.utils.DatosUtils.MATHEMATICA;
import static com.ronal.utils.DatosUtils.PREGUNTAS;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.ronal.models.Examen;
import com.ronal.repository.ExamenRepository;
import com.ronal.repository.ExamenRepositoryImpl;
import com.ronal.repository.PreguntaRepository;
import com.ronal.repository.PreguntaRepositoryImpl;
import com.ronal.utils.DatosUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    @Spy
    ExamenRepositoryImpl examenRepository;

    @Spy
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl examenService;




    /**
     * Los spy no son 100% métodos reales, si no son un híbrido entre un objeto real y un mock.
     * */
    @Test
    void testSpy_and_doReturn() {
//        ExamenRepository examenRepository1 = Mockito.spy(ExamenRepositoryImpl.class);
//        PreguntaRepository preguntaRepository1 = Mockito.spy(PreguntaRepositoryImpl.class);
//        ExamenService examenService1 = new ExamenServiceImpl(examenRepository1, preguntaRepository1);
//

        List<String> preguntas = Collections.singletonList("aritmética");

        //Cuando usamos spy y usamos when se hace el llamado al método real, para evitar esto usamos doReturn.
        //Mockito.when(preguntaRepository1.findPorExamenId(Mockito.anyLong())).thenReturn(preguntas);

        //De esta forma ya no se hace el llamado real al método.
        Mockito.doReturn(preguntas).when(this.preguntaRepository).findPorExamenId(Mockito.anyLong());


        Examen examen1 = this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Assertions.assertEquals(5L, examen1.getId());
        Assertions.assertEquals("Matemática", examen1.getNombre());


    }



}