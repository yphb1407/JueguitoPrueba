package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;

/**
 * Representa un texto que flota y se desvanece en la pantalla,
 * comúnmente utilizado para mostrar cambios de puntuación o vida.
 */
public class TextoFlotante {
    // --- Constants ---
    private static final float DEFAULT_DURATION_MAX = 1.0f; // Duración máxima del texto flotante en segundos
    private static final float FLOAT_SPEED_Y = 50; // Velocidad a la que el texto sube en el eje Y

    private float x, y;
    private final String texto;
    private float tiempoVida;
    private final float duracionMax;
    private final Color color; // Hacer el color final para evitar cambios inesperados

    /**
     * Constructor para crear un nuevo TextoFlotante.
     * @param texto El contenido de texto a mostrar.
     * @param x La coordenada X inicial del texto.
     * @param y La coordenada Y inicial del texto.
     * @param color El color inicial del texto.
     */
    public TextoFlotante(String texto, float x, float y, Color color) {
        this.texto = texto;
        this.x = x;
        this.y = y;
        this.color = new Color(color); // Crear una nueva instancia de Color para evitar modificar el original
        this.tiempoVida = 0;
        this.duracionMax = DEFAULT_DURATION_MAX;
    }

    /**
     * Actualiza el estado del texto flotante en cada frame.
     * Mueve el texto hacia arriba y reduce su opacidad con el tiempo.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    public void actualizar(float delta) {
        tiempoVida += delta;
        y += FLOAT_SPEED_Y * delta; // Sube poco a poco
        // Calcula la opacidad (alpha) basándose en el tiempo de vida transcurrido
        color.a = 1 - (tiempoVida / duracionMax);
        // Asegurarse de que el alpha no sea negativo
        if (color.a < 0) {
            color.a = 0;
        }
    }

    /**
     * Dibuja el texto flotante en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar.
     * @param font La fuente BitmapFont utilizada para renderizar el texto.
     */
    public void dibujar(SpriteBatch batch, BitmapFont font) {
        font.setColor(color); // Establecer el color (incluyendo el alpha)
        font.draw(batch, texto, x, y);
    }

    /**
     * Verifica si el texto flotante ha excedido su duración máxima y debe ser eliminado.
     * @return true si el texto ha "muerto" (terminado su ciclo de vida), false en caso contrario.
     */
    public boolean isMuerto() {
        return tiempoVida >= duracionMax;
    }
}
