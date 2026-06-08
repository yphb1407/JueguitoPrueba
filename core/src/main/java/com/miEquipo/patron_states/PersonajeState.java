package com.miEquipo.patron_states;

import com.miEquipo.Entidades.Personaje;

public interface PersonajeState {
    void manejarEntrada(Personaje personaje);
    void actualizar(Personaje personaje, float delta);
}
