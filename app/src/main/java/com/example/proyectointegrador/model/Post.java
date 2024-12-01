package com.example.proyectointegrador.model;

import java.util.List;

public class Post {
    private String titulo, descripcion, categoria;
    private int duracion;
    private double presupuesto;
    private List<String> imagenes;

    public Post(String titulo, String descripcion, String categoria, int duracion, double presupuesto) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.duracion = duracion;
        this.presupuesto = presupuesto;
    }

    public Post(String titulo, String descripcion, String categoria, int duracion, double presupuesto, List<String> imagenes) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.duracion = duracion;
        this.presupuesto = presupuesto;
        this.imagenes = imagenes;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }
}
