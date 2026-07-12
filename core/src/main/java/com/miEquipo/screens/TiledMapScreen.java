package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.miEquipo.mygame.MyGdxGame;
import com.badlogic.gdx.Input;
import com.miEquipo.Entidades.Personaje;
import com.miEquipo.patron_states.EstadoQuieto;

public class TiledMapScreen implements Screen {

    private final MyGdxGame game;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    // Player variables (now using Personaje class)
    private Personaje personaje;
    private float playerSpeed = 400; // Pixels por segundo (ajustado para tiles de 100x100)
    private float gravity = -1000f; // Gravedad (ajustada)
    private float JUMP_FORCE = 2000f; // Fuerza del salto (ajustada)
    private boolean onGround; // Indica si el jugador está en el suelo

    // Map properties
    private int tileWidth;
    private int tileHeight;
    private int mapWidthInTiles;
    private int mapHeightInTiles;
    private int mapWidthInPixels;
    private int mapHeightInPixels;

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
        mapWidthInPixels = mapWidthInTiles * tileWidth; // 30 * 100 = 3000
        mapHeightInPixels = mapHeightInTiles * tileHeight; // 20 * 100 = 2000

        // Configurar la cámara
        camera = new OrthographicCamera();
        // El viewport de la cámara será del tamaño exacto del mapa en píxeles
        camera.setToOrtho(false, mapWidthInPixels, mapHeightInPixels);
        camera.update();

        // Inicializar el personaje
        personaje = new Personaje(300, 300); // Posición inicial
        personaje.setEstado(new EstadoQuieto()); // Asegurarse de que tenga un estado inicial
        onGround = false; // Asumimos que no está en el suelo al inicio hasta que se verifique
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Player Input ---
        float currentVelX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            currentVelX = -playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentVelX = playerSpeed;
        }
        personaje.setVelocidadX(currentVelX);

        // --- Apply Gravity and Jump ---
        personaje.setVelocidadY(personaje.getVelocidadY() + gravity * delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            personaje.setVelocidadY(JUMP_FORCE);
            onGround = false; // El jugador ya no está en el suelo al saltar
        }

        // --- Store Old Position ---
        float oldPersonajeX = personaje.getX();
        float oldPersonajeY = personaje.getY();

        // --- Predicted Movement ---
        float newPersonajeX = personaje.getX() + personaje.getVelocidadX() * delta;
        float newPersonajeY = personaje.getY() + personaje.getVelocidadY() * delta;

        // --- Horizontal Movement and Collision ---
        personaje.setX(newPersonajeX); // Mover horizontalmente
        // Clamp a los límites del mapa (horizontal)
        if (personaje.getX() < 0) personaje.setX(0);
        if (personaje.getX() > mapWidthInPixels - personaje.getWidth())
            personaje.setX(mapWidthInPixels - personaje.getWidth());

        Rectangle playerBounds = new Rectangle(personaje.getX(), personaje.getY(), personaje.getWidth(), personaje.getHeight());
        if (collidesWithMap(playerBounds, map, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles)) {
            // Colisión detectada horizontalmente
            personaje.setX(oldPersonajeX); // Revertir a la posición X anterior
            personaje.setVelocidadX(0); // Detener movimiento horizontal
        }

        // --- Vertical Movement and Collision ---
        personaje.setY(newPersonajeY); // Mover verticalmente
        // Clamp a los límites del mapa (vertical)
        if (personaje.getY() < 0) {
            personaje.setY(0);
            personaje.setVelocidadY(0);
            onGround = true;
        }
        if (personaje.getY() > mapHeightInPixels - personaje.getHeight()) {
            personaje.setY(mapHeightInPixels - personaje.getHeight());
            personaje.setVelocidadY(0); // Choca con el techo
        }

        playerBounds.setPosition(personaje.getX(), personaje.getY()); // Actualizar playerBounds para la verificación vertical
        if (collidesWithMap(playerBounds, map, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles)) {
            // Colisión detectada verticalmente
            personaje.setY(oldPersonajeY); // Revertir a la posición Y anterior
            personaje.setVelocidadY(0); // Detener movimiento vertical
            if (newPersonajeY < oldPersonajeY) { // Si el jugador estaba cayendo
                onGround = true;
            }
        }

        // --- Final onGround check ---
        // Esta verificación es crucial y debe hacerse después de todos los movimientos y colisiones verticales.
        // Comprueba si hay un tile sólido directamente debajo del jugador.
        Rectangle groundCheckRect = new Rectangle(personaje.getX(), personaje.getY() - 1, personaje.getWidth(), 1);
        onGround = collidesWithMap(groundCheckRect, map, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles);

        // --- Camera Update ---
        // La cámara ya no necesita seguir al jugador ni ser "clamp", ya que muestra todo el mapa.
        // Solo la actualizamos una vez para que se centre en el mapa.
        camera.position.set(mapWidthInPixels / 2f, mapHeightInPixels / 2f, 0);
        camera.update();
        renderer.setView(camera);

        // Render the map
        renderer.render();

        // Render the player
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        personaje.dibujar(game.getBatch()); // Usar el método dibujar del Personaje
        game.getBatch().end();

        // Update Personaje's internal state (animations, etc.)
        // Esto ahora solo maneja la animación y el estado, no la posición.
        personaje.actualizar(delta);

        // Volver al menú principal si se presiona ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    // Helper method to check if a tile ID is considered solid/collidable
    private boolean isTileSolid(int tileId) {
        // GID 1 y 2 son los tiles de colisión según tu descripción
        return tileId == 1 || tileId == 2;
    }

    // Helper method to check if a rectangle collides with any solid tile in the map
    private boolean collidesWithMap(Rectangle rect, TiledMap map, int tileWidth, int tileHeight, int mapWidthInTiles, int mapHeightInTiles) {
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(i);

                // Check the four corners of the rectangle for collision
                if (checkPointCollision(rect.x, rect.y, layer, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles) ||
                    checkPointCollision(rect.x + rect.width, rect.y, layer, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles) ||
                    checkPointCollision(rect.x, rect.y + rect.height, layer, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles) ||
                    checkPointCollision(rect.x + rect.width, rect.y + rect.height, layer, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Helper method to check if a specific point collides with a solid tile
    private boolean checkPointCollision(float x, float y, TiledMapTileLayer layer, int tileWidth, int tileHeight, int mapWidthInTiles, int mapHeightInTiles) {
        int col = (int) (x / tileWidth);
        int row = (int) (y / tileHeight);

        // Ensure the point is within map bounds
        if (col < 0 || row < 0 || col >= mapWidthInTiles || row >= mapHeightInTiles) {
            return false;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(col, row);
        return cell != null && isTileSolid(cell.getTile().getId());
    }


    @Override
    public void resize(int width, int height) {
        // El viewport de la cámara será del tamaño exacto del mapa en píxeles
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
        personaje.dispose(); // Dispose del personaje
    }
}
