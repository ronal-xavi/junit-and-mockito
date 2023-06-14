package com.ronal.repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ronal.models.Examen;
import com.ronal.utils.DatosUtils;

public class ExamenRepositoryImpl implements ExamenRepository {

    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
        try {
            System.out.println("ExamenRepositoryImpl");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DatosUtils.EXAMENES;
    }

    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return DatosUtils.EXAMENES.get(0);
    }
}
