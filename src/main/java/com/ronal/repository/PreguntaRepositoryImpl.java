package com.ronal.repository;

import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ronal.utils.DatosUtils;

public class PreguntaRepositoryImpl implements  PreguntaRepository {

    @Override
    public List<String> findPorExamenId(Long id) {
        System.out.println("PreguntaRepositoryImpl.findPorExamenId");
        try {
            TimeUnit.SECONDS.sleep(5);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DatosUtils.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");

    }
}
