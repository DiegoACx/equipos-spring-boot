package com.quiz.equipos.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name="equipos")

public class Equipo {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    private String observaciones;

    
    public Long getId() { 
    	return Id;
    }
    public void setId(Long id) { 
    	Id = id;
    }

    public String getNombre() { 
    	return nombre; 
    }
    public void setNombre(String nombre) { 
    	this.nombre = nombre;
    }

    public String getCategoria() { 
    	return categoria; 
    }
    public void setCategoria(String categoria) { 
    	this.categoria = categoria; 
    }

    public String getMarca() { 
    	return marca; 
    }
    public void setMarca(String marca) { 
    	this.marca = marca; 
    }

    public Double getPrecio() { 
    	return precio; 
    }
    public void setPrecio(Double precio) { 
    	this.precio = precio;
    }

    public String getEstado() {
    	return estado; 
    }
    public void setEstado(String estado) {
    	this.estado = estado;
    }

    public String getObservaciones() {
    	return observaciones;
    }
    public void setObservaciones(String observaciones) { 
    	this.observaciones = observaciones; 
    }
}
