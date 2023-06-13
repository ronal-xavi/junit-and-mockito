package com.ronal.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import com.ronal.models.Examen;

public class DatosUtils {
  public final static List<Examen> EXAMENES =
      Arrays.asList(new Examen(5L,"Matemática"),
          new Examen(6L,"Lenguaje"),
          new Examen(7L, "Historia"));
  public final static List<Examen> EXAMENES_ID_NEGATIVOS =
      Arrays.asList(new Examen(-5L,"Matemática"),
          new Examen(-6L,"Lenguaje"),
          new Examen(null, "Historia"));

  public final static List<Examen> EXAMENES_ID_NULL =
      Arrays.asList(new Examen(null,"Matemática"),
          new Examen(null,"Lenguaje"),
          new Examen(null, "Historia"));

  public final static List<String> PREGUNTAS =
      Arrays.asList("Pregunta1", "Pregunta2");

}

