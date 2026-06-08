package com.miEquipo.patron_states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.miEquipo.Entidades.Personaje;

public class EstadoSaltando implements PersonajeState {
    private final float GRAVEDAD = -1500f;
    private final float VELOCIDAD_AIRE = 200f;
    private final float NIVEL_SUELO = 50f;

    @Override
    public void manejarEntrada(Personaje personaje) {
        // No permitimos doble salto por ahora
    }

    @Override
    public void actualizar(Personaje personaje, float delta) {
        // 1. Aplicar Gravedad
        personaje.setVelocidadY(personaje.getVelocidadY() + (GRAVEDAD * delta));

        // 2. Control horizontal
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            personaje.setVelocidadX(VELOCIDAD_AIRE);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            personaje.setVelocidadX(-VELOCIDAD_AIRE);
        } else {
            personaje.setVelocidadX(0);
        }

        // 3. Detectar colisión con el suelo SOLO si está cayendo
        if (personaje.getVelocidadY() < 0 && personaje.getY() <= NIVEL_SUELO) {
            personaje.setY(NIVEL_SUELO);
            personaje.setVelocidadY(0);
            personaje.setEstado(new EstadoQuieto());
        }
    }
}
