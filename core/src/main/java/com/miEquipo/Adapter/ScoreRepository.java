package com.miEquipo.Adapter;

public interface ScoreRepository {
    void guardarRecord(String nombre, int puntuacion);
    int cargarRecord();
}
