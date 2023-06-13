package com.ronal.services;

import com.ronal.models.Examen;
import com.ronal.repository.ExamenRepository;
import com.ronal.repository.PreguntaRepository;
import com.ronal.utils.DatosUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
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

    /**
     * Con thenThrow lanzamos una excepción.
     * Para poder comprobar la excepción podemos usar el assertThrows haciendo referencia al método.
     * Para mandar parámetros null podemos utilizar el Mockito.isNull(). Es mejor.
     */
    @Test
    void testManejoDeException() {
        this.examen.setNombre("Matemática");

        Mockito.when(this.examenRepository.findAll())
                .thenReturn(DatosUtils.EXAMENES_ID_NULL);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.isNull()))
                .thenThrow(IllegalArgumentException.class);

        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.examenService.findExamenPorNombreConPreguntas("Matemática");
        });

        Assertions.assertEquals(IllegalArgumentException.class,exception.getClass());

    }

    /**
     * ArgumentMatches = sirve para hacer nuestras validaciones de forma más personalizada
     * lo usamos en el Mockito.verify.
     * Esto es para parámetros de métodos.
     */

    @Test
    void testArgumentMatches() {
        Mockito.when(examenRepository.findAll())
            .thenReturn(DatosUtils.EXAMENES);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong()))
            .thenReturn(DatosUtils.PREGUNTAS);

        this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPorExamenId(Mockito.argThat(x -> x != null && x.equals(5L)));

    }
    /**
    * ArgumentMatches personalizados con clases:
    * tenemos que crear una clase personalizada donde hacemos la validación
    * y retornamos un mensaje de error.
    * */
    @Test
    void testArgumentMatches_con_clase_personalizada() {
        Mockito.when(examenRepository.findAll())
            .thenReturn(DatosUtils.EXAMENES_ID_NEGATIVOS);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong()))
            .thenReturn(DatosUtils.PREGUNTAS);

        this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPorExamenId(Mockito.argThat(new MiArgsMatchers()));

    }

    void createExamen() {
        this.examen = new Examen();
        examen.setId(1L);
        examen.setNombre("Física");

    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long>{

        private Long arg;
        @Override
        public boolean matches(Long aLong) {
            this.arg = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return String.format("Es para un mensaje personalizado de error que imprime"
                + " mockito en caso de que falle algun test"
                + "debe ser un entero positivo :%s ", arg);
        }
    }


}