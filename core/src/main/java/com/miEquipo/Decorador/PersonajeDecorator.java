package com.miEquipo.Decorador;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.Entidades.ComponentePersonaje;
import com.miEquipo.patron_states.PersonajeState;

public abstract class PersonajeDecorator implements ComponentePersonaje {
    protected ComponentePersonaje personajeDecorado;

    public PersonajeDecorator(ComponentePersonaje personajeDecorado) {
        this.personajeDecorado = personajeDecorado;
    }

    @Override public void actualizar(float delta) { personajeDecorado.actualizar(delta); }
    @Override public void dibujar(SpriteBatch batch) { personajeDecorado.dibujar(batch); }
    @Override public void setEstado(PersonajeState nuevoEstado) { personajeDecorado.setEstado(nuevoEstado); }
    @Override public float getX() { return personajeDecorado.getX(); }
    @Override public float getY() { return personajeDecorado.getY(); }
    @Override public void setY(float y) { personajeDecorado.setY(y); }
    @Override public float getVelocidadY() { return personajeDecorado.getVelocidadY(); }
    @Override public void setVelocidadY(float vy) { personajeDecorado.setVelocidadY(vy); }
    @Override public float getVelocidadX() { return personajeDecorado.getVelocidadX(); } // Added
    @Override public void setVelocidadX(float vx) { personajeDecorado.setVelocidadX(vx); }
    @Override public boolean isMirandoDerecha() { return personajeDecorado.isMirandoDerecha(); }
}
