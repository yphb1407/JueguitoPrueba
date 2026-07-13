package com.miEquipo.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.miEquipo.Entidades.*;
import com.miEquipo.Decorador.VidaRegeneracionDecorator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Gestiona el ciclo de vida (actualización, renderizado, colisiones) de las entidades del juego
 * como enemigos, proyectiles, ítems de regeneración y textos flotantes.
 */
public class EntityManager implements Disposable {
    // --- Constants (consider centralizing these later if many managers need them) ---
    private static final int PROJECTILE_ENEMY_COLLISION_THRESHOLD = 40;
    private static final int ENEMY_SCORE_VALUE = 100;
    private static final int ENEMY_PLAYER_COLLISION_THRESHOLD_X = 35;
    private static final int ENEMY_PLAYER_COLLISION_THRESHOLD_Y = 40;
    private static final int PLAYER_DAMAGE_VALUE = 10;
    private static final int MAX_PLAYER_HEALTH = 500; // Used for healing cap
    private static final int ENEMY_DESPAWN_OFFSET = 300;
    private static final String TEXT_HEALTH_GAIN_PREFIX = "+";
    private static final String TEXT_HEALTH_GAIN_SUFFIX = " HP";
    private static final String TEXT_HEALTH_LOSS = "-10 HP";
    private static final String TEXT_SCORE_GAIN = "+100";
    private static final String TEXT_ITEM_COLLECTED = "Item recogido";

    private final List<Enemigo> enemigos;
    private final List<Proyectil> proyectiles;
    private final List<TextoFlotante> textos;
    private final List<ItemRegeneracion> items;
    /**
     * Interfaz de callback para que EntityManager notifique a su "dueño" (ej. PartidaFacade)
     * sobre interacciones importantes que afectan el estado del juego.
     */
    public interface EntityManagerCallback {
        void onPlayerDamaged(int amount);
        void onScoreIncreased(int amount);
        void onPlayerHealed(int amount);
        void onNewFloatingText(TextoFlotante text);
        void onPlayerComponentChanged(ComponentePersonaje newPlayerComponent); // When a decorator is applied
    }
    private EntityManagerCallback callback;
    public EntityManager(EntityManagerCallback callback) {
        this.enemigos = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        this.textos = new ArrayList<>();
        this.items = new ArrayList<>();
        this.callback = callback;
    }

    public void addEnemigo(Enemigo enemigo) {
        enemigos.add(enemigo);
    }

    public void addProyectil(Proyectil proyectil) {
        proyectiles.add(proyectil);
    }

    public void addItem(ItemRegeneracion item) {
        items.add(item);
    }

    public void addTextoFlotante(TextoFlotante texto) {
        textos.add(texto);
    }

