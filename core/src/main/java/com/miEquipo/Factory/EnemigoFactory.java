package com.miEquipo.Factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.miEquipo.Entidades.Enemigo;
import com.miEquipo.Entidades.EnemigoCaminante;
import com.badlogic.gdx.utils.Disposable; // Import Disposable

/**
 * Fábrica para crear instancias de enemigos.
 * Ahora gestiona la creación de enemigos que requieren información del mapa.
 */
public class EnemigoFactory implements Disposable { // Implement Disposable
    private static final String ENEMY_TYPE_GOOMBA = "goomba";
    private static final float DEFAULT_ENEMY_SPEED = -100f;

    // Map parameters to be passed to enemy constructors
    private final TiledMap map;
    private final TiledMapTileLayer collisionLayer;
    private final int tileWidth;
    private final int tileHeight;

    /**
     * Constructor para EnemigoFactory.
     * @param map El TiledMap del juego.
     * @param collisionLayer La capa de colisión del TiledMap.
     * @param tileWidth El ancho de los tiles del mapa.
     * @param tileHeight El alto de los tiles del mapa.
     */
    public EnemigoFactory(TiledMap map, TiledMapTileLayer collisionLayer, int tileWidth, int tileHeight) {
        this.map = map;
        this.collisionLayer = collisionLayer;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Crea una nueva instancia de Enemigo del tipo especificado,
     * configurando su posición y velocidad.
     * @param tipo El tipo de enemigo a crear (ej. "goomba").
     * @param x La coordenada X inicial del enemigo.
     * @param y La coordenada Y inicial del enemigo.
     * @param velX La velocidad inicial en el eje X del enemigo.
     * @return Una nueva instancia de Enemigo, o null si el tipo no es reconocido.
     */
    public Enemigo crearEnemigo(String tipo, float x, float y, float velX) {
        switch (tipo) {
            case ENEMY_TYPE_GOOMBA:
                // Directamente instanciamos, pasando los parámetros del mapa
                return new EnemigoCaminante(x, y, velX, map, collisionLayer, tileWidth, tileHeight);
            default:
                Gdx.app.error("EnemigoFactory", "Tipo de enemigo no reconocido: " + tipo);
                return null;
        }
    }

    /**
     * Crea una nueva instancia de Enemigo del tipo especificado,
     * configurando su posición con una velocidad por defecto.
     * @param tipo El tipo de enemigo a crear (ej. "goomba").
     * @param x La coordenada X inicial del enemigo.
     * @param y La coordenada Y inicial del enemigo.
     * @return Una nueva instancia de Enemigo, o null si el tipo no es reconocido.
     */
    public Enemigo crearEnemigo(String tipo, float x, float y) {
        return crearEnemigo(tipo, x, y, DEFAULT_ENEMY_SPEED);
    }

    /**
     * Libera los recursos de la fábrica.
     * En este diseño, la fábrica no posee recursos Disposable directamente,
     * pero si se añadieran prototipos que sí los tuvieran, se liberarían aquí.
     * Por ahora, no hay nada que liberar directamente en la fábrica.
     */
    @Override
    public void dispose() {
        // No hay prototipos estáticos que liberar en este nuevo diseño.
        // Los enemigos individuales se liberan a través de EntityManager.
    }
}
