package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable; // Import Disposable

/**
 * Representa un item de regeneración que cae y puede ser recogido por el personaje.
 * Proporciona métodos para actualizar su posición, dibujarse y detectar colisiones.
 */
public class ItemRegeneracion implements Disposable {
    // --- Constants ---
    private static final String TEXTURE_PATH = "regeneracion.png";
    private static final float FALL_SPEED = 150f; // Velocidad de caída del item
    private static final float ITEM_WIDTH = 48;
    private static final float ITEM_HEIGHT = 48;

    private float x, y;
    private Texture textura;
    private boolean activo;

    /**
     * Constructor para crear un nuevo ItemRegeneracion.
     * @param x La coordenada X inicial del item.
     * @param y La coordenada Y inicial del item.
     */
    public ItemRegeneracion(float x, float y) {
        this.x = x;
        this.y = y;
        this.textura = new Texture(TEXTURE_PATH);
        this.activo = true;
    }

    /**
     * Actualiza la posición del item en cada frame.
     * El item cae hasta alcanzar la posición del suelo.
     * @param delta El tiempo transcurrido desde el último frame.
     * @param sueloY La coordenada Y que representa el suelo.
     */
    public void actualizar(float delta, float sueloY) {
        if (y > sueloY) {
            y -= FALL_SPEED * delta;
        } else {
            y = sueloY;
        }
    }

    /**
     * Dibuja el item en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar texturas.
     */
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, ITEM_WIDTH, ITEM_HEIGHT);
    }

    /**
     * Verifica si este item colisiona con otro objeto.
     * @param otherX La coordenada X del otro objeto.
     * @param otherY La coordenada Y del otro objeto.
     * @param otherWidth El ancho del otro objeto.
     * @param otherHeight La altura del otro objeto.
     * @return true si hay colisión, false en caso contrario.
     */
    public boolean colisiona(float otherX, float otherY, float otherWidth, float otherHeight) {
        return x < otherX + otherWidth &&
               x + ITEM_WIDTH > otherX &&
               y < otherY + otherHeight &&
               y + ITEM_HEIGHT > otherY;
    }

    /**
     * Verifica si el item está activo.
     * @return true si el item está activo, false en caso contrario.
     */
    public boolean isActivo() { return activo; }

    /**
     * Desactiva el item, haciéndolo no interactuable o invisible.
     */
    public void desactivar() { activo = false; }

    /**
     * Obtiene la coordenada X actual del item.
     * @return La coordenada X del item.
     */
    public float getX() { return x; }

    /**
     * Obtiene la coordenada Y actual del item.
     * @return La coordenada Y del item.
     */
    public float getY() { return y; }

    /**
     * Libera los recursos utilizados por el item, como su textura.
     */
    @Override
    public void dispose() {
        if (textura != null) {
            textura.dispose();
        }
    }
}
