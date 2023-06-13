package com.ronal.services;

import com.ronal.models.Examen;
import com.ronal.repository.ExamenRepository;
import com.ronal.repository.PreguntaRepository;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return examenRepository.findAll()
                .stream()
                .filter(e -> e.getNombre().contains(nombre))
                .findFirst();
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = this.findExamenPorNombre(nombre);
        Examen examen = null;

            if (examenOptional.isPresent()) {
                examen = examenOptional.get();
                List<String> preguntas = this.preguntaRepository.findPorExamenId(examen.getId());
                examen.setPreguntas(preguntas);
            }

        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if (!CollectionUtils.isEmpty(examen.getPreguntas())) {
            this.preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return this.examenRepository.guardar(examen);
    }
}
