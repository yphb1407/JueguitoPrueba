package com.miEquipo.facade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle; // Importar Rectangle
import com.miEquipo.Entidades.*;
import com.miEquipo.Factory.EnemigoFactory;
import com.miEquipo.Datos_y_almacen.ScoreManager;
import com.miEquipo.Decorador.VidaRegeneracionDecorator;
import com.miEquipo.patron_states.EstadoSaltando;
import com.badlogic.gdx.utils.Disposable;
import com.miEquipo.managers.EntityManager;

/**
 * Fachada principal para la lógica de la partida.
 * Orquesta la interacción entre el personaje, las entidades del juego, la gestión de puntuaciones y la interfaz de usuario.
 */
public class PartidaFacade implements Disposable, EntityManager.EntityManagerCallback {
    // --- Constants ---
    private static final int PLAYER_JUMP_THRESHOLD = 10;
    private static final int PROJECTILE_SPAWN_OFFSET_Y = 20;
    private static final int MAX_PLAYER_HEALTH = 500;
    private static final int UI_PADDING_X = 20;
    private static final int UI_HEALTH_POS_Y = 20;
    private static final int UI_SCORE_POS_Y = 40;
    private static final int UI_MAX_SCORE_POS_Y = 60;
    private static final String GAME_OVER_TITLE = "GAME OVER";
    private static final String GAME_OVER_PROMPT = "Ingresa tu nombre para el ranking";
    private static final String GAME_OVER_INPUT_TITLE = "GAME OVER! Ingresa tu nombre:";
    private static final String GAME_OVER_INPUT_HINT = "Tu nombre";
    private static final String ANONYMOUS_PLAYER_NAME = "Anonimo";
    private static final String ENEMY_TYPE_GOOMBA = "goomba";
    private static final String TEXT_HEAL_FINISHED = "Curación terminada";
    private static final float ENEMY_SPAWN_HEIGHT_OFFSET = 100; // Altura por encima del worldHeight para spawnear enemigos

    // --- Fields ---
    private ComponentePersonaje personaje; // Cambiado a ComponentePersonaje
    private final ScoreManager scoreManager;
    private EntityManager entityManager;
    private EnemigoFactory enemigoFactory; // Instancia de la fábrica de enemigos
    private float worldWidth;
    private float worldHeight;

    // Propiedades del mapa Tiled
    private TiledMap map;
    private TiledMapTileLayer collisionLayer;
    private int tileWidth;
    private int tileHeight;

    private int scoreActual;
    private int vida;

    private float playerSpeed = 700; // Velocidad del personaje
    private float gravity = -1000f; // Gravedad
    private float JUMP_FORCE = 2000f; // Fuerza del salto
    private boolean onGround; // Indica si el jugador está en el suelo

    // Temporizadores de spawn (se moverán a SpawnManager en el futuro)
    private float timerEnemigos;
    private float tiempoSpawnEnemigo = 2f;
    private float timerItem;
    private float proximoItemEn = MathUtils.random(10, 30);

    /**
     * Constructor de la fachada de la partida.
     * @param scoreManager Gestor de puntuaciones del juego.
     * @param worldWidth El ancho del mundo del juego.
     * @param worldHeight El alto del mundo del juego.
     * @param map El TiledMap del juego.
     * @param collisionLayer La capa de colisión del TiledMap.
     * @param tileWidth El ancho de los tiles del mapa.
     * @param tileHeight El alto de los tiles del mapa.
     */
    public PartidaFacade(ScoreManager scoreManager, float worldWidth, float worldHeight,
                         TiledMap map, TiledMapTileLayer collisionLayer, int tileWidth, int tileHeight) {
        this.scoreManager = scoreManager;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.map = map;
        this.collisionLayer = collisionLayer;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        // Log the world dimensions
        Gdx.app.log("PartidaFacade", "World Dimensions: " + worldWidth + "x" + worldHeight);


        this.personaje = new Personaje(300, 300); // Initializing with a base Personaje
        this.onGround = false; // Asumimos que no está en el suelo al inicio hasta que se verifique

        this.scoreActual = 0;
        this.vida = 20;

        this.entityManager = new EntityManager(this);
        this.enemigoFactory = new EnemigoFactory(map, collisionLayer, tileWidth, tileHeight); // Inicializar la fábrica

        // Añadir los dos enemigos iniciales
        // Spawneamos los enemigos a una altura por encima del worldHeight para que caigan por gravedad
        // Un enemigo desde la derecha, otro desde la izquierda
        entityManager.addEnemigo(enemigoFactory.crearEnemigo(ENEMY_TYPE_GOOMBA, worldWidth + 100, worldHeight + ENEMY_SPAWN_HEIGHT_OFFSET, -100)); // Desde la derecha, moviéndose a la izquierda
        entityManager.addEnemigo(enemigoFactory.crearEnemigo(ENEMY_TYPE_GOOMBA, -100, worldHeight + ENEMY_SPAWN_HEIGHT_OFFSET, 100)); // Desde la izquierda, moviéndose a la derecha


        this.timerEnemigos = 0;
        this.tiempoSpawnEnemigo = 2f;
        this.timerItem = 0;
        this.proximoItemEn = MathUtils.random(10, 30);
    }

