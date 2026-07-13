package com.miEquipo.patron_states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.miEquipo.Entidades.Personaje;

public class EstadoCaminando implements PersonajeState {
    private final float VELOCIDAD_CAMINAR = 200f;

    @Override
    public void manejarEntrada(Personaje personaje) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            personaje.setEstado(new EstadoSaltando());
            personaje.setVelocidadY(450);
        }
    }

    @Override
    public void actualizar(Personaje personaje, float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            personaje.setVelocidadX(VELOCIDAD_CAMINAR);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            personaje.setVelocidadX(-VELOCIDAD_CAMINAR);
        } else {
            personaje.setEstado(new EstadoQuieto());
        }
    }
}
