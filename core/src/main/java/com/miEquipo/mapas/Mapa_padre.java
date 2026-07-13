package com.miEquipo.mapas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.miEquipo.Entidades.Personaje; // Importar Personaje

/**
 * Clase abstracta base para definir las propiedades y lógica específica de cada mapa Tiled.
 * Las subclases implementarán los detalles de un mapa concreto.
 */
public abstract class Mapa_padre implements Disposable {

    protected TiledMap tiledMap;
    protected int tileWidth;
    protected int tileHeight;
    protected int mapWidthInTiles;
    protected int mapHeightInTiles;
    protected int mapWidthInPixels;
    protected int mapHeightInPixels;

    // Constructor vacío. La carga del mapa se hará en TiledMapScreen.show()
    public Mapa_padre() {
        // No cargar el mapa aquí.
    }

    /**
     * Carga el mapa Tiled y sus propiedades.
     * Este método debe ser llamado explícitamente por la pantalla que utiliza el mapa (ej. TiledMapScreen.show()).
     */
    public void load() {
        tiledMap = new TmxMapLoader().load(getMapPath());
        if (tiledMap != null) {
            tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
            tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
            mapWidthInTiles = tiledMap.getProperties().get("width", Integer.class);
            mapHeightInTiles = tiledMap.getProperties().get("height", Integer.class);
            mapWidthInPixels = mapWidthInTiles * tileWidth;
            mapHeightInPixels = mapHeightInTiles * tileHeight;
        } else {
            Gdx.app.error("Mapa_padre", "Error: TiledMap no se pudo cargar para " + getMapPath());
        }
    }

    /**
     * Devuelve la ruta del archivo .tmx para este mapa.
     *
     * @return La ruta del archivo .tmx.
     */
    public abstract String getMapPath();

    /**
     * Determina si un tile con el ID dado es sólido (colisionable).
     *
     * @param tileId El ID del tile.
     * @return true si el tile es sólido, false en caso contrario.
     */
    public abstract boolean isTileSolid(int tileId);

    /**
     * Devuelve la velocidad base del jugador para este mapa.
     * @return La velocidad del jugador.
     */
    public abstract float getPlayerSpeed();

    /**
     * Devuelve la fuerza de gravedad para este mapa.
     * @return La fuerza de gravedad.
     */
    public abstract float getGravity();

    /**
     * Devuelve la fuerza de salto del jugador para este mapa.
     * @return La fuerza de salto.
     */
    public abstract float getJumpForce();

    /**
     * Crea y devuelve una instancia de Personaje con sus propiedades iniciales para este mapa.
     * @return Una instancia de Personaje.
     */
    public abstract Personaje createPlayer();


    /**
     * Devuelve los puntos de aparición de enemigos definidos para este mapa.
     *
     * @return Un Array de Vector2 con las posiciones de los enemigos.
     */
    public abstract Array<Vector2> getEnemySpawnPoints();


    /**
     * Devuelve el punto de aparición del jugador.
     * Asume que hay una capa de objetos llamada "PlayerSpawn" con al menos un objeto.
     *
     * @return Un Vector2 con la posición de aparición del jugador.
     */
    public Vector2 getPlayerSpawnPoint() {
        // Asegurarse de que la capa "PlayerSpawn" exista
        if (tiledMap != null && tiledMap.getLayers().get("PlayerSpawn") != null) {
            MapObjects objects = tiledMap.getLayers().get("PlayerSpawn").getObjects();
            if (objects != null && objects.getCount() > 0) {
                MapObject object = objects.get(0); // Tomamos el primer objeto como el spawn del jugador
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    return new Vector2(rectObject.getRectangle().x, rectObject.getRectangle().y);
                }
            }
        }
        //Gdx.app.warn("Mapa_padre", "No se encontró la capa de objetos 'PlayerSpawn' o ningún objeto en ella en el mapa: " + getMapPath() + ". Usando posición por defecto (300,300).");
        return new Vector2(300, 300); // Posición por defecto
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getMapWidthInPixels() {
        return mapWidthInPixels;
    }

    public int getMapHeightInPixels() {
        return mapHeightInPixels;
    }

    public int getMapWidthInTiles() {
        return mapWidthInTiles;
    }

    public int getMapHeightInTiles() {
        return mapHeightInTiles;
    }

    @Override
    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
