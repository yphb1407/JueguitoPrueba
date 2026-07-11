package com.miEquipo.Factory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import com.miEquipo.Entidades.Enemigo;
import com.miEquipo.Entidades.EnemigoCaminante;
import java.util.HashMap;
import java.util.Map;

/**
 * Fábrica para crear instancias de enemigos utilizando el patrón Prototype.
 * Permite la creación eficiente de enemigos clonando prototipos predefinidos.
 */
public class EnemigoFactory { // Removed 'implements Disposable'
    private static final String ENEMY_TYPE_GOOMBA = "goomba";
    private static final float DEFAULT_ENEMY_SPEED = -100f;

    // Mapa de prototipos de enemigos
    private static final Map<String, Enemigo> prototypes = new HashMap<>();

    // Inicializa los prototipos en un bloque estático
    static {
        // Aquí se pueden añadir diferentes tipos de enemigos como prototipos
        // Por ejemplo, un "goomba" base
        prototypes.put(ENEMY_TYPE_GOOMBA, new EnemigoCaminante());
    }

    /**
     * Crea una nueva instancia de Enemigo del tipo especificado,
     * clonando un prototipo y configurando su posición y velocidad.
     * @param tipo El tipo de enemigo a crear (ej. "goomba").
     * @param x La coordenada X inicial del enemigo.
     * @param y La coordenada Y inicial del enemigo.
     * @param velX La velocidad inicial en el eje X del enemigo.
     * @return Una nueva instancia de Enemigo, o null si el tipo no es reconocido.
     */
    public static Enemigo crearEnemigo(String tipo, float x, float y, float velX) {
        Enemigo prototype = prototypes.get(tipo);
        if (prototype != null) {
            Enemigo newEnemy = prototype.clone();
            newEnemy.setPosicion(x, y);
            newEnemy.velocidadX = velX; // Accessing protected field, consider adding a setter in Enemigo
            return newEnemy;
        }
        Gdx.app.error("EnemigoFactory", "Tipo de enemigo no reconocido: " + tipo);
        return null;
    }

    /**
     * Crea una nueva instancia de Enemigo del tipo especificado,
     * clonando un prototipo y configurando su posición con una velocidad por defecto.
     * @param tipo El tipo de enemigo a crear (ej. "goomba").
     * @param x La coordenada X inicial del enemigo.
     * @param y La coordenada Y inicial del enemigo.
     * @return Una nueva instancia de Enemigo, o null si el tipo no es reconocido.
     */
    public static Enemigo crearEnemigo(String tipo, float x, float y) {
        return crearEnemigo(tipo, x, y, DEFAULT_ENEMY_SPEED);
    }

    /**
     * Libera los recursos de todos los prototipos de enemigos.
     * Debe llamarse al finalizar el juego para evitar fugas de memoria.
     */
    public static void disposeAll() { // Changed to static
        for (Enemigo enemy : prototypes.values()) {
            enemy.dispose();
        }
        prototypes.clear();
    }
}
