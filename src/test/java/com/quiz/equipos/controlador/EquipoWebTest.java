package com.quiz.equipos.controlador;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.quiz.equipos.entidades.Equipo;
import com.quiz.equipos.repositorio.EquipoRepositorio;

@WebMvcTest({ EquipoWeb.class, IndexWeb.class })
class EquipoWebTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private EquipoRepositorio equipoRepositorio;

	private static Equipo equipoConId(Long id) {
		Equipo equipo = new Equipo();
		equipo.setId(id);
		equipo.setNombre("Impresora de recepcion");
		equipo.setCategoria("Impresora");
		equipo.setMarca("HP");
		equipo.setPrecio(850000.0);
		equipo.setEstado("En reparacion");
		equipo.setObservaciones("Bateria reemplazada en 2025");
		return equipo;
	}

	@Test
	void inicioResponde200ConLaVistaIndexEnLasTresRutas() throws Exception {
		for (String ruta : List.of("/", "/index", "/menu")) {
			mockMvc.perform(get(ruta)).andExpect(status().isOk()).andExpect(view().name("index"));
		}
	}

	@Test
	void inicioEnlazaAVerequipoYNoAShow() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(content().string(containsString("href=\"/verequipo\"")))
				.andExpect(content().string(not(containsString("/show"))));
	}

	@Test
	void editarUnIdInexistenteDevuelve404() throws Exception {
		given(equipoRepositorio.findById(999L)).willReturn(Optional.empty());

		mockMvc.perform(get("/equipo/editar/999")).andExpect(status().isNotFound());
	}

	@Test
	void editarUnIdExistenteMuestraElFormularioConElEquipo() throws Exception {
		given(equipoRepositorio.findById(1L)).willReturn(Optional.of(equipoConId(1L)));

		mockMvc.perform(get("/equipo/editar/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("formequipo"))
				.andExpect(model().attributeExists("equipo"));
	}

	@Test
	void guardarUnEquipoInvalidoVuelveAlFormularioConErroresYNoGuarda() throws Exception {
		mockMvc.perform(post("/guardarequipo")
				.param("nombre", "   ")
				.param("categoria", "Laptop")
				.param("marca", "Dell")
				.param("precio", "-5")
				.param("estado", "Nuevo"))
				.andExpect(status().isOk())
				.andExpect(view().name("formequipo"))
				.andExpect(model().attributeHasFieldErrors("equipo", "nombre", "precio"));

		verify(equipoRepositorio, never()).save(any(Equipo.class));
	}

	@Test
	void guardarSinPrecioEsInvalido() throws Exception {
		mockMvc.perform(post("/guardarequipo")
				.param("nombre", "Servidor")
				.param("categoria", "Servidor")
				.param("marca", "Dell")
				.param("estado", "Nuevo"))
				.andExpect(status().isOk())
				.andExpect(view().name("formequipo"))
				.andExpect(model().attributeHasFieldErrors("equipo", "precio"));

		verify(equipoRepositorio, never()).save(any(Equipo.class));
	}

	@Test
	void guardarUnEquipoValidoGuardaYRedirigeALaLista() throws Exception {
		mockMvc.perform(post("/guardarequipo")
				.param("nombre", "Portatil")
				.param("categoria", "Laptop")
				.param("marca", "Lenovo")
				.param("precio", "1500.50")
				.param("estado", "Nuevo"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/verequipo"));

		verify(equipoRepositorio).save(any(Equipo.class));
	}

	@Test
	void eliminarSoloAceptaPostYRedirigeALaLista() throws Exception {
		mockMvc.perform(post("/equipo/eliminar/7"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/verequipo"));
		verify(equipoRepositorio).deleteById(7L);
	}

	@Test
	void eliminarPorGetNoEstaPermitidoYNoBorra() throws Exception {
		mockMvc.perform(get("/equipo/eliminar/7")).andExpect(status().isMethodNotAllowed());

		verify(equipoRepositorio, never()).deleteById(anyLong());
	}

	@Test
	void laListaMuestraObservacionesYBorraConFormularioPost() throws Exception {
		given(equipoRepositorio.findAll()).willReturn(List.of(equipoConId(1L)));

		mockMvc.perform(get("/verequipo"))
				.andExpect(status().isOk())
				.andExpect(view().name("verequipo"))
				.andExpect(content().string(containsString("Bateria reemplazada en 2025")))
				.andExpect(content().string(containsString("action=\"/equipo/eliminar/1\"")))
				.andExpect(content().string(containsString("method=\"post\"")));
	}

	@Test
	void elFormularioAceptaCentavosEnElPrecio() throws Exception {
		mockMvc.perform(get("/verequipo/formequipo"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("step=\"0.01\"")));
	}
}
