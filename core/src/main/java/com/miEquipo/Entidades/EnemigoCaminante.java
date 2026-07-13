package com.miEquipo.Entidades;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Representa un tipo de enemigo que se mueve horizontalmente.
 * Extiende la clase abstracta Enemigo.
 */
public class EnemigoCaminante extends Enemigo {

    // --- Constants ---
    private static final String ENEMY_TYPE = "Caminante";
    private static final String TEXTURE_PATH = "enemigo.png";
    private static final float DEFAULT_WALKING_SPEED_X = -100f; // Velocidad por defecto si no se especifica

    /**
     * Constructor para crear un EnemigoCaminante con una posición y velocidad específicas.
     * @param x La coordenada X inicial del enemigo.
     * @param y La coordenada Y inicial del enemigo.
     * @param velX La velocidad inicial en el eje X del enemigo.
     * @param map El TiledMap del juego.
     * @param collisionLayer La capa de colisión del TiledMap.
     * @param tileWidth El ancho de los tiles del mapa.
     * @param tileHeight El alto de los tiles del mapa.
     */
    public EnemigoCaminante(float x, float y, float velX, TiledMap map, TiledMapTileLayer collisionLayer, int tileWidth, int tileHeight) {
        super(ENEMY_TYPE, TEXTURE_PATH, map, collisionLayer, tileWidth, tileHeight);
        this.x = x;
        this.y = y;
        this.velocidadX = velX;
    }

    /**
     * Constructor por defecto para crear un EnemigoCaminante.
     * Utiliza una velocidad X predefinida.
     * @param map El TiledMap del juego.
     * @param collisionLayer La capa de colisión del TiledMap.
     * @param tileWidth El ancho de los tiles del mapa.
     * @param tileHeight El alto de los tiles del mapa.
     */
    public EnemigoCaminante(TiledMap map, TiledMapTileLayer collisionLayer, int tileWidth, int tileHeight) {
        this(0, 0, DEFAULT_WALKING_SPEED_X, map, collisionLayer, tileWidth, tileHeight); // Llama al constructor principal con valores por defecto
    }

    /**
     * Actualiza la posición del enemigo en cada frame.
     * Llama al método actualizar de la clase base para manejar la gravedad y las colisiones con el mapa.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    @Override
    public void actualizar(float delta) {
        super.actualizar(delta); // La lógica de movimiento horizontal, gravedad y colisión se maneja en la clase base Enemigo
        // Si EnemigoCaminante tuviera un comportamiento adicional específico (ej. cambiar de dirección bajo ciertas condiciones), iría aquí.
    }
}
