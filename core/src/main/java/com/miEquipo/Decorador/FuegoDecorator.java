package com.miEquipo.Decorador;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.Entidades.ComponentePersonaje;

public class FuegoDecorator extends PersonajeDecorator {

    public FuegoDecorator(ComponentePersonaje personajeDecorado) {
        super(personajeDecorado);
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        Color colorOriginal = batch.getColor();
        batch.setColor(Color.RED); // Tinte rojo para el modo fuego
        super.dibujar(batch);
        batch.setColor(colorOriginal);
    }

    @Override
    public boolean isMirandoDerecha() {
        return false;
    }
}
