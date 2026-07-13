package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.miEquipo.mygame.MyGdxGame;
import com.badlogic.gdx.Input;
import com.miEquipo.Entidades.Personaje;
import com.miEquipo.Entidades.Enemigo;
import com.miEquipo.Entidades.Proyectil;
import com.miEquipo.Entidades.TextoFlotante; // Importar TextoFlotante
import com.miEquipo.Entidades.ComponentePersonaje; // Importar ComponentePersonaje
import com.miEquipo.Factory.EnemigoFactory;
import com.miEquipo.patron_states.EstadoQuieto;
import com.miEquipo.mapas.Mapa_padre;
import com.miEquipo.managers.EntityManager; // Importar EntityManager
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Importar BitmapFont

import java.util.Iterator;

public class TiledMapScreen implements Screen, EntityManager.EntityManagerCallback { // Implementar la interfaz

    private final MyGdxGame game;
    private Mapa_padre mapa;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;

    // Player variables
    private Personaje personaje;
    private float playerSpeed;
    private float gravity;
    private float JUMP_FORCE;
    private boolean onGround;

    // Enemy variables
    private Array<Enemigo> enemigos;
    private Array<Vector2> originalEnemySpawnPoints;

    // Projectile variables
    private Array<Proyectil> proyectiles;

    // Map properties
    private int tileWidth;
    private int tileHeight;
    private int mapWidthInTiles;
    private int mapHeightInTiles;
    private int mapWidthInPixels;
    private int mapHeightInPixels;

    // EntityManager
    private EntityManager entityManager;

    // Constructor
    public TiledMapScreen(MyGdxGame game, Mapa_padre mapa) {
        this.game = game;
        this.mapa = mapa;
    }

    @Override
    public void show() {
        mapa.load();

        tiledMap = mapa.getTiledMap();
        renderer = new OrthogonalTiledMapRenderer(tiledMap);

        tileWidth = mapa.getTileWidth();
        tileHeight = mapa.getTileHeight();
        mapWidthInTiles = mapa.getMapWidthInTiles();
        mapHeightInTiles = mapa.getMapHeightInTiles();
        mapWidthInPixels = mapa.getMapWidthInPixels();
        mapHeightInPixels = mapa.getMapHeightInPixels();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, mapWidthInPixels, mapHeightInPixels);
        camera.update();

        playerSpeed = mapa.getPlayerSpeed();
        gravity = mapa.getGravity();
        JUMP_FORCE = mapa.getJumpForce();

        personaje = mapa.createPlayer();
        onGround = false;

        enemigos = new Array<>();
        originalEnemySpawnPoints = mapa.getEnemySpawnPoints();
        loadInitialEnemies();

        proyectiles = new Array<>();

