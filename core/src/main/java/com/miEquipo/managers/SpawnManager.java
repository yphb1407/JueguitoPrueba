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
    private float tiempoSpawnEnemigo = 2f;
    private float timerItem;
    private float proximoItemEn;

    private static final String ENEMY_TYPE_GOOMBA = "goomba";
    private static final float ENEMY_SPAWN_HEIGHT_OFFSET = 100f;

    public SpawnManager(EntityManager entityManager, EnemigoFactory enemigoFactory, float worldWidth, float worldHeight) {
        this.entityManager = entityManager;
        this.enemigoFactory = enemigoFactory;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.proximoItemEn = MathUtils.random(10, 30);
    }

    public void update(float delta) {
        handleEnemySpawning(delta);
        handleItemSpawning(delta);
    }

    private void handleEnemySpawning(float delta) {
        timerEnemigos += delta;
        if (timerEnemigos >= tiempoSpawnEnemigo) {
            boolean desdeDerecha = MathUtils.randomBoolean();
            float spawnX = desdeDerecha ? worldWidth + 100 : -100;
            float velX = desdeDerecha ? -MathUtils.random(100, 200) : MathUtils.random(100, 200);
            float spawnY = worldHeight + ENEMY_SPAWN_HEIGHT_OFFSET;

            entityManager.addEnemigo(enemigoFactory.crearEnemigo(ENEMY_TYPE_GOOMBA, spawnX, spawnY, velX));
            timerEnemigos = 0;
            tiempoSpawnEnemigo = Math.max(0.7f, tiempoSpawnEnemigo - 0.02f);
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
