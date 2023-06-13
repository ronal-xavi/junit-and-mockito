package com.ronal.services;

import com.ronal.models.Examen;
import com.ronal.repository.ExamenRepository;
import com.ronal.repository.PreguntaRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    ExamenRepository examenRepository;

    @Mock
    PreguntaRepository preguntaRepository;

    @InjectMocks
    ExamenServiceImpl examenService;

    Examen examen;

    @BeforeEach
    void beforeEach() {
//        MockitoAnnotations.openMocks(this);
        this.createExamen();

    }

    @Test
    @DisplayName("Método findAll del Servicio OK")
    void findExamenPorNombre() {
        List<Examen> examenList = Arrays.asList(
                new Examen(5L, "Matemática"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historia")
        );

        Mockito.when(this.examenRepository.findAll()).thenReturn(examenList);

        Optional<Examen> examen = this.examenService.findExamenPorNombre("Matemática");

        Assertions.assertAll(
                () -> Assertions.assertNotNull(examen),
                () -> Assertions.assertEquals(5L, examen.orElseThrow(null).getId()),
                () -> Assertions.assertEquals("Matemática", examen.orElseThrow(null).getNombre())
        );
    }

    @Test
    @DisplayName("Método findAll del Servicio KO")
    void findExamenPorNombre_KO() {

        Mockito.when(this.examenRepository.findAll()).thenReturn(Collections.emptyList());

        Optional<Examen> examen = this.examenService.findExamenPorNombre("Matemática");

        Assertions.assertAll(
                () -> Assertions.assertFalse(examen.isPresent())
        );
    }

    @Test
    void testPreguntasExamen() {

        List<Examen> examenList = Arrays.asList(
                new Examen(5L, "Matemática"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historia")
        );

        Mockito.when(this.examenRepository.findAll())
                .thenReturn(examenList);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong()))
                .thenReturn(Arrays.asList("Pregunta1", "Pregunta2", "Pregunta3"));

        Examen result = this.examenService.findExamenPorNombreConPreguntas("Historia");

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertFalse(CollectionUtils.isEmpty(result.getPreguntas())),
                () -> Assertions.assertEquals(3, CollectionUtils.size(result.getPreguntas())),
                () -> Mockito.verify(this.examenRepository).findAll(),
                () -> Mockito.verify(this.preguntaRepository).findPorExamenId(Mockito.anyLong())
        );
    }

    @Test
    void testGuardarExamen() {
        this.examen.setId(null);
        /* Para id autoincrementados */
        // Given -> precondiciones en el entorno de prueba
        Mockito.when(this.examenRepository.guardar(Mockito.any(Examen.class)))
                .then(new Answer<Examen>() {
                    Long secuencia = 8L;

                    @Override
                    public Examen answer(InvocationOnMock invocationOnMock) {
                        Examen examen = invocationOnMock.getArgument(0);
                        examen.setId(secuencia++);
                        return examen;
                    }
                });

        //When -> Cuando
        Examen result =
                this.examenService.guardar(this.examen);

        //Then -> Verificamos
        Assertions.assertAll(
                () -> Assertions.assertNotNull(result.getId()),
                () -> Assertions.assertEquals(8L, result.getId()),
                () -> Assertions.assertEquals("Física", result.getNombre()),
                () -> Mockito.verify(this.examenRepository).guardar(Mockito.any(Examen.class))
        );

    }

    @Test
    void testManejoDeException() {
        this.examen.setNombre("Matemática");
        Mockito.when(this.examenRepository.findAll())
                .thenReturn(Collections.singletonList(this.examen));

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong()))
                .thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.examenService.findExamenPorNombreConPreguntas("Matemática");
        });

    }

    void createExamen() {
        this.examen = new Examen();
        examen.setId(1L);
        examen.setNombre("Física");

    }


}