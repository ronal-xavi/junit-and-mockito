package com.ronal.repository;

import java.util.List;

public interface PreguntaRepository {
    List<String> findPorExamenId(Long id);
    void guardarVarias(List<String> preguntas);
}