    /**
     * Actualiza la lógica del juego y procesa las entradas del usuario.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    public void actualizarYEscucharEntradas(float delta) {
        if (isGameOver()) {
            return;
        }

        Personaje realPersonaje = getRealPersonaje(personaje);
        if (realPersonaje == null) {
            Gdx.app.error("PartidaFacade", "realPersonaje is null in actualizarYEscucharEntradas.");
            return;
        }

        // --- Player Input (movimiento horizontal) ---
        float currentVelX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            currentVelX = -playerSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            currentVelX = playerSpeed;
        }
        realPersonaje.setVelocidadX(currentVelX);

        // --- Apply Gravity and Jump ---
        realPersonaje.setVelocidadY(realPersonaje.getVelocidadY() + gravity * delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            realPersonaje.setEstado(new EstadoSaltando());
            realPersonaje.setVelocidadY(JUMP_FORCE);
            onGround = false; // El jugador ya no está en el suelo al saltar
        }

        // --- Store Old Position ---
        float oldPersonajeX = realPersonaje.getX();
        float oldPersonajeY = realPersonaje.getY();

        // --- Predicted Movement ---
        float newPersonajeX = realPersonaje.getX() + realPersonaje.getVelocidadX() * delta;
        float newPersonajeY = realPersonaje.getY() + realPersonaje.getVelocidadY() * delta;

        // --- Horizontal Movement and Collision ---
        realPersonaje.setX(newPersonajeX); // Mover horizontalmente
        // Clamp a los límites del mapa (horizontal)
        if (realPersonaje.getX() < 0) realPersonaje.setX(0);
        if (realPersonaje.getX() > worldWidth - realPersonaje.getWidth())
            realPersonaje.setX(worldWidth - realPersonaje.getWidth());

        Rectangle playerBounds = new Rectangle(realPersonaje.getX(), realPersonaje.getY(), realPersonaje.getWidth(), realPersonaje.getHeight());
        if (collidesWithMap(playerBounds)) {
            // Colisión detectada horizontalmente
            realPersonaje.setX(oldPersonajeX); // Revertir a la posición X anterior
            realPersonaje.setVelocidadX(0); // Detener movimiento horizontal
        }

        // --- Vertical Movement and Collision ---
        realPersonaje.setY(newPersonajeY); // Mover verticalmente
        // Clamp a los límites del mapa (vertical)
        if (realPersonaje.getY() < 0) {
            realPersonaje.setY(0);
            realPersonaje.setVelocidadY(0);
            onGround = true;
        }
        if (realPersonaje.getY() > worldHeight - realPersonaje.getHeight()) {
            realPersonaje.setY(worldHeight - realPersonaje.getHeight());
            realPersonaje.setVelocidadY(0); // Choca con el techo
        }

        playerBounds.setPosition(realPersonaje.getX(), realPersonaje.getY()); // Actualizar playerBounds para la verificación vertical
        if (collidesWithMap(playerBounds)) {
            // Colisión detectada verticalmente
            realPersonaje.setY(oldPersonajeY); // Revertir a la posición Y anterior
            realPersonaje.setVelocidadY(0); // Detener movimiento vertical
            if (newPersonajeY < oldPersonajeY) { // Si el jugador estaba cayendo
                onGround = true;
            }
        }

        // --- Final onGround check ---
        // Esta verificación es crucial y debe hacerse después de todos los movimientos y colisiones verticales.
        // Comprueba si hay un tile sólido directamente debajo del jugador.
        Rectangle groundCheckRect = new Rectangle(realPersonaje.getX(), realPersonaje.getY() - 1, realPersonaje.getWidth(), 1);
        onGround = collidesWithMap(groundCheckRect);


        handleEnemySpawning(delta);
        handleItemSpawning(delta);
        handlePlayerInput(); // Este método ahora solo maneja el ataque

        personaje.actualizar(delta); // This calls actualizar on the ComponentePersonaje (decorator or base)
        checkRegenerationDecoratorStatus();

        // EntityManager ya no necesita GROUND_Y, ya que la colisión del personaje se maneja aquí.
        // Si EntityManager necesita un "suelo" para otras entidades, debería calcularlo por sí mismo o recibirlo de otra forma.
        entityManager.update(delta, personaje, this.worldWidth); // ¡Paso 2 completado! Se pasa worldWidth.
    }

    /**
     * Gestiona la lógica de generación de enemigos.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    private void handleEnemySpawning(float delta) {
        timerEnemigos += delta;
        if (timerEnemigos >= tiempoSpawnEnemigo) {
            boolean desdeDerecha = MathUtils.randomBoolean(); // Aleatorio: izquierda o derecha
            // Usar worldWidth para el spawn de enemigos
            float spawnX = desdeDerecha ? worldWidth + 100 : -100;
            // Velocidad X: negativa si viene de la derecha, positiva si viene de la izquierda
            float velX = desdeDerecha ? -MathUtils.random(100, 200) : MathUtils.random(100, 200);
            // Spawneamos los enemigos a una altura por encima del worldHeight para que caigan por gravedad
            float spawnY = worldHeight + ENEMY_SPAWN_HEIGHT_OFFSET;

            entityManager.addEnemigo(enemigoFactory.crearEnemigo(ENEMY_TYPE_GOOMBA, spawnX, spawnY, velX));
            timerEnemigos = 0;
            tiempoSpawnEnemigo = Math.max(0.7f, tiempoSpawnEnemigo - 0.02f);
        }
    }

    /**
     * Gestiona la lógica de generación de ítems.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    private void handleItemSpawning(float delta) {
        timerItem += delta;
        if (timerItem >= proximoItemEn) {
            // Usar worldWidth y worldHeight para el spawn de ítems
            float spawnX = MathUtils.random(50, worldWidth - 50);
            // Para los ítems, podemos spawnearlos un poco más alto y dejar que caigan
            entityManager.addItem(new ItemRegeneracion(spawnX, worldHeight + 50));
            timerItem = 0;
            proximoItemEn = MathUtils.random(20, 40);
        }
    }

    /**
     * Procesa las entradas del jugador (solo ataque ahora).
     */
    private void handlePlayerInput() {
        // La lógica de movimiento y salto se ha movido a actualizarYEscucharEntradas
        Personaje realPersonaje = getRealPersonaje(personaje);
        if (realPersonaje == null) {
            Gdx.app.error("PartidaFacade", "realPersonaje is null in handlePlayerInput.");
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) { // Usamos 'Z' como tecla de ataque
            realPersonaje.iniciarAtaque(null); // Iniciar animación de ataque (sin listener por ahora)
            // Pasa worldWidth al constructor de Proyectil
            entityManager.addProyectil(new Proyectil(realPersonaje.getX(), realPersonaje.getY() + PROJECTILE_SPAWN_OFFSET_Y, realPersonaje.isMirandoDerecha(), worldWidth));
        }
    }

