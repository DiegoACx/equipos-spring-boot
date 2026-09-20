package com.quiz.equipos.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.quiz.equipos.entidades.Equipo;
import com.quiz.equipos.repositorio.EquipoRepositorio;


@Controller

public class EquipoWeb {
	
	@Autowired
	private  EquipoRepositorio equiporepositorio;
	
	@GetMapping({"/verequipo","/mostrarequipo","/listarequipo"})
	public String listarEquipo(Model model) {
		List<Equipo> listaEquipo = equiporepositorio.findAll();
		model.addAttribute("listaEquipo", listaEquipo);
		return "verequipo";
	}
	
	@GetMapping("/verequipo/formequipo")
	public String mostrarFormulario (Model model) {
		model.addAttribute("equipo", new Equipo());
		List<Equipo> listaEquipo = equiporepositorio.findAll();
		model.addAttribute("listaEquipo", listaEquipo);

		return "formequipo";
	}
	
	@PostMapping("/guardarequipo")
	public String guardarEquipo (Equipo equipo) {
		equiporepositorio.save(equipo);
		return "redirect:/verequipo";
	}
	
	@GetMapping("/equipo/editar/{id}")
	public String modificarEquipo (@PathVariable("id") Long id, Model model) {
		Equipo equipo = equiporepositorio.findById(id).get();
		model.addAttribute("equipo",equipo);
		List<Equipo> listaEquipo = equiporepositorio.findAll();
		model.addAttribute("listaEquipo", listaEquipo);

		return "formequipo";
		
	}
	
	@GetMapping("/equipo/eliminar/{id}")
	public String eliminarequipo(@PathVariable("id") Long id, Model model) {
		equiporepositorio.deleteById(id);
		return "redirect:/verequipo";
	}
	

}
