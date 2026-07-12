package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.patron_states.PersonajeState;

/**
 * Interfaz base para cualquier componente que actúe como un personaje en el juego.
 * Define las operaciones básicas que un personaje debe poder realizar.
 */
public interface ComponentePersonaje {
    /**
     * Actualiza el estado del personaje en cada frame del juego.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    void actualizar(float delta);

    /**
     * Dibuja el personaje en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar texturas.
     */
    void dibujar(SpriteBatch batch);

    /**
     * Establece el estado actual del personaje (por ejemplo, quieto, saltando).
     * @param nuevoEstado El nuevo estado del personaje.
     */
    void setEstado(PersonajeState nuevoEstado);

    /**
     * Obtiene la posición X actual del personaje.
     * @return La coordenada X del personaje.
     */
    float getX();

    /**
     * Establece la posición X del personaje.
     * @param x La nueva coordenada X del personaje.
     */
    void setX(float x); // Añadido para permitir el ajuste de posición en colisiones

    /**
     * Obtiene la posición Y actual del personaje.
     * @return La coordenada Y del personaje.
     */
    float getY();

    /**
     * Establece la posición Y del personaje.
     * @param y La nueva coordenada Y del personaje.
     */
    void setY(float y);

    /**
     * Obtiene la velocidad actual del personaje en el eje Y.
     * @return La velocidad en Y del personaje.
     */
    float getVelocidadY();

    /**
     * Establece la velocidad del personaje en el eje Y.
     * @param vy La nueva velocidad en Y del personaje.
     */
    void setVelocidadY(float vy);

    /**
     * Obtiene la velocidad actual del personaje en el eje X.
     * @return La velocidad en X del personaje.
     */
    float getVelocidadX();

    /**
     * Establece la velocidad del personaje en el eje X.
     * @param vx La nueva velocidad en X del personaje.
     */
    void setVelocidadX(float vx);

    /**
     * Indica si el personaje está mirando hacia la derecha.
     * @return true si el personaje mira a la derecha, false si mira a la izquierda.
     */
    boolean isMirandoDerecha();

    /**
     * Obtiene el ancho del personaje.
     * @return El ancho del personaje.
     */
    float getWidth(); // Añadido

    /**
     * Obtiene el alto del personaje.
     * @return El alto del personaje.
     */
    float getHeight(); // Añadido
}
