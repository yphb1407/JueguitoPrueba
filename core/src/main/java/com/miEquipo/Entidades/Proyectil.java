package com.miEquipo.Entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable; // Import Disposable
import com.badlogic.gdx.utils.GdxRuntimeException; // Import GdxRuntimeException para el manejo de errores

/**
 * Representa un proyectil disparado por el personaje.
 * Gestiona su movimiento, estado activo y dibujo.
 */
public class Proyectil implements Disposable {
    // --- Constants ---
    private static final String TEXTURE_PATH = "proyectil.png"; // Vuelto a 'p' minúscula
    private static final float SPEED_X = 400f; // Velocidad base del proyectil
    public static final float WIDTH = 100;
    public static final float HEIGHT = 100;
    private static final float DESPAWN_OFFSET = 50; // Distancia fuera de pantalla para desactivar el proyectil

    private float x, y;
    private float velocidadX;
    private Texture textura; // Puede ser null si la carga falla
    private boolean activo;
    private float worldWidth;

    /**
     * Constructor para crear un nuevo proyectil.
     * @param x La coordenada X inicial del proyectil.
     * @param y La coordenada Y inicial del proyectil.
     * @param haciaDerecha true si el proyectil se mueve hacia la derecha, false si se mueve a la izquierda.
     * @param worldWidth El ancho del mundo del juego para determinar cuándo el proyectil sale de los límites.
     */
    public Proyectil(float x, float y, boolean haciaDerecha, float worldWidth) {
        this.x = x;
        this.y = y;
        this.velocidadX = haciaDerecha ? SPEED_X : -SPEED_X;
        this.activo = true;
        this.worldWidth = worldWidth;

        try {
            this.textura = new Texture(TEXTURE_PATH);
            // Gdx.app.log("Proyectil", "Textura cargada exitosamente: " + TEXTURE_PATH); // ELIMINADO
        } catch (GdxRuntimeException e) {
            Gdx.app.error("Proyectil", "ERROR: No se pudo cargar la textura del proyectil desde: " + TEXTURE_PATH + ". Mensaje: " + e.getMessage());
            this.textura = null; // Establecer a null para evitar NullPointerException
        }
    }

    /**
     * Actualiza el estado del proyectil en cada frame.
     * Mueve el proyectil y lo desactiva si sale de los límites del mundo.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    public void actualizar(float delta) {
        x += velocidadX * delta;
        // Desactivar si sale de los límites del mundo
        if (x < -DESPAWN_OFFSET || x > worldWidth + DESPAWN_OFFSET) {
            activo = false;
        }
    }

    /**
     * Dibuja el proyectil en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar texturas.
     */
    public void dibujar(SpriteBatch batch) {
        if (textura != null) { // Solo dibujar si la textura se cargó correctamente
            batch.draw(textura, x, y, WIDTH, HEIGHT);
        } else {
            // Gdx.app.debug("Proyectil", "No se dibuja el proyectil porque la textura es nula.");
        }
    }

    /**
     * Verifica si el proyectil está activo (en pantalla y no ha colisionado).
     * @return true si el proyectil está activo, false en caso contrario.
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Obtiene la coordenada X actual del proyectil.
     * @return La coordenada X del proyectil.
     */
    public float getX() { return x; }

    /**
     * Obtiene la coordenada Y actual del proyectil.
     * @return La coordenada Y del proyectil.
     */
    public float getY() { return y; }

    /**
     * Libera los recursos utilizados por el proyectil, como su textura.
     */
    @Override
    public void dispose() {
        if (textura != null) { // Solo liberar si la textura existe
            textura.dispose();
            textura = null; // Ayuda a evitar doble dispose si se llama varias veces
        }
    }
}
