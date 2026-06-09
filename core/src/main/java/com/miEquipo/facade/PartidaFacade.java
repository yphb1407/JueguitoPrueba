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
import com.miEquipo.patron_states.EstadoQuieto;
import com.miEquipo.patron_states.EstadoSaltando;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PartidaFacade {
    private ComponentePersonaje personaje;
    private List<Enemigo> listaEnemigos;
    private List<Proyectil> proyectiles;
    private List<TextoFlotante> textos;
    private List<ItemRegeneracion> items;
    private ScoreManager scoreManager;

    private int scoreActual = 0;
    private int vida = 20;
    private final float SUELO_Y = 64;

    private Texture texturaTerreno;
    private float timerEnemigos = 0;
    private float tiempoSpawnEnemigo = 2f;

    private float timerItem = 0;
    private float proximoItemEn = MathUtils.random(10, 30);

    public PartidaFacade(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.personaje = new Personaje(140, SUELO_Y);
        this.listaEnemigos = new ArrayList<>();
        this.proyectiles = new ArrayList<>();
        this.textos = new ArrayList<>();
        this.items = new ArrayList<>();
        this.texturaTerreno = new Texture("terreno.png");
    }

    public void actualizarYEscucharEntradas(float delta) {
        if (isGameOver()) {
            return;
        }

        // Spawn de Enemigos
        timerEnemigos += delta;
        if (timerEnemigos >= tiempoSpawnEnemigo) {
            boolean desdeDerecha = MathUtils.randomBoolean();
            float spawnX = desdeDerecha ? Gdx.graphics.getWidth() + 100 : -100;
            float velX = desdeDerecha ? -MathUtils.random(100, 200) : MathUtils.random(100, 200);
            listaEnemigos.add(EnemigoFactory.crearEnemigo("goomba", spawnX, SUELO_Y, velX));
            timerEnemigos = 0;
            tiempoSpawnEnemigo = Math.max(0.7f, tiempoSpawnEnemigo - 0.02f);
        }

        // Spawn de Items
        timerItem += delta;
        if (timerItem >= proximoItemEn) {
            float spawnX = MathUtils.random(50, Gdx.graphics.getWidth() - 50);
            items.add(new ItemRegeneracion(spawnX, Gdx.graphics.getHeight() + 50));
            timerItem = 0;
            proximoItemEn = MathUtils.random(20, 40);
        }

        // Entradas
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && personaje.getY() <= SUELO_Y + 10) {
            personaje.setEstado(new EstadoSaltando());
            personaje.setVelocidadY(650);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Personaje realPersonaje = obtenerPersonajeReal(personaje);
            if (realPersonaje != null) {
                realPersonaje.iniciarAtaque(() -> {
                    proyectiles.add(new Proyectil(personaje.getX(), personaje.getY() + 20, personaje.isMirandoDerecha()));
                });
            }
        }

        personaje.actualizar(delta);

        // Des-decorar si la regeneración terminó
        if (personaje instanceof VidaRegeneracionDecorator) {
            if (((VidaRegeneracionDecorator) personaje).isTerminado()) {
                personaje = ((VidaRegeneracionDecorator) personaje).getPersonajeOriginal();
                textos.add(new TextoFlotante("Curación terminada", personaje.getX(), personaje.getY() + 100, Color.WHITE));
            }
        }

        // Lógica de Items
        Iterator<ItemRegeneracion> itI = items.iterator();
        while (itI.hasNext()) {
            ItemRegeneracion item = itI.next();
            item.actualizar(delta, SUELO_Y);
            if (item.colisiona(personaje.getX(), personaje.getY(), 64, 64)) {
                personaje = new VidaRegeneracionDecorator(personaje, (cant) -> {
                    vida = Math.min(500, vida + cant);
                    textos.add(new TextoFlotante("+" + cant + " HP", personaje.getX(), personaje.getY() + 80, Color.GREEN));
                });
                itI.remove();
            }
        }

        // Proyectiles
        Iterator<Proyectil> itP = proyectiles.iterator();
        while (itP.hasNext()) {
            Proyectil p = itP.next();
            p.actualizar(delta);
            if (!p.isActivo()) { itP.remove(); continue; }
            for (Iterator<Enemigo> itE = listaEnemigos.iterator(); itE.hasNext(); ) {
                Enemigo e = itE.next();
                if (Math.abs(p.getX() - e.getX()) < 40) {
                    textos.add(new TextoFlotante("+100", e.getX(), e.getY() + 60, Color.YELLOW));
                    itE.remove(); itP.remove();
                    scoreActual += 100; break;
                }
            }
        }

        // Enemigos
        Iterator<Enemigo> itE = listaEnemigos.iterator();
        while (itE.hasNext()) {
            Enemigo e = itE.next();
            e.actualizar(delta);
            e.setPosicion(e.getX(), SUELO_Y);
            if (Math.abs(e.getX() - personaje.getX()) < 35 && Math.abs(personaje.getY() - SUELO_Y) < 40) {
                vida -= 10;
                textos.add(new TextoFlotante("-10 HP", personaje.getX(), personaje.getY() + 80, Color.RED));
                itE.remove();
            } else if (e.getX() < -300 || e.getX() > Gdx.graphics.getWidth() + 300) itE.remove();
        }

        for (Iterator<TextoFlotante> itT = textos.iterator(); itT.hasNext(); ) {
            TextoFlotante t = itT.next();
            t.actualizar(delta);
            if (t.isMuerto()) itT.remove();
        }
    }

    private Personaje obtenerPersonajeReal(ComponentePersonaje comp) {
        if (comp instanceof Personaje) return (Personaje) comp;
        if (comp instanceof VidaRegeneracionDecorator) {
            return obtenerPersonajeReal(((VidaRegeneracionDecorator) comp).getPersonajeOriginal());
        }
        return null;
    }

    public void renderizarMundo(SpriteBatch batch, BitmapFont font) {
        // DIBUJAR TERRENO
        for (int i = 0; i < Gdx.graphics.getWidth(); i += 64) {
            batch.draw(texturaTerreno, i, 0, 64, 64);
        }

        // DIBUJAR ENTIDADES
        for (ItemRegeneracion item : items) item.dibujar(batch);
        personaje.dibujar(batch);
        for (Proyectil p : proyectiles) p.dibujar(batch);
        for (Enemigo enemigo : listaEnemigos) enemigo.dibujar(batch);
        for (TextoFlotante t : textos) t.dibujar(batch, font);

        // UI
        font.setColor(Color.WHITE);
        font.draw(batch, "VIDA: " + vida, 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "SCORE: " + scoreActual, 20, Gdx.graphics.getHeight() - 40);
        font.setColor(Color.GOLD);
        font.draw(batch, "MAX: " + scoreManager.getHighestScore(), 20, Gdx.graphics.getHeight() - 60);

        if (isGameOver()) {
            font.setColor(Color.RED);
            font.draw(batch, "GAME OVER", Gdx.graphics.getWidth()/2f - 50, Gdx.graphics.getHeight()/2f + 50);
            font.draw(batch, "Ingresa tu nombre para el ranking", Gdx.graphics.getWidth()/2f - 150, Gdx.graphics.getHeight()/2f);
        }
    }

    public boolean isGameOver() {
        return vida <= 0;
    }

    public int getScoreActual() {
        return scoreActual;
    }

    public void dispose() {
        texturaTerreno.dispose();
    }
}
