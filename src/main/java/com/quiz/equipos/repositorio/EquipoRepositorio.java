package com.quiz.equipos.repositorio;

import com.quiz.equipos.entidades.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipoRepositorio extends JpaRepository<Equipo, Long> {
}
