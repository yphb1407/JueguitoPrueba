package com.miEquipo.managers;

import com.badlogic.gdx.math.MathUtils;
import com.miEquipo.Entidades.ItemRegeneracion;
import com.miEquipo.Factory.EnemigoFactory;

/**
 * Gestiona la generación de enemigos e ítems en el juego.
 * Sigue el principio de Responsabilidad Única (SRP).
 */
public class SpawnManager {
    private final EntityManager entityManager;
    private final EnemigoFactory enemigoFactory;
    private final float worldWidth;
    private final float worldHeight;

    private float timerEnemigos;
    // private float tiempoSpawnEnemigo = 2f; // Ya no se usará directamente, se calculará
    private float timerItem;
    private float proximoItemEn;

    private static final String ENEMY_TYPE_GOOMBA = "goomba";
    private static final float ENEMY_SPAWN_HEIGHT_OFFSET = 100f;

    // Nuevas constantes para la dificultad
    private static final float BASE_ENEMY_SPAWN_INTERVAL = 2.0f; // Tiempo inicial entre spawns de enemigos
    private static final int SCORE_THRESHOLD_FOR_DIFFICULTY = 1000; // Cada cuántos puntos aumenta la dificultad
    private static final float MIN_ENEMY_SPAWN_INTERVAL = 0.2f; // Tiempo mínimo entre spawns de enemigos

    public SpawnManager(EntityManager entityManager, EnemigoFactory enemigoFactory, float worldWidth, float worldHeight) {
        this.entityManager = entityManager;
        this.enemigoFactory = enemigoFactory;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.proximoItemEn = MathUtils.random(10, 30);
    }

    /**
     * Actualiza el manager de spawns, gestionando la aparición de enemigos e ítems.
     * @param delta El tiempo transcurrido desde el último frame.
     * @param score El puntaje actual del jugador, usado para ajustar la dificultad.
     */
    public void update(float delta, int score) {
        handleEnemySpawning(delta, score);
        handleItemSpawning(delta);
    }

    private void handleEnemySpawning(float delta, int score) {
        timerEnemigos += delta;

        // Calcular el nivel de dificultad basado en el score
        int difficultyLevel = score / SCORE_THRESHOLD_FOR_DIFFICULTY;
        // Calcular el tiempo de spawn actual: se reduce a la mitad por cada nivel de dificultad
        float currentSpawnInterval = BASE_ENEMY_SPAWN_INTERVAL / (float) Math.pow(2, difficultyLevel);

        // Asegurar que el tiempo de spawn no sea menor que el mínimo
        currentSpawnInterval = Math.max(MIN_ENEMY_SPAWN_INTERVAL, currentSpawnInterval);

        if (timerEnemigos >= currentSpawnInterval) {
            boolean desdeDerecha = MathUtils.randomBoolean();
            float spawnX = desdeDerecha ? worldWidth + 100 : -100;
            float velX = desdeDerecha ? -MathUtils.random(100, 200) : MathUtils.random(100, 200);
            float spawnY = worldHeight + ENEMY_SPAWN_HEIGHT_OFFSET;

            entityManager.addEnemigo(enemigoFactory.crearEnemigo(ENEMY_TYPE_GOOMBA, spawnX, spawnY, velX));
            timerEnemigos = 0;
            // La línea de ajuste de tiempoSpawnEnemigo se elimina, ya que ahora se calcula dinámicamente
            // tiempoSpawnEnemigo = Math.max(0.7f, tiempoSpawnEnemigo - 0.02f);
        }
    }

    private void handleItemSpawning(float delta) {
        timerItem += delta;
        if (timerItem >= proximoItemEn) {
            float spawnX = MathUtils.random(50, worldWidth - 50);
            entityManager.addItem(new ItemRegeneracion(spawnX, worldHeight + 50));
            timerItem = 0;
            proximoItemEn = MathUtils.random(20, 40);
        }
    }
}
