package com.quiz.equipos.repositorio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.quiz.equipos.entidades.Equipo;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
class EquipoRepositorioTest {

	@Autowired
	private EquipoRepositorio repositorio;

	private static Equipo equipoValido(String nombre) {
		Equipo equipo = new Equipo();
		equipo.setNombre(nombre);
		equipo.setCategoria("Laptop");
		equipo.setMarca("Lenovo");
		equipo.setPrecio(2500000.50);
		equipo.setEstado("Nuevo");
		equipo.setObservaciones("Sin observaciones");
		return equipo;
	}

	@Test
	void guardaYRecuperaUnEquipoConIdGenerado() {
		Equipo guardado = repositorio.save(equipoValido("Portatil contabilidad"));

		assertThat(guardado.getId()).isNotNull();
		Equipo leido = repositorio.findById(guardado.getId()).orElseThrow();
		assertThat(leido.getNombre()).isEqualTo("Portatil contabilidad");
		assertThat(leido.getPrecio()).isEqualTo(2500000.50);
		assertThat(leido.getObservaciones()).isEqualTo("Sin observaciones");
	}

	@Test
	void findAllDevuelveTodosLosEquiposGuardados() {
		repositorio.save(equipoValido("Equipo A"));
		repositorio.save(equipoValido("Equipo B"));

		assertThat(repositorio.findAll()).extracting(Equipo::getNombre).containsExactlyInAnyOrder("Equipo A", "Equipo B");
	}

	@Test
	void deleteByIdEliminaElEquipo() {
		Long id = repositorio.save(equipoValido("Servidor de pruebas")).getId();

		repositorio.deleteById(id);

		assertThat(repositorio.findById(id)).isEmpty();
	}

	@Test
	void findByIdDeUnIdInexistenteDevuelveVacio() {
		assertThat(repositorio.findById(999L)).isEmpty();
	}

	@Test
	void noPersisteUnEquipoInvalido() {
		Equipo invalido = equipoValido("   ");
		invalido.setPrecio(-10.0);

		assertThatThrownBy(() -> repositorio.saveAndFlush(invalido)).isInstanceOf(ConstraintViolationException.class);
	}
}
