package com.miEquipo.patron_states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.miEquipo.Entidades.Personaje;

public class EstadoQuieto implements PersonajeState {
    @Override
    public void manejarEntrada(Personaje personaje) {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            personaje.setEstado(new EstadoSaltando());
            personaje.setVelocidadY(450);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            personaje.setEstado(new EstadoCaminando());
        }
    }

    @Override
    public void actualizar(Personaje personaje, float delta) {
        personaje.setVelocidadX(0);
    }
}
