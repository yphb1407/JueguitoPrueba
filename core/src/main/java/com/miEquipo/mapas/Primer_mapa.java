package com.miEquipo.mapas;

import com.miEquipo.Entidades.Personaje;
import com.miEquipo.patron_states.EstadoQuieto;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array; // Importar Array de LibGDX

import java.util.Arrays;
import java.util.List;

/**
 * Implementación concreta de Mapa_padre para el primer mapa del juego.
 * Define la ruta del archivo TMX, los tiles colisionables específicos,
 * y las propiedades del jugador y enemigos para este mapa.
 */
public class Primer_mapa extends Mapa_padre {

    private static final String mapatmx = "mapa/mapa1/segundo_intento_de_mapa.tmx";
    // mapatsx and mapapng are not directly used by Mapa_padre, but kept for potential future use.
    private static final String mapatsx = "mapa/mapa1/segundo_intento_de_mapa.tsx";
    private static final String mapapng = "mapa/mapa1/segundo_intento_de_mapa.png";

    // List of tile IDs that are considered solid/collidable for this map.
    private final List<Integer> solidTileIds = Arrays.asList(1, 2); // Example: tiles with ID 1 and 2 are solid.

    // Propiedades del jugador específicas para este mapa
    private static final float PLAYER_SPEED = 400f;
    private static final float GRAVITY = -1200f;
    private static final float JUMP_FORCE = 600f; // Ajustado a un valor más común

    // Posición de spawn del jugador definida directamente en el código
    private static final Vector2 PLAYER_INITIAL_SPAWN = new Vector2(1400, 300);

    // Puntos de spawn de enemigos definidos directamente en el código
    private final Array<Vector2> enemySpawnPoints = new Array<>(new Vector2[]{
        new Vector2(2500, 1500), // Primer enemigo
        new Vector2(800, 200), // Segundo enemigo
        new Vector2(1200, 150) // Tercer enemigo
    });

    public Primer_mapa() {
        // Constructor vacío. La carga del mapa se hará en TiledMapScreen.show()
    }

    @Override
    public String getMapPath() {
        return mapatmx;
    }

    @Override
    public boolean isTileSolid(int tileId) {
        return solidTileIds.contains(tileId);
    }

    @Override
    public float getPlayerSpeed() {
        return PLAYER_SPEED;
    }

    @Override
    public float getGravity() {
        return GRAVITY;
    }

    @Override
    public float getJumpForce() {
        return JUMP_FORCE;
    }

    @Override
    public Personaje createPlayer() {
        // Crea el personaje con la posición de spawn definida aquí
        Personaje personaje = new Personaje(PLAYER_INITIAL_SPAWN.x, PLAYER_INITIAL_SPAWN.y);
        personaje.setEstado(new EstadoQuieto()); // Establecer el estado inicial
        // Aquí podrías configurar otras propiedades iniciales del personaje si las hubiera
        return personaje;
    }

    @Override
    public Array<Vector2> getEnemySpawnPoints() {
        return enemySpawnPoints;
    }
}
