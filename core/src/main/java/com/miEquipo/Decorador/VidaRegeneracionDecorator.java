package com.miEquipo.Decorador;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.Entidades.ComponentePersonaje;

/**
 * Decorador que añade un efecto de regeneración de vida al personaje.
 * Durante un tiempo determinado, el personaje regenera vida periódicamente
 * y su sprite se muestra con un efecto visual.
 */
public class VidaRegeneracionDecorator extends PersonajeDecorator {
    // --- Constants ---
    private static final float DURATION_TOTAL = 15f; // Duración total del efecto de regeneración
    private static final float REGENERATION_TICK_INTERVAL = 1.0f; // Intervalo de tiempo para cada tick de regeneración
    private static final int REGENERATION_AMOUNT_PER_TICK = 3; // Cantidad de vida regenerada por tick
    private static final Color REGENERATION_COLOR = new Color(0.5f, 1f, 0.5f, 1f); // Color base para el efecto visual
    private static final float ALPHA_BASE = 0.7f; // Alfa base para el efecto de parpadeo
    private static final float ALPHA_PULSE_MAGNITUDE = 0.3f; // Magnitud del pulso de alfa
    private static final float ALPHA_PULSE_SPEED = 5f; // Velocidad del pulso de alfa

    private float tiempoTranscurrido = 0;
    private float acumuladorSegundos = 0;
    private boolean terminado = false;

    /**
     * Interfaz para definir un callback que se ejecuta en cada tick de regeneración.
     */
    public interface OnRegenerarTick {
        /**
         * Se invoca cuando el personaje regenera vida.
         * @param cantidad La cantidad de vida regenerada.
         */
        void onTick(int cantidad);
    }
    private OnRegenerarTick callback;

    /**
     * Constructor para el decorador de regeneración de vida.
     * @param personajeDecorado El ComponentePersonaje a decorar.
     * @param callback El callback a invocar en cada tick de regeneración.
     */
    public VidaRegeneracionDecorator(ComponentePersonaje personajeDecorado, OnRegenerarTick callback) {
        super(personajeDecorado);
        this.callback = callback;
    }

    /**
     * Actualiza el estado del decorador, incluyendo el temporizador de regeneración
     * y la invocación del callback.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    @Override
    public void actualizar(float delta) {
        super.actualizar(delta);
        if (terminado) return;

        tiempoTranscurrido += delta;
        acumuladorSegundos += delta;

        if (acumuladorSegundos >= REGENERATION_TICK_INTERVAL) {
            if (callback != null) {
                callback.onTick(REGENERATION_AMOUNT_PER_TICK);
            }
            acumuladorSegundos = 0;
        }

        if (tiempoTranscurrido >= DURATION_TOTAL) {
            terminado = true;
        }
    }

    /**
     * Dibuja el personaje decorado con un efecto visual de regeneración.
     * @param batch El SpriteBatch utilizado para dibujar texturas.
     */
    @Override
    public void dibujar(SpriteBatch batch) {
        if (!terminado) {
            Color colorAnterior = batch.getColor().cpy();
            float alpha = ALPHA_BASE + (float) Math.abs(Math.sin(tiempoTranscurrido * ALPHA_PULSE_SPEED)) * ALPHA_PULSE_MAGNITUDE;
            batch.setColor(REGENERATION_COLOR.r, REGENERATION_COLOR.g, REGENERATION_COLOR.b, alpha);

            super.dibujar(batch);

            batch.setColor(colorAnterior); // Restaurar color original del Batch
        } else {
            super.dibujar(batch);
        }
    }

    @Override
    public void setX(float x) {

    }

    @Override
    public float getWidth() {
        return 0;
    }

    @Override
    public float getHeight() {
        return 0;
    }

    /**
     * Indica si el efecto de regeneración ha terminado.
     * @return true si el efecto ha terminado, false en caso contrario.
     */
    public boolean isTerminado() {
        return terminado;
    }

    /**
     * Obtiene el componente de personaje original que fue decorado.
     * @return El ComponentePersonaje original.
     */
    public ComponentePersonaje getPersonajeOriginal() {
        return personajeDecorado;
    }

    // Los métodos getVelocidadX() y isMirandoDerecha() son heredados de PersonajeDecorator
    // y delegados automáticamente al personajeDecorado.
}
