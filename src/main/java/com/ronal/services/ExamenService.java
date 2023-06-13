package com.ronal.services;

import com.ronal.models.Examen;

import java.util.Optional;

public interface ExamenService {
   Optional <Examen> findExamenPorNombre(String nombre);
   Examen findExamenPorNombreConPreguntas(String nombre);
   Examen guardar(Examen examen);

}
