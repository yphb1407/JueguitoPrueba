package com.miEquipo.facade;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.miEquipo.Entidades.*;
import com.miEquipo.Factory.EnemigoFactory;
import com.miEquipo.Datos_y_almacen.ScoreManager;
import com.miEquipo.Decorador.VidaRegeneracionDecorator;
import com.miEquipo.patron_states.EstadoSaltando;
import com.miEquipo.managers.EntityManager;
import com.miEquipo.managers.SpawnManager;
import com.miEquipo.physics.MapCollisionHandler;

/**
 * Fachada principal para la lógica de la partida.
 * Orquesta la interacción entre los subsistemas (EntityManager, SpawnManager, Física).
 */
public class PartidaFacade implements Disposable, EntityManager.EntityManagerCallback {
    // --- Constants ---
    private static final int MAX_PLAYER_HEALTH = 500;
    private static final float GRAVITY = -1000f;
    private static final float JUMP_FORCE = 2000f;
    private static final float PLAYER_SPEED = 700f;
    private static final String TEXT_HEAL_FINISHED = "Curación terminada";

    // --- Sub-sistemas ---
    private ComponentePersonaje personaje;
    private final ScoreManager scoreManager;
    private final EntityManager entityManager;
    private final SpawnManager spawnManager;
    private final MapCollisionHandler collisionHandler;

    // --- Estado del Juego ---
    private int scoreActual;
    private int vida;
    private float worldWidth;
    private float worldHeight;
    private boolean onGround;

    public PartidaFacade(ScoreManager scoreManager, float worldWidth, float worldHeight,
                         TiledMap map, TiledMapTileLayer collisionLayer, int tileWidth, int tileHeight) {
        this.scoreManager = scoreManager;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.vida = 20;
        this.scoreActual = 0;

        this.collisionHandler = new MapCollisionHandler(map, collisionLayer, tileWidth, tileHeight);
        this.entityManager = new EntityManager(this);
        EnemigoFactory enemigoFactory = new EnemigoFactory(map, collisionLayer, tileWidth, tileHeight);
        this.spawnManager = new SpawnManager(entityManager, enemigoFactory, worldWidth, worldHeight);

        this.personaje = new Personaje(300, 300);
        this.onGround = false;
    }

    public void actualizarYEscucharEntradas(float delta) {
        if (isGameOver()) return;

        handleInput();
        applyPhysics(delta);

        spawnManager.update(delta, scoreActual); // <--- CAMBIO AQUÍ: Se pasa el scoreActual
        personaje.actualizar(delta);
        entityManager.update(delta, personaje, worldWidth);

        checkRegenerationDecoratorStatus();
    }

    private void handleInput() {
        Personaje realPersonaje = getRealPersonaje(personaje);
        if (realPersonaje == null) return;

        float currentVelX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) currentVelX = -PLAYER_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) currentVelX = PLAYER_SPEED;
        realPersonaje.setVelocidadX(currentVelX);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && onGround) {
            realPersonaje.setEstado(new EstadoSaltando());
            realPersonaje.setVelocidadY(JUMP_FORCE);
            onGround = false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            realPersonaje.iniciarAtaque(null);
            entityManager.addProyectil(realPersonaje.lanzarProyectil(worldWidth));
        }
    }

    private void applyPhysics(float delta) {
        Personaje realPersonaje = getRealPersonaje(personaje);
        if (realPersonaje == null) return;

        // Gravedad
        realPersonaje.setVelocidadY(realPersonaje.getVelocidadY() + GRAVITY * delta);

        // Movimiento Horizontal
        float oldX = realPersonaje.getX();
        float newX = oldX + realPersonaje.getVelocidadX() * delta;
        realPersonaje.setX(Math.max(0, Math.min(worldWidth - realPersonaje.getWidth(), newX)));

        if (collisionHandler.collides(new Rectangle(realPersonaje.getX(), realPersonaje.getY(), realPersonaje.getWidth(), realPersonaje.getHeight()))) {
            realPersonaje.setX(oldX);
        }

        // Movimiento Vertical
        float oldY = realPersonaje.getY();
        float newY = oldY + realPersonaje.getVelocidadY() * delta;
        realPersonaje.setY(newY);

        // Suelo básico (Y=0)
        if (realPersonaje.getY() < 0) {
            realPersonaje.setY(0);
            realPersonaje.setVelocidadY(0);
            onGround = true;
        }

        // Colisión con mapa (Vertical)
        if (collisionHandler.collides(new Rectangle(realPersonaje.getX(), realPersonaje.getY(), realPersonaje.getWidth(), realPersonaje.getHeight()))) {
            realPersonaje.setY(oldY);
            if (realPersonaje.getVelocidadY() < 0) onGround = true;
            realPersonaje.setVelocidadY(0);
        }

        // Ground Check
        onGround = collisionHandler.collides(new Rectangle(realPersonaje.getX(), realPersonaje.getY() - 1, realPersonaje.getWidth(), 1)) || realPersonaje.getY() <= 0;
    }

    private void checkRegenerationDecoratorStatus() {
        if (personaje instanceof VidaRegeneracionDecorator) {
            VidaRegeneracionDecorator decorator = (VidaRegeneracionDecorator) personaje;
            if (decorator.isTerminado()) {
                this.personaje = decorator.getPersonajeOriginal();
                Personaje realP = getRealPersonaje(this.personaje);
                if (realP != null) {
                    entityManager.addTextoFlotante(new TextoFlotante(TEXT_HEAL_FINISHED, realP.getX(), realP.getY() + 100, Color.WHITE));
                }
            }
        }
    }

    private Personaje getRealPersonaje(ComponentePersonaje comp) {
        if (comp instanceof Personaje) return (Personaje) comp;
        if (comp instanceof VidaRegeneracionDecorator) return getRealPersonaje(((VidaRegeneracionDecorator) comp).getPersonajeOriginal());
        return null;
    }

    public void renderizarMundo(SpriteBatch batch, BitmapFont font) {
        entityManager.render(batch, font);
        personaje.dibujar(batch);
        renderUI(batch, font);
        if (isGameOver()) renderGameOverScreen(batch, font);
    }

    private void renderUI(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.WHITE);
        font.draw(batch, "VIDA: " + vida, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "SCORE: " + scoreActual, 20, Gdx.graphics.getHeight() - 40);
        font.setColor(Color.GOLD);
        font.draw(batch, "MAX: " + scoreManager.getHighestScore(), 20, Gdx.graphics.getHeight() - 60);
    }

    private void renderGameOverScreen(SpriteBatch batch, BitmapFont font) {
        font.setColor(Color.RED);
        font.draw(batch, "GAME OVER", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f + 50);
    }

    public boolean isGameOver() { return vida <= 0; }
    public int getScoreActual() { return scoreActual; }

    @Override
    public void dispose() {
        entityManager.dispose();
        if (personaje instanceof Disposable) ((Disposable) personaje).dispose();
    }

    @Override public void onPlayerDamaged(int amount) { vida -= amount; }
    @Override public void onScoreIncreased(int amount) { scoreActual += amount; }
    @Override public void onPlayerHealed(int amount) { vida = Math.min(MAX_PLAYER_HEALTH, vida + amount); }
    @Override public void onNewFloatingText(TextoFlotante text) {}
    @Override public void onPlayerComponentChanged(ComponentePersonaje newComp) { this.personaje = newComp; }
}