    /**
     * Verifica y gestiona el estado del decorador de regeneración del personaje.
     */
    private void checkRegenerationDecoratorStatus() {
        if (personaje instanceof VidaRegeneracionDecorator) {
            VidaRegeneracionDecorator decorator = (VidaRegeneracionDecorator) personaje;
            if (decorator.isTerminado()) {
                // The decorator has finished its effect. Revert to the original Personaje.
                this.personaje = decorator.getPersonajeOriginal(); // Revert to the base Personaje
                Personaje realP = getRealPersonaje(this.personaje); // Get the real Personaje for coordinates
                if (realP != null) {
                    entityManager.addTextoFlotante(new TextoFlotante(TEXT_HEAL_FINISHED, realP.getX(), realP.getY() + 100, Color.WHITE));
                }
            }
        }
    }

    /**
     * Obtiene la instancia real del personaje, desenvolviendo decoradores si los hay.
     * @param comp El componente de personaje actual.
     * @return La instancia base del personaje.
     */
    private Personaje getRealPersonaje(ComponentePersonaje comp) {
        if (comp instanceof Personaje) {
            return (Personaje) comp;
        }
        if (comp instanceof VidaRegeneracionDecorator) {
            return getRealPersonaje(((VidaRegeneracionDecorator) comp).getPersonajeOriginal());
        }
        return null;
    }

    /**
     * Renderiza todos los elementos del mundo del juego.
     * @param batch El SpriteBatch para dibujar.
     * @param font La fuente para dibujar textos.
     */
    public void renderizarMundo(SpriteBatch batch, BitmapFont font) {
        entityManager.render(batch, font);
        personaje.dibujar(batch);
        renderUI(batch, font);
        renderGameOverScreen(batch, font);
    }

