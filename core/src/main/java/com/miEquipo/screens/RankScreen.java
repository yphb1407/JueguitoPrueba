package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.miEquipo.Datos_y_almacen.ScoreEntry;
import com.miEquipo.mygame.MyGdxGame;

import java.util.LinkedList;

public class RankScreen implements Screen {
    private final MyGdxGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private LinkedList<ScoreEntry> topScores;

    public RankScreen(MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
        this.font = game.font;
        this.topScores = game.scoreManager.getTopScores();
    }

    @Override
    public void show() {
        // Se llama cuando esta pantalla se convierte en la pantalla actual de Game
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f); // Fondo oscuro para el ranking
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.getData().setScale(2);
        font.setColor(Color.GOLD);
        font.draw(batch, "TOP 5 RANKING", Gdx.graphics.getWidth() / 2f - 120, Gdx.graphics.getHeight() - 50);

        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);
        float yPos = Gdx.graphics.getHeight() - 150;
        int rank = 1;
        for (ScoreEntry entry : topScores) {
            font.draw(batch, rank + ". " + entry.name + " - " + entry.score, Gdx.graphics.getWidth() / 2f - 100, yPos);
            yPos -= 40;
            rank++;
        }

        // Rellenar con "---" si hay menos de 5 entradas
        while (rank <= 5) {
            font.draw(batch, rank + ". ---", Gdx.graphics.getWidth() / 2f - 100, yPos);
            yPos -= 40;
            rank++;
        }

        font.getData().setScale(1f);
        font.setColor(Color.CYAN);
        float backButtonX = Gdx.graphics.getWidth() / 2f - 80;
        float backButtonY = 50;
        font.draw(batch, "Volver al Menú", backButtonX, backButtonY);

        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Detectar toque en "Volver al Menú"
            if (touchX > backButtonX && touchX < backButtonX + 160 && touchY > backButtonY - 20 && touchY < backButtonY + 10) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
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
        // No se dispone de batch ni font aquí, ya que son gestionados por MyGdxGame
    }
}
