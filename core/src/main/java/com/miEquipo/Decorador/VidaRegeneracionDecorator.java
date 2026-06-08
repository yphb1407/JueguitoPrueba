package com.miEquipo.Decorador;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.Entidades.ComponentePersonaje;

public class VidaRegeneracionDecorator extends PersonajeDecorator {
    private float tiempoTranscurrido = 0;
    private float acumuladorSegundos = 0;
    private float duracionTotal = 15f;
    private boolean terminado = false;
    private Color colorVerde = new Color(0.5f, 1f, 0.5f, 1f);

    public interface OnRegenerarTick {
        void onTick(int cantidad);
    }
    private OnRegenerarTick callback;

    public VidaRegeneracionDecorator(ComponentePersonaje personajeDecorado, OnRegenerarTick callback) {
        super(personajeDecorado);
        this.callback = callback;
    }

    @Override
    public void actualizar(float delta) {
        super.actualizar(delta);
        if (terminado) return;

        tiempoTranscurrido += delta;
        acumuladorSegundos += delta;

        if (acumuladorSegundos >= 1.0f) {
            if (callback != null) callback.onTick(3);
            acumuladorSegundos = 0;
        }

        if (tiempoTranscurrido >= duracionTotal) {
            terminado = true;
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        if (!terminado) {
            Color colorAnterior = batch.getColor().cpy();
            float alpha = 0.7f + (float) Math.abs(Math.sin(tiempoTranscurrido * 5)) * 0.3f;
            batch.setColor(0.5f, 1f, 0.5f, alpha);

            super.dibujar(batch);

            batch.setColor(colorAnterior); // Restaurar color original del Batch
        } else {
            super.dibujar(batch);
        }
    }

    public boolean isTerminado() { return terminado; }

    public ComponentePersonaje getPersonajeOriginal() {
        return personajeDecorado;
    }

    @Override
    public boolean isMirandoDerecha() {
        return personajeDecorado.isMirandoDerecha();
    }
}
