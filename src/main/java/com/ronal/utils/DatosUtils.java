package com.ronal.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ronal.models.Examen;

public class DatosUtils {

  public static final String MATHEMATICA = "Matemática";

  public static final String LENGUAJE = "Lenguaje";

  public static final String HISTORY = "Historia";

  private DatosUtils() {
  }

  public static final List<Examen> EXAMENES = Collections.unmodifiableList
      (Arrays.asList(
          new Examen(5L, MATHEMATICA),
          new Examen(6L, LENGUAJE),
          new Examen(7L, HISTORY)
      ));

  public static final List<Examen> EXAMENES_ID_NEGATIVOS = Collections.unmodifiableList
      (Arrays.asList(
          new Examen(-5L, MATHEMATICA),
          new Examen(-6L, LENGUAJE),
          new Examen(null, HISTORY)
      ));

  public static final List<Examen> EXAMENES_ID_NULL = Collections.unmodifiableList
      (Arrays.asList(
          new Examen(null, MATHEMATICA),
          new Examen(null, LENGUAJE),
          new Examen(null, HISTORY)
      ));

  public static final List<String> PREGUNTAS = Collections.unmodifiableList(
      Arrays.asList("Pregunta1", "Pregunta2"));

}

