package com.miEquipo.facade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
    private static final float GROUND_Y = 64;
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
    private static final String TEXTURE_TERRAIN = "terreno.png";
    private static final String TEXT_HEAL_FINISHED = "Curación terminada";

    // --- Fields ---
    private ComponentePersonaje personaje;
    private final ScoreManager scoreManager;
    private EntityManager entityManager;

    private int scoreActual;
    private int vida;

    private final Texture texturaTerreno;

    // Temporizadores de spawn (se moverán a SpawnManager en el futuro)
    private float timerEnemigos;
    private float tiempoSpawnEnemigo = 2f;
    private float timerItem;
    private float proximoItemEn = MathUtils.random(10, 30);

    /**
     * Constructor de la fachada de la partida.
     * @param scoreManager Gestor de puntuaciones del juego.
     */
    public PartidaFacade(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.personaje = new Personaje(140, GROUND_Y);
        this.texturaTerreno = new Texture(TEXTURE_TERRAIN);

        this.scoreActual = 0;
        this.vida = 20;

        this.entityManager = new EntityManager(this);

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

        handleEnemySpawning(delta);
        handleItemSpawning(delta);
        handlePlayerInput();

        personaje.actualizar(delta);
        checkRegenerationDecoratorStatus();

        entityManager.update(delta, GROUND_Y, personaje);
    }

    /**
     * Gestiona la lógica de generación de enemigos.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    private void handleEnemySpawning(float delta) {
        timerEnemigos += delta;
        if (timerEnemigos >= tiempoSpawnEnemigo) {
            boolean desdeDerecha = MathUtils.randomBoolean();
            float spawnX = desdeDerecha ? Gdx.graphics.getWidth() + 100 : -100;
            float velX = desdeDerecha ? -MathUtils.random(100, 200) : MathUtils.random(100, 200);
            entityManager.addEnemigo(EnemigoFactory.crearEnemigo(ENEMY_TYPE_GOOMBA, spawnX, GROUND_Y, velX));
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
            float spawnX = MathUtils.random(50, Gdx.graphics.getWidth() - 50);
            entityManager.addItem(new ItemRegeneracion(spawnX, Gdx.graphics.getHeight() + 50));
            timerItem = 0;
            proximoItemEn = MathUtils.random(20, 40);
        }
    }

    /**
     * Procesa las entradas del jugador.
     */
    private void handlePlayerInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && personaje.getY() <= GROUND_Y + PLAYER_JUMP_THRESHOLD) {
            personaje.setEstado(new EstadoSaltando());
            personaje.setVelocidadY(650);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Personaje realPersonaje = getRealPersonaje(personaje);
            if (realPersonaje != null) {
                realPersonaje.iniciarAtaque(() -> {
                    entityManager.addProyectil(new Proyectil(personaje.getX(), personaje.getY() + PROJECTILE_SPAWN_OFFSET_Y, personaje.isMirandoDerecha()));
                });
            }
        }
    }

    /**
     * Verifica y gestiona el estado del decorador de regeneración del personaje.
     */
    private void checkRegenerationDecoratorStatus() {
        if (personaje instanceof VidaRegeneracionDecorator) {
            VidaRegeneracionDecorator decorator = (VidaRegeneracionDecorator) personaje;
            if (decorator.isTerminado()) {
                personaje = decorator.getPersonajeOriginal();
                entityManager.addTextoFlotante(new TextoFlotante(TEXT_HEAL_FINISHED, personaje.getX(), personaje.getY() + 100, Color.WHITE));
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
        renderTerrain(batch);
        entityManager.render(batch, font);
        personaje.dibujar(batch);
        renderUI(batch, font);
        renderGameOverScreen(batch, font);
    }

    /**
     * Renderiza el terreno del juego.
     * @param batch El SpriteBatch para dibujar.
     */
    private void renderTerrain(SpriteBatch batch) {
        for (int i = 0; i < Gdx.graphics.getWidth(); i += 64) {
            batch.draw(texturaTerreno, i, 0, 64, 64);
        }
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
        texturaTerreno.dispose();
        entityManager.dispose();
        EnemigoFactory.disposeAll();
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
