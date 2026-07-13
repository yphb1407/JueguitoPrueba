package com.miEquipo.physics;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Encapsula la lógica de colisión con el mapa Tiled.
 * Cumple con el principio de Responsabilidad Única (SRP).
 */
public class MapCollisionHandler {
    private final TiledMap map;
    private final TiledMapTileLayer collisionLayer;
    private final int tileWidth;
    private final int tileHeight;

    public MapCollisionHandler(TiledMap map, TiledMapTileLayer collisionLayer, int tileWidth, int tileHeight) {
        this.map = map;
        this.collisionLayer = collisionLayer;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Verifica si un rectángulo colisiona con algún tile sólido del mapa.
     */
    public boolean collides(Rectangle rect) {
        if (collisionLayer == null) return false;

        return checkPointCollision(rect.x, rect.y) ||
               checkPointCollision(rect.x + rect.width, rect.y) ||
               checkPointCollision(rect.x, rect.y + rect.height) ||
               checkPointCollision(rect.x + rect.width, rect.y + rect.height);
    }

    /**
     * Verifica si un punto específico colisiona con un tile sólido.
     */
    private boolean checkPointCollision(float x, float y) {
        int col = (int) (x / tileWidth);
        int row = (int) (y / tileHeight);

        Integer mapWidth = map.getProperties().get("width", Integer.class);
        Integer mapHeight = map.getProperties().get("height", Integer.class);

        if (col < 0 || row < 0 || col >= mapWidth || row >= mapHeight) {
            return false;
        }

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(col, row);
        if (cell == null || cell.getTile() == null) return false;

        int tileId = cell.getTile().getId();
        // IDs de tiles sólidos definidos por el usuario
        return tileId == 1 || tileId == 2;
    }
}