        // Inicializar EntityManager, pasándose a sí mismo como callback
        entityManager = new EntityManager(this);
        // Aquí podrías añadir ítems de regeneración si los tuvieras definidos en el mapa o de otra forma
        // Por ejemplo: entityManager.addItem(new ItemRegeneracion(100, 100));
    }

    private void loadInitialEnemies() {
        if (originalEnemySpawnPoints.size == 0) {
            Gdx.app.log("TiledMapScreen", "No se encontraron puntos de aparición de enemigos en el mapa.");
            return;
        }

        for (Vector2 spawnPoint : originalEnemySpawnPoints) {
            spawnNewEnemy(spawnPoint);
        }
    }

    private void spawnNewEnemy(Vector2 spawnPoint) {
        Enemigo enemigo = EnemigoFactory.crearEnemigo("goomba", spawnPoint.x, spawnPoint.y, -100f);
        if (enemigo != null) {
            enemigos.add(enemigo);
        }
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

        // Disparar proyectil
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            Proyectil nuevoProyectil = personaje.disparar();
            if (nuevoProyectil != null) {
                proyectiles.add(nuevoProyectil);
            }
        }

        // --- Apply Gravity and Jump ---
        personaje.setVelocidadY(personaje.getVelocidadY() + gravity * delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            personaje.setVelocidadY(JUMP_FORCE);
            onGround = false;
        }

        // --- Store Old Position ---
        float oldPersonajeX = personaje.getX();
        float oldPersonajeY = personaje.getY();

        // --- Predicted Movement ---
        float newPersonajeX = personaje.getX() + personaje.getVelocidadX() * delta;
        float newPersonajeY = personaje.getY() + personaje.getVelocidadY() * delta;

        // --- Horizontal Movement and Collision ---
        personaje.setX(newPersonajeX);
        if (personaje.getX() < 0) personaje.setX(0);
        if (personaje.getX() > mapWidthInPixels - personaje.getWidth())
            personaje.setX(mapWidthInPixels - personaje.getWidth());

        Rectangle playerBounds = new Rectangle(personaje.getX(), personaje.getY(), personaje.getWidth(), personaje.getHeight());
        if (collidesWithMap(playerBounds, tiledMap, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles)) {
            personaje.setX(oldPersonajeX);
            personaje.setVelocidadX(0);
        }

        // --- Vertical Movement and Collision ---
        personaje.setY(newPersonajeY);
        if (personaje.getY() < 0) {
            personaje.setY(0);
            personaje.setVelocidadY(0);
            onGround = true;
        }
        if (personaje.getY() > mapHeightInPixels - personaje.getHeight()) {
            personaje.setY(mapHeightInPixels - personaje.getHeight());
            personaje.setVelocidadY(0);
        }

        playerBounds.setPosition(personaje.getX(), personaje.getY());
        if (collidesWithMap(playerBounds, tiledMap, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles)) {
            personaje.setY(oldPersonajeY);
            personaje.setVelocidadY(0);
            if (newPersonajeY < oldPersonajeY) {
                onGround = true;
            }
        }

        // --- Final onGround check ---
        Rectangle groundCheckRect = new Rectangle(personaje.getX(), personaje.getY() - 1, personaje.getWidth(), 1);
        onGround = collidesWithMap(groundCheckRect, tiledMap, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles);

        // --- Camera Update ---
        camera.position.set(mapWidthInPixels / 2f, mapHeightInPixels / 2f, 0);
        camera.update();
        renderer.setView(camera);

        // Render the map
        renderer.render();

        // Render the player
        game.getBatch().setProjectionMatrix(camera.combined);
        game.getBatch().begin();
        personaje.dibujar(game.getBatch());

        // --- Update and Render Enemies (with respawn logic) ---
        Iterator<Enemigo> enemyIterator = enemigos.iterator();
        while (enemyIterator.hasNext()) {
            Enemigo enemigo = enemyIterator.next();
            enemigo.actualizar(delta, personaje.getX(), personaje.getY(), mapWidthInPixels, mapHeightInPixels);
            enemigo.dibujar(game.getBatch());

            // Player-enemy collision detection
            Rectangle enemyRect = new Rectangle(enemigo.getX(), enemigo.getY(), enemigo.getAncho(), enemigo.getAlto());
            if (playerBounds.overlaps(enemyRect)) {
                entityManager.onPlayerHit(personaje.getX(), personaje.getY()); // Notificar a EntityManager
                enemyIterator.remove(); // Eliminar el enemigo
                // Respawnear un nuevo enemigo
                if (originalEnemySpawnPoints.size > 0) {
                    int randomIndex = (int) (Math.random() * originalEnemySpawnPoints.size);
                    spawnNewEnemy(originalEnemySpawnPoints.get(randomIndex));
                }
                // No break, ya que el jugador podría colisionar con múltiples enemigos en un frame
                // y queremos que todos los eventos se disparen.
            } else if (enemigo.getX() + enemigo.getAncho() < 0 || enemigo.getX() > mapWidthInPixels) {
                // Comprobar si el enemigo está fuera de los límites del mapa
                enemyIterator.remove(); // Eliminar el enemigo actual
                // Spawnear un nuevo enemigo en un punto de spawn aleatorio de los originales
                if (originalEnemySpawnPoints.size > 0) {
                    int randomIndex = (int) (Math.random() * originalEnemySpawnPoints.size);
                    spawnNewEnemy(originalEnemySpawnPoints.get(randomIndex));
                }
            }
        }

        // --- Update and Render Projectiles (with collision logic) ---
        Iterator<Proyectil> projectileIterator = proyectiles.iterator();
        while (projectileIterator.hasNext()) {
            Proyectil proyectil = projectileIterator.next();
            proyectil.actualizar(delta);
            proyectil.dibujar(game.getBatch());

            // Colisión de proyectil con el mapa
            Rectangle projectileBounds = new Rectangle(proyectil.getX(), proyectil.getY(), proyectil.getWidth(), proyectil.getHeight());
            if (collidesWithMap(projectileBounds, tiledMap, tileWidth, tileHeight, mapWidthInTiles, mapHeightInTiles)) {
                proyectil.setActivo(false); // Desactivar proyectil al colisionar con el mapa
            }

            // Colisión de proyectil con enemigos
            Iterator<Enemigo> enemyCollisionIterator = enemigos.iterator();
            while (enemyCollisionIterator.hasNext()) {
                Enemigo enemigo = enemyCollisionIterator.next();
                Rectangle enemyBounds = new Rectangle(enemigo.getX(), enemigo.getY(), enemigo.getAncho(), enemigo.getAlto());

                if (proyectil.isActivo() && projectileBounds.overlaps(enemyBounds)) {
                    proyectil.setActivo(false); // Desactivar proyectil
                    entityManager.onEnemyDefeated(enemigo.getX(), enemigo.getY()); // Notificar a EntityManager
                    enemyCollisionIterator.remove(); // Eliminar enemigo
                    // Respawnear un nuevo enemigo
                    if (originalEnemySpawnPoints.size > 0) {
                        int randomIndex = (int) (Math.random() * originalEnemySpawnPoints.size);
                        spawnNewEnemy(originalEnemySpawnPoints.get(randomIndex));
                    }
                    break; // Un proyectil solo puede golpear a un enemigo a la vez
                }
            }

            // Eliminar proyectiles inactivos
            if (!proyectil.isActivo()) {
                proyectil.dispose(); // Liberar recursos del proyectil
                projectileIterator.remove();
            }
        }

        game.getBatch().end();

        // Update Personaje's internal state (animations, etc.)
        personaje.actualizar(delta);

        // Actualizar EntityManager (para ítems y textos flotantes)
        entityManager.update(delta, personaje);

        // Renderizar entidades gestionadas por EntityManager (ítems y textos flotantes)
        game.getBatch().setProjectionMatrix(camera.combined); // Asegurar que el batch usa la proyección de la cámara
        game.getBatch().begin();
        entityManager.render(game.getBatch(), game.getFont()); // Asumiendo que game.getFont() existe
        game.getBatch().end();


        // Volver al menú principal si se presiona ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    // Helper method to check if a tile ID is considered solid/collidable
    private boolean isTileSolid(int tileId) {
        return mapa.isTileSolid(tileId);
    }

    // Helper method to check if a rectangle collides with any solid tile in the map
    private boolean collidesWithMap(Rectangle rect, TiledMap map, int tileWidth, int tileHeight, int mapWidthInTiles, int mapHeightInTiles) {
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            if (map.getLayers().get(i) instanceof TiledMapTileLayer) {
                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(i);

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

        if (col < 0 || row < 0 || col >= mapWidthInTiles || row >= mapHeightInTiles) {
            return false;
        }

        TiledMapTileLayer.Cell cell = layer.getCell(col, row);
        return cell != null && isTileSolid(cell.getTile().getId());
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
        renderer.dispose();
        personaje.dispose();
        for (Enemigo enemigo : enemigos) {
            enemigo.dispose();
        }
        enemigos.clear();
        for (Proyectil proyectil : proyectiles) {
            proyectil.dispose();
        }
        proyectiles.clear();
        mapa.dispose();
        entityManager.dispose(); // Disponer EntityManager
    }

    // --- Implementación de EntityManager.EntityManagerCallback ---
    @Override
    public void onPlayerDamaged(int amount) {
        // Aquí puedes implementar la lógica para reducir la vida del personaje
        // Por ejemplo: personaje.recibirDano(amount);
        Gdx.app.log("TiledMapScreen", "Player damaged by: " + amount);
        // Necesitarás una forma de manejar la vida del personaje, quizás en la clase Personaje
        // o en una variable aquí en TiledMapScreen.
    }

    @Override
    public void onScoreIncreased(int amount) {
        // Aquí puedes implementar la lógica para aumentar la puntuación del jugador
        // Por ejemplo: game.addScore(amount);
        Gdx.app.log("TiledMapScreen", "Score increased by: " + amount);
        // Necesitarás una variable de puntuación, quizás en MyGdxGame o aquí.
    }

    @Override
    public void onPlayerHealed(int amount) {
        // Aquí puedes implementar la lógica para curar al personaje
        // Por ejemplo: personaje.curar(amount);
        Gdx.app.log("TiledMapScreen", "Player healed by: " + amount);
    }

    @Override
    public void onNewFloatingText(TextoFlotante text) {
        // EntityManager ya añade el texto a su lista interna, así que no necesitamos hacer nada aquí
        // a menos que TiledMapScreen necesite una referencia directa a todos los textos flotantes.
        // Por ahora, solo lo logueamos.
        //Gdx.app.log("TiledMapScreen", "New floating text: " + text.getText());

    }

    @Override
    public void onPlayerComponentChanged(ComponentePersonaje newPlayerComponent) {
        // Esto es crucial: actualizar la instancia del personaje en TiledMapScreen
        // cuando se aplica un decorador (ej. VidaRegeneracionDecorator)
        this.personaje = (Personaje) newPlayerComponent;
        Gdx.app.log("TiledMapScreen", "Player component changed (e.g., decorator applied)");
    }
}
