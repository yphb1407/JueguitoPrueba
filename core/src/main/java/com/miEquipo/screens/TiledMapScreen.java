package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer; // Importar TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.miEquipo.mygame.MyGdxGame;
import com.miEquipo.facade.PartidaFacade;

public class TiledMapScreen implements Screen {

    private final MyGdxGame game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    private PartidaFacade motorJuego;

    // Map properties
    private int tileWidth;
    private int tileHeight;
    private int mapWidthInTiles;
    private int mapHeightInTiles;
    private int mapWidthInPixels;
    private int mapHeightInPixels;
    private TiledMapTileLayer collisionLayer; // Declarar la capa de colisión

    public TiledMapScreen(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Cargar el mapa Tiled
        map = new TmxMapLoader().load("mapa/segundo_intento_de_mapa.tmx");

        // Configurar el renderer
        renderer = new OrthogonalTiledMapRenderer(map);

        // Obtener propiedades del mapa
        tileWidth = map.getProperties().get("tilewidth", Integer.class);
        tileHeight = map.getProperties().get("tileheight", Integer.class);
        mapWidthInTiles = map.getProperties().get("width", Integer.class);
        mapHeightInTiles = map.getProperties().get("height", Integer.class);
        mapWidthInPixels = mapWidthInTiles * tileWidth;
        mapHeightInPixels = mapHeightInTiles * tileHeight;

        // Intentar obtener la capa de colisión por nombre
        collisionLayer = (TiledMapTileLayer) map.getLayers().get("collision"); // Asumiendo que la capa se llama "collision"
        if (collisionLayer == null) {
            collisionLayer = (TiledMapTileLayer) map.getLayers().get("collisions"); // O "collisions"
        }
        if (collisionLayer == null) {
            // Si no se encuentra por nombre, buscar la primera capa de tiles
            for (int i = 0; i < map.getLayers().getCount(); i++) {
                if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                    collisionLayer = (TiledMapTileLayer) map.getLayers().get(i);
                    Gdx.app.log("TiledMapScreen", "No se encontró capa de colisión 'collision' o 'collisions'. Usando la primera capa de tiles encontrada: " + map.getLayers().get(i).getName());
                    break;
                }
            }
        }
        if (collisionLayer == null) {
            Gdx.app.error("TiledMapScreen", "CRÍTICO: No se encontró ninguna capa de tiles para colisiones en el mapa.");
            // Considerar lanzar una excepción o manejar este error de forma más robusta
        }


        // Configurar la cámara
        camera = new OrthographicCamera();
        camera.setToOrtho(false, mapWidthInPixels, mapHeightInPixels);
        camera.update();

        // Inicializar la fachada del juego, pasándole el ScoreManager, el ancho y el alto del mundo,
        // el mapa, la capa de colisión y las dimensiones de los tiles
        motorJuego = new PartidaFacade(game.getScoreManager(), mapWidthInPixels, mapHeightInPixels,
                                       map, collisionLayer, tileWidth, tileHeight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        motorJuego.actualizarYEscucharEntradas(delta);

        camera.position.set(mapWidthInPixels / 2f, mapHeightInPixels / 2f, 0);
        camera.update();
        renderer.setView(camera);

        renderer.render();

        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        motorJuego.renderizarMundo(game.getBatch(), game.getFont());
        game.getBatch().end();

        if (motorJuego.isGameOver()) {
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    String playerName = text.isEmpty() ? "Anonimo" : text;
                    game.getScoreManager().addScore(playerName, motorJuego.getScoreActual());
                    game.setScreen(new RankScreen(game));
                    dispose();
                }

                @Override
                public void canceled() {
                    game.getScoreManager().addScore("Anonimo", motorJuego.getScoreActual());
                    game.setScreen(new RankScreen(game));
                    dispose();
                }
            }, "GAME OVER! Ingresa tu nombre:", "", "Tu nombre");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, mapWidthInPixels, mapHeightInPixels);
        camera.update();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        motorJuego.dispose();
    }
}
