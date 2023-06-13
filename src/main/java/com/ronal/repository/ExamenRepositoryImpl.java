package com.ronal.repository;

import com.ronal.models.Examen;

import java.util.Arrays;
import java.util.List;

public class ExamenRepositoryImpl implements ExamenRepository {
    @Override
    public List<Examen> findAll() {
        return Arrays.asList(
                new Examen(5L, "Matemática"),
                new Examen(6L, "Lenguaje"),
                new Examen(7L, "Historia")
        );
    }

    @Override
    public Examen guardar(Examen examen) {
        return null;
    }
}