    /**
     * Actualiza el estado de todas las entidades gestionadas.
     * @param delta El tiempo transcurrido desde el último frame.
     * @param personaje El componente del personaje principal para detección de colisiones.
     * @param worldWidth El ancho del mundo del juego para la lógica de despawn.
     */
    public void update(float delta, ComponentePersonaje personaje, float worldWidth) { // Added worldWidth parameter
        // Actualizar ítems
        Iterator<ItemRegeneracion> itI = items.iterator();
        while (itI.hasNext()) {
            ItemRegeneracion item = itI.next();
            item.actualizar(delta, 0); // groundY is no longer passed, items will need to handle their own collision or fall
            // Asumiendo un tamaño de caja de colisión de 64x64 para el personaje
            if (item.colisiona(personaje.getX(), personaje.getY(), 64, 64)) {
                if (callback != null) {
                    // Notificar que el personaje ha sido decorado (ej. con regeneración)
                    callback.onPlayerComponentChanged(
                        new VidaRegeneracionDecorator(personaje, (cant) -> {
                            callback.onPlayerHealed(cant); // Notificar la curación
                            callback.onNewFloatingText(
                                new TextoFlotante(TEXT_HEALTH_GAIN_PREFIX + cant + TEXT_HEALTH_GAIN_SUFFIX, personaje.getX(), personaje.getY() + 80, Color.GREEN)
                            );
                        })
                    );
                    callback.onNewFloatingText(
                        new TextoFlotante(TEXT_ITEM_COLLECTED, item.getX(), item.getY() + 30, Color.WHITE)
                    );
                }
                item.dispose();
                itI.remove();
            }
        }

        // Actualizar proyectiles
        Iterator<Proyectil> itP = proyectiles.iterator();
        while (itP.hasNext()) {
            Proyectil p = itP.next();
            p.actualizar(delta);
            if (!p.isActivo()) {
                p.dispose();
                itP.remove();
                continue;
            }
            for (Iterator<Enemigo> itE = enemigos.iterator(); itE.hasNext(); ) {
                Enemigo e = itE.next();
                if (Math.abs(p.getX() - e.getX()) < PROJECTILE_ENEMY_COLLISION_THRESHOLD) {
                    if (callback != null) {
                        callback.onNewFloatingText(
                            new TextoFlotante(TEXT_SCORE_GAIN, e.getX(), e.getY() + 60, Color.YELLOW)
                        );
                        callback.onScoreIncreased(ENEMY_SCORE_VALUE);
                    }
                    e.dispose();
                    itE.remove();
                    p.dispose();
                    itP.remove();
                    break;
                }
            }
        }

        // Actualizar enemigos
        Iterator<Enemigo> itE = enemigos.iterator();
        while (itE.hasNext()) {
            Enemigo e = itE.next();
            e.actualizar(delta);
            // e.setPosicion(e.getX(), groundY); // Removed this line
            if (Math.abs(e.getX() - personaje.getX()) < ENEMY_PLAYER_COLLISION_THRESHOLD_X && Math.abs(personaje.getY() - e.getY()) < ENEMY_PLAYER_COLLISION_THRESHOLD_Y) { // Changed groundY to e.getY()
                if (callback != null) {
                    callback.onPlayerDamaged(PLAYER_DAMAGE_VALUE);
                    callback.onNewFloatingText(
                        new TextoFlotante(TEXT_HEALTH_LOSS, personaje.getX(), personaje.getY() + 80, Color.RED)
                    );
                }
                e.dispose();
                itE.remove();
            }
            // else if (e.getX() < -ENEMY_DESPAWN_OFFSET || e.getX() > worldWidth + ENEMY_DESPAWN_OFFSET) { // Comentado temporalmente para depuración
            //     e.dispose();
            //     itE.remove();
            // }
        }

        // Actualizar textos flotantes
        Iterator<TextoFlotante> itT = textos.iterator();
        while (itT.hasNext()) {
            TextoFlotante t = itT.next();
            t.actualizar(delta);
            if (t.isMuerto()) {
                itT.remove();
            }
        }
    }

    /**
     * Dibuja todas las entidades gestionadas.
     * @param batch El SpriteBatch para dibujar.
     * @param font La fuente para dibujar textos flotantes.
     */
    public void render(SpriteBatch batch, BitmapFont font) {
        for (ItemRegeneracion item : items) item.dibujar(batch);
        for (Proyectil p : proyectiles) p.dibujar(batch);
        for (Enemigo enemigo : enemigos) enemigo.dibujar(batch);
        for (TextoFlotante t : textos) t.dibujar(batch, font);
    }

    /**
     * Libera los recursos de todas las entidades gestionadas.
     */
    @Override
    public void dispose() {
        for (Enemigo e : enemigos) {
            e.dispose();
        }
        enemigos.clear();

        for (Proyectil p : proyectiles) {
            p.dispose();
        }
        proyectiles.clear();

        for (ItemRegeneracion item : items) {
            item.dispose();
        }
        items.clear();
        // TextoFlotante no implementa Disposable, no necesita dispose.
    }
}
