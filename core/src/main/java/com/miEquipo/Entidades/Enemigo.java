package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable; // Import Disposable

/**
 * Clase abstracta base para todos los enemigos en el juego.
 * Define propiedades y comportamientos comunes, como posición, velocidad, textura y dibujo.
 * Implementa Cloneable para permitir la creación de copias de enemigos.
 * Implementa Disposable para gestionar la liberación de recursos (texturas).
 */
public abstract class Enemigo implements Cloneable, Disposable {
    // --- Constants ---
    protected static final float DEFAULT_SPEED_X = -60f;
    protected static final float DEFAULT_WIDTH = 64;
    protected static final float DEFAULT_HEIGHT = 64;

    protected float x, y;
    public float velocidadX;
    protected Texture textura;
    protected String tipo;
    protected float ancho;
    protected float alto;

    /**
     * Constructor para la clase abstracta Enemigo.
     * Inicializa el tipo de enemigo y carga su textura.
     * @param tipo El tipo o nombre del enemigo.
     * @param rutaTextura La ruta del archivo de textura del enemigo.
     */
    public Enemigo(String tipo, String rutaTextura) {
        this.tipo = tipo;
        this.textura = new Texture(rutaTextura);
        this.velocidadX = DEFAULT_SPEED_X;
        this.ancho = DEFAULT_WIDTH;
        this.alto = DEFAULT_HEIGHT;
    }

    /**
     * Actualiza el estado del enemigo en cada frame del juego.
     * Este método debe ser implementado por las subclases concretas.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    public abstract void actualizar(float delta);

    /**
     * Dibuja el enemigo en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar texturas.
     */
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, ancho, alto);
    }

    /**
     * Crea y devuelve una copia de este objeto.
     * @return Una copia superficial de este objeto Enemigo.
     */
    @Override
    public Enemigo clone() {
        try {
            // Realiza una copia superficial. La textura se comparte entre clones,
            // lo cual es eficiente si todos los enemigos del mismo tipo usan la misma textura.
            return (Enemigo) super.clone();
        } catch (CloneNotSupportedException e) {
            // Esto no debería ocurrir ya que implementamos Cloneable
            throw new InternalError(e);
        }
    }

    /**
     * Establece la posición del enemigo en el mundo del juego.
     * @param x La nueva coordenada X del enemigo.
     * @param y La nueva coordenada Y del enemigo.
     */
    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene la coordenada X actual del enemigo.
     * @return La coordenada X del enemigo.
     */
    public float getX() { return x; }

    /**
     * Obtiene la coordenada Y actual del enemigo.
     * @return La coordenada Y del enemigo.
     */
    public float getY() { return y; }

    /**
     * Libera los recursos utilizados por el enemigo.
     * NOTA: No liberamos la textura aquí porque, al usar el patrón Prototype,
     * la textura es compartida entre todos los clones. Si un clon la libera,
     * todos los demás enemigos (y el prototipo) se quedan sin ella.
     */
    @Override
    public void dispose() {
        // No disponemos la textura aquí para evitar que los clones la destruyan prematuramente.
    }

    /**
     * Libera específicamente la textura del enemigo.
     * Este método debe ser llamado solo cuando estemos seguros de que la textura
     * ya no será necesaria por ningún clon (por ejemplo, al cerrar el juego).
     */
    public void disposeTexture() {
        if (textura != null) {
            textura.dispose();
            textura = null;
        }
    }
}
