package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.patron_states.PersonajeState;

public interface ComponentePersonaje {
    void actualizar(float delta);
    void dibujar(SpriteBatch batch);
    void setEstado(PersonajeState nuevoEstado);
    float getX();
    float getY();
    void setY(float y);
    float getVelocidadY();
    void setVelocidadY(float vy);
    void setVelocidadX(float vx);
    boolean isMirandoDerecha(); // Nuevo para saber hacia dónde disparar
}
