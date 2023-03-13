package com.actividades.gestortareas;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.time.format.DateTimeFormatter;

public class Tarea {

    @BsonProperty(value = "_id")
    private ObjectId id;
    @BsonProperty(value = "fecha")
    private String fecha;
    @BsonProperty(value = "descripcion")
    private String descripcion;
    @BsonProperty(value = "finalizada")
    private Boolean finalizada;

    public final static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Tarea() {

    }

    public Tarea(String fecha, String descripcion, boolean finalizada) {
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.finalizada = finalizada;
    }

    public ObjectId getId() {
        return id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getFinalizada() {
        return finalizada;
    }

    public void setFinalizada(Boolean finalizada) {
        this.finalizada = finalizada;
    }
}
