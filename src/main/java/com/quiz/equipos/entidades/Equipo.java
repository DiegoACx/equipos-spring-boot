package com.quiz.equipos.entidades;

import jakarta.persistence.*;

@Entity
@Table(name="equipos")

public class Equipo {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;
	
    private String nombre;
    
    private String categoria;
    
    private String marca;
    
    private Double precio;
    
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
