package com.ronal.services;

import static com.ronal.utils.DatosUtils.EXAMENES;
import static com.ronal.utils.DatosUtils.MATHEMATICA;
import static com.ronal.utils.DatosUtils.PREGUNTAS;

import java.util.ArrayList;
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
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    ExamenRepositoryImpl examenRepository;

    @Mock
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
    ExamenServiceImpl examenService;

    @Captor
    ArgumentCaptor<Long> captor;

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
     * Con thenThrow lanzamos una excepción. Para poder comprobar la excepción podemos usar el assertThrows haciendo referencia al método.
     * Para mandar parámetros null podemos utilizar el Mockito.isNull(). Es mejor.
     */
    @Test
    void testManejoDeException() {
        this.examen.setNombre("Matemática");

        Mockito.when(this.examenRepository.findAll())
            .thenReturn(DatosUtils.EXAMENES_ID_NULL);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.isNull()))
            .thenThrow(IllegalArgumentException.class);

        final IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
            this.examenService.findExamenPorNombreConPreguntas("Matemática")
        );

        Assertions.assertEquals(IllegalArgumentException.class, exception.getClass());

    }

    /**
     * ArgumentMatches = sirve para hacer nuestras validaciones de forma más personalizada lo usamos en el Mockito.verify. Esto es para
     * parámetros de métodos.
     */
    @Test
    void testArgumentMatches() {
        Mockito.when(examenRepository.findAll())
            .thenReturn(EXAMENES);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong()))
            .thenReturn(PREGUNTAS);

        this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPorExamenId(Mockito.argThat(x -> x != null && x.equals(5L)));

    }

    /**
     * ArgumentMatches personalizados con clases: tenemos que crear una clase personalizada donde hacemos la validación y retornamos un
     * mensaje de error.
     */
    @Test
    void testArgumentMatches_con_clase_personalizada() {
        Mockito.when(examenRepository.findAll())
            .thenReturn(EXAMENES);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong()))
            .thenReturn(PREGUNTAS);

        this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Mockito.verify(examenRepository).findAll();
        Mockito.verify(preguntaRepository).findPorExamenId(Mockito.argThat(new MiArgsMatchers()));

    }

    /**
     * Capturando Argumentos con Argument Capture En este ejemplo estamos capturando el valor que se le pasa al
     * preguntaRepository.findPorExamenId estamos capturando el valor de su parámetro y lo estamos usando para verificar su valor con un
     * equals. Este Captor lo utilizamos con anotación, pero también podemos utilizarlo instantiation de forma explícita. * Podemos obtener
     * el valor y hacer algo con él.
     */
    @Test
    void testArgumentCaptor() {
        Mockito.when(examenRepository.findAll())
            .thenReturn(EXAMENES);

        Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong()))
            .thenReturn(PREGUNTAS);

        examenService.findExamenPorNombreConPreguntas("Matemática");

        //    ArgumentCaptor<Long> captor= ArgumentCaptor.forClass(Long.class);
        Mockito.verify(this.preguntaRepository).findPorExamenId(captor.capture());
        Assertions.assertEquals(5L, captor.getValue());
    }

    /**
     * doThrow => nos sirve para lanzar excepciones cuando se llama a un método void. Se verifica la excepción como si fuera un thenThrow
     * igual
     */
    @Test
    void testDoThrow_paraMetodosVoid() {
        Examen examen1 = EXAMENES.get(1);
        examen1.setPreguntas(PREGUNTAS);

        Mockito.doThrow(IllegalArgumentException.class).when(this.preguntaRepository).guardarVarias(Mockito.anyList());

        Assertions.assertThrows(IllegalArgumentException.class, () -> examenService.guardar(examen1));

    }

    /**
     * El doAnswer es para hacer validaciones más complejas a los parámetros.
     */
    @Test
    void testDoAswer() {
        Mockito.when(this.examenRepository.findAll())
            .thenReturn(EXAMENES);

        //     Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong())).thenReturn(PREGUNTAS);
        Mockito.doAnswer(x -> {
            Long id = x.getArgument(0);
            return id == 5L ? PREGUNTAS : null;
        }).when(preguntaRepository).findPorExamenId(Mockito.anyLong());

        Examen result =
            this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getPreguntas().contains("Pregunta1"));
        Assertions.assertEquals(MATHEMATICA, result.getNombre());
    }

    /**
     * Otra Forma de usar el doAnswer
     */
    @Test
    void testGuardarExamen_doAnswer() {
        this.examen.setId(null);
        Mockito.doAnswer(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(this.examenRepository).guardar(Mockito.any(Examen.class));

        Examen result =
            this.examenService.guardar(this.examen);

        Assertions.assertAll(
            () -> Assertions.assertNotNull(result.getId()),
            () -> Assertions.assertEquals(8L, result.getId()),
            () -> Assertions.assertEquals("Física", result.getNombre()),
            () -> Mockito.verify(this.examenRepository).guardar(Mockito.any(Examen.class))
        );

    }

    /**
     * doCallRealMethod => llamando al método real y no a un mock.
     */
    @Test
    void testDoCallRealMethod() {
        Mockito.when(this.examenRepository.findAll())
            .thenReturn(EXAMENES);

        //Mockito.when(this.preguntaRepository.findPorExamenId(Mockito.anyLong())).thenReturn(PREGUNTAS);

        Mockito.doCallRealMethod().when(this.preguntaRepository).findPorExamenId(Mockito.anyLong());

        Examen examen = this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Assertions.assertEquals(5L, examen.getId());
        Assertions.assertEquals("Matemática", examen.getNombre());

    }

    /**
     * Los epy no son 100% métodos reales, si no son un híbrido entre un objeto real y un mock.
     * */
    @Test
    void testSpy_and_doReturn() {
        ExamenRepository examenRepository1 = Mockito.spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository1 = Mockito.spy(PreguntaRepositoryImpl.class);
        ExamenService examenService1 = new ExamenServiceImpl(examenRepository1, preguntaRepository1);


        List<String> preguntas = Collections.singletonList("aritmética");

        //Cuando usamos spy y usamos when se hace el llamado al método real, para evitar esto usamos doReturn.
        //Mockito.when(preguntaRepository1.findPorExamenId(Mockito.anyLong())).thenReturn(preguntas);

        //De esta forma ya no se hace el llamado real al método.
        Mockito.doReturn(preguntas).when(preguntaRepository1).findPorExamenId(Mockito.anyLong());


        Examen examen1 = examenService1.findExamenPorNombreConPreguntas("Matemática");

        Assertions.assertEquals(5L, examen1.getId());
        Assertions.assertEquals("Matemática", examen1.getNombre());


    }

    /**
     * Nos permite verificar el orden de ejecución de los métodos.
     * Mockito.inOrder();
     */
    @Test
    void testOrdenDeInvocaciones() {
        Mockito.when(this.examenRepository.findAll()).thenReturn(EXAMENES);

        this.examenService.findExamenPorNombreConPreguntas("Matemática");
        this.examenService.findExamenPorNombreConPreguntas("Lenguaje");

        final InOrder inOrder = Mockito.inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findPorExamenId(5L);
        inOrder.verify(preguntaRepository).findPorExamenId(6L);
    }

    @Test
    void testOrdenDeInvocaciones2() {
        Mockito.when(this.examenRepository.findAll()).thenReturn(EXAMENES);

        this.examenService.findExamenPorNombreConPreguntas("Matemática");
        this.examenService.findExamenPorNombreConPreguntas("Lenguaje");

        // Podemos pasar varios parámetros
        final InOrder inOrder = Mockito.inOrder(examenRepository, preguntaRepository);
        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPorExamenId(5L);
        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPorExamenId(6L);
    }

    /**
     * Verificamos cuantas veces puede ser llamado un método.
     * */
    @Test
    void testNumberDeInvocaciones() {
        Mockito.when(this.examenRepository.findAll()).thenReturn(EXAMENES);
        this.examenService.findExamenPorNombreConPreguntas("Matemática");

        //Verificar que se ejecute 1 vez
        Mockito.verify(this.preguntaRepository,Mockito.times(1)).findPorExamenId(5L);
        //Verificar que se ejecute al menos 1 vez
        Mockito.verify(this.preguntaRepository,Mockito.atLeast(1)).findPorExamenId(5L);
        //Verificar que se ejecute al menos 1 vez de forma estática
        Mockito.verify(this.preguntaRepository,Mockito.atLeastOnce()).findPorExamenId(5L);
        // Verificar que se ejecute como máximo 10 veces
        Mockito.verify(this.preguntaRepository,Mockito.atMost(10)).findPorExamenId(5L);
        // Verificar que se ejecute como máximo 1 vez
        Mockito.verify(this.preguntaRepository,Mockito.atMostOnce()).findPorExamenId(5L);

    }

    /**
     * Verificamos que el método nunca se llame
     */
    @Test
    void testNever() {
        Mockito.when(this.examenRepository.findAll()).thenReturn(new ArrayList<>());
        this.examenService.findExamenPorNombreConPreguntas("Matemática");

        Mockito.verify(this.preguntaRepository,Mockito.never()).findPorExamenId(Mockito.anyLong());

    }

    void createExamen() {
        this.examen = new Examen();
        examen.setId(1L);
        examen.setNombre("Física");

    }

    /**
     * Clase ArgumentMatcher para validar los ID = Long
     */
    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long arg;

        @Override
        public boolean matches(Long aLong) {
            this.arg = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return String.format("Es para un mensaje personalizado de error que imprime"
                + " mockito en caso de que falle algún test"
                + "debe ser un entero positivo :%s ", arg);
        }
    }

}