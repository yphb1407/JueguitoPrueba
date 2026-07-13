package com.miEquipo.Entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable; // Import Disposable

/**
 * Representa un proyectil disparado por el personaje.
 * Gestiona su movimiento, estado activo y dibujo.
 */
public class Proyectil implements Disposable {
    // --- Constants ---
    private static final String TEXTURE_PATH = "proyectil.png";
    private static final float SPEED_X = 400f; // Velocidad base del proyectil
    public static final float WIDTH = 32; // Hacer público para acceso desde Personaje
    public static final float HEIGHT = 32; // Hacer público para acceso desde Personaje
    private static final float DESPAWN_OFFSET = 50; // Distancia fuera de pantalla para desactivar el proyectil

    private float x, y;
    private float velocidadX;
    private Texture textura;
    private boolean activo;

    /**
     * Constructor para crear un nuevo proyectil.
     * @param x La coordenada X inicial del proyectil.
     * @param y La coordenada Y inicial del proyectil.
     * @param haciaDerecha true si el proyectil se mueve hacia la derecha, false si se mueve a la izquierda.
     */
    public Proyectil(float x, float y, boolean haciaDerecha) {
        this.x = x;
        this.y = y;
        this.textura = new Texture(TEXTURE_PATH);
        this.velocidadX = haciaDerecha ? SPEED_X : -SPEED_X;
        this.activo = true;
    }

    /**
     * Actualiza el estado del proyectil en cada frame.
     * Mueve el proyectil y lo desactiva si sale de los límites de la pantalla.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    public void actualizar(float delta) {
        x += velocidadX * delta;
        // Desactivar si sale de la pantalla (usando Gdx.graphics.getWidth() como referencia, aunque en TiledMapScreen
        // se usará mapWidthInPixels para colisiones con el mapa)
        if (x < -DESPAWN_OFFSET || x > Gdx.graphics.getWidth() + DESPAWN_OFFSET) {
            activo = false;
        }
    }

    /**
     * Dibuja el proyectil en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar texturas.
     */
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, WIDTH, HEIGHT);
    }

    /**
     * Verifica si el proyectil está activo (en pantalla y no ha colisionado).
     * @return true si el proyectil está activo, false en caso contrario.
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Establece el estado de actividad del proyectil.
     * @param activo true para activar, false para desactivar.
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
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
     * Obtiene el ancho del proyectil.
     * @return El ancho del proyectil.
     */
    public float getWidth() { return WIDTH; }

    /**
     * Obtiene el alto del proyectil.
     * @return El alto del proyectil.
     */
    public float getHeight() { return HEIGHT; }

    /**
     * Libera los recursos utilizados por el proyectil, como su textura.
     */
    @Override
    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }
}