    /**
     * Renderiza la interfaz de usuario (vida, puntuación).
     * @param batch El SpriteBatch para dibujar.
     * @param font La fuente para dibujar textos.
     */
    private void renderUI(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.WHITE);
        font.draw(batch, "VIDA: " + vida, UI_PADDING_X, Gdx.graphics.getHeight() - UI_HEALTH_POS_Y);
        font.draw(batch, "SCORE: " + scoreActual, UI_PADDING_X, Gdx.graphics.getHeight() - UI_SCORE_POS_Y);
        font.setColor(Color.GOLD);
        font.draw(batch, "MAX: " + scoreManager.getHighestScore(), UI_PADDING_X, Gdx.graphics.getHeight() - UI_MAX_SCORE_POS_Y);
    }

    /**
     * Renderiza la pantalla de "Game Over" si el juego ha terminado.
     * @param batch El SpriteBatch para dibujar.
     * @param font La fuente para dibujar textos.
     */
    private void renderGameOverScreen(SpriteBatch batch, BitmapFont font) {
        if (isGameOver()) {
            font.setColor(Color.RED);
            font.draw(batch, GAME_OVER_TITLE, Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f + 50);
            font.draw(batch, GAME_OVER_PROMPT, Gdx.graphics.getWidth() / 2f - 150, Gdx.graphics.getHeight() / 2f);
        }
    }

    /**
     * Verifica si el juego ha terminado.
     * @return true si la vida del personaje es menor o igual a 0, false en caso contrario.
     */
    public boolean isGameOver() {
        return vida <= 0;
    }

    /**
     * Obtiene la puntuación actual del jugador.
     * @return La puntuación actual.
     */
    public int getScoreActual() {
        return scoreActual;
    }

    /**
     * Libera los recursos utilizados por la fachada de la partida.
     */
    @Override
    public void dispose() {
        entityManager.dispose();
        enemigoFactory.dispose(); // Disponer la fábrica de enemigos
    }

    // --- Métodos de colisión con el mapa Tiled (copiados de TiledMapScreen) ---

    // Helper method to check if a tile ID is considered solid/collidable
    private boolean isTileSolid(int tileId) {
        // GID 1 y 2 son los tiles de colisión según tu descripción
        return tileId == 1 || tileId == 2;
    }

    // Helper method to check if a rectangle collides with any solid tile in the map
    private boolean collidesWithMap(Rectangle rect) {
        // Solo verificamos la capa de colisión que se pasó al constructor
        if (collisionLayer == null) return false;

        // Check the four corners of the rectangle for collision
        if (checkPointCollision(rect.x, rect.y) ||
            checkPointCollision(rect.x + rect.width, rect.y) ||
            checkPointCollision(rect.x, rect.y + rect.height) ||
            checkPointCollision(rect.x + rect.width, rect.y + rect.height)) {
            return true;
        }
        return false;
    }

    // Helper method to check if a specific point collides with a solid tile
    private boolean checkPointCollision(float x, float y) {
        int col = (int) (x / tileWidth);
        int row = (int) (y / tileHeight);

        // Ensure the point is within map bounds
        if (col < 0 || row < 0 || col >= map.getProperties().get("width", Integer.class) || row >= map.getProperties().get("height", Integer.class)) {
            return false;
        }

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(col, row);
        return cell != null && isTileSolid(cell.getTile().getId());
    }

    // --- Implementación de EntityManager.EntityManagerCallback ---
    /**
     * Callback invocado cuando el jugador recibe daño.
     * @param amount Cantidad de daño recibido.
     */
    @Override
    public void onPlayerDamaged(int amount) {
        this.vida -= amount;
    }

    /**
     * Callback invocado cuando la puntuación del jugador aumenta.
     * @param amount Cantidad de puntos añadidos.
     */
    @Override
    public void onScoreIncreased(int amount) {
        this.scoreActual += amount;
    }

    /**
     * Callback invocado cuando el jugador es curado.
     * @param amount Cantidad de vida curada.
     */
    @Override
    public void onPlayerHealed(int amount) {
        this.vida = Math.min(MAX_PLAYER_HEALTH, this.vida + amount);
    }

    /**
     * Callback invocado cuando se debe añadir un nuevo texto flotante.
     * @param text El objeto TextoFlotante a añadir.
     */
    @Override
    public void onNewFloatingText(TextoFlotante text) {
        // EntityManager ya gestiona y renderiza los textos flotantes internamente.
        // PartidaFacade no necesita hacer nada directamente con esta notificación.
    }

    /**
     * Callback invocado cuando el componente del personaje cambia (ej. por un decorador).
     * @param newPlayerComponent El nuevo componente de personaje.
     */
    @Override
    public void onPlayerComponentChanged(ComponentePersonaje newPlayerComponent) {
        this.personaje = newPlayerComponent;
    }
}
