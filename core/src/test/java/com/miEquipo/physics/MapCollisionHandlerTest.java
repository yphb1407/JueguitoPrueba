package com.miEquipo.physics;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MapCollisionHandlerTest {

    private TiledMap map;
    private TiledMapTileLayer collisionLayer;
    private MapProperties properties;
    private MapCollisionHandler collisionHandler;

    @BeforeEach
    void setUp() {

        map = mock(TiledMap.class);
        collisionLayer = mock(TiledMapTileLayer.class);
        properties = new MapProperties();

        properties.put("width", 10);
        properties.put("height", 10);

        when(map.getProperties()).thenReturn(properties);

        collisionHandler = new MapCollisionHandler(
            map,
            collisionLayer,
            32,
            32
        );
    }

    @Test
    void debeRetornarFalseSiNoHayCapaDeColision() {

        MapCollisionHandler handler =
            new MapCollisionHandler(map, null, 32, 32);

        Rectangle rect = new Rectangle(0, 0, 32, 32);

        assertFalse(handler.collides(rect));
    }

    @Test
    void debeRetornarFalseCuandoNoHayCelda() {

        when(collisionLayer.getCell(anyInt(), anyInt()))
            .thenReturn(null);

        Rectangle rect = new Rectangle(32, 32, 16, 16);

        assertFalse(collisionHandler.collides(rect));
    }

    @Test
    void debeDetectarColisionConTileIdUno() {

        TiledMapTileLayer.Cell cell = mock(TiledMapTileLayer.Cell.class);
        TiledMapTile tile = mock(TiledMapTile.class);

        when(tile.getId()).thenReturn(1);
        when(cell.getTile()).thenReturn(tile);

        when(collisionLayer.getCell(anyInt(), anyInt()))
            .thenReturn(cell);

        Rectangle rect = new Rectangle(32, 32, 16, 16);

        assertTrue(collisionHandler.collides(rect));
    }

    @Test
    void debeDetectarColisionConTileIdDos() {

        TiledMapTileLayer.Cell cell = mock(TiledMapTileLayer.Cell.class);
        TiledMapTile tile = mock(TiledMapTile.class);

        when(tile.getId()).thenReturn(2);
        when(cell.getTile()).thenReturn(tile);

        when(collisionLayer.getCell(anyInt(), anyInt()))
            .thenReturn(cell);

        Rectangle rect = new Rectangle(32, 32, 16, 16);

        assertTrue(collisionHandler.collides(rect));
    }

    @Test
    void noDebeDetectarColisionConTileNoSolido() {

        TiledMapTileLayer.Cell cell = mock(TiledMapTileLayer.Cell.class);
        TiledMapTile tile = mock(TiledMapTile.class);

        when(tile.getId()).thenReturn(10);
        when(cell.getTile()).thenReturn(tile);

        when(collisionLayer.getCell(anyInt(), anyInt()))
            .thenReturn(cell);

        Rectangle rect = new Rectangle(32, 32, 16, 16);

        assertFalse(collisionHandler.collides(rect));
    }

    @Test
    void noDebeDetectarColisionFueraDelMapa() {

        Rectangle rect = new Rectangle(-100, -100, 16, 16);

        assertFalse(collisionHandler.collides(rect));

        verify(collisionLayer, never())
            .getCell(anyInt(), anyInt());
    }

    @Test
    void debeRetornarFalseSiLaCeldaNoTieneTile() {

        TiledMapTileLayer.Cell cell = mock(TiledMapTileLayer.Cell.class);

        when(cell.getTile()).thenReturn(null);

        when(collisionLayer.getCell(anyInt(), anyInt()))
            .thenReturn(cell);

        Rectangle rect = new Rectangle(32, 32, 16, 16);

        assertFalse(collisionHandler.collides(rect));
    }

}
