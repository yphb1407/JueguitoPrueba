package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.facade.PartidaFacade;
import com.miEquipo.mygame.MyGdxGame;

public class GameScreen implements Screen {
    private final MyGdxGame game;
    private PartidaFacade motorJuego;
    private SpriteBatch batch;
    private BitmapFont font;

    public GameScreen(MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
        this.font = game.font;
        motorJuego = new PartidaFacade(game.scoreManager); // Pasamos el ScoreManager
    }

    @Override
    public void show() {
        // Se llama cuando esta pantalla se convierte en la pantalla actual de Game
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        motorJuego.renderizarMundo(batch, font); // Pasamos el font para dibujar UI
        batch.end();

        motorJuego.actualizarYEscucharEntradas(delta);

        // Lógica de Game Over
        if (motorJuego.isGameOver()) {
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    String playerName = text.isEmpty() ? "Anonimo" : text;
                    game.scoreManager.addScore(playerName, motorJuego.getScoreActual());
                    game.setScreen(new RankScreen(game)); // Ir a la pantalla de ranking
                    dispose();
                }

                @Override
                public void canceled() {
                    game.scoreManager.addScore("Anonimo", motorJuego.getScoreActual());
                    game.setScreen(new RankScreen(game)); // Ir a la pantalla de ranking
                    dispose();
                }
            }, "GAME OVER! Ingresa tu nombre:", "", "Tu nombre");
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
        motorJuego.dispose(); // Asegurarse de liberar recursos de PartidaFacade
    }
}
