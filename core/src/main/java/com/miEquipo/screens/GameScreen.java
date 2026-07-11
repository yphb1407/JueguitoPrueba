package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.facade.PartidaFacade;
import com.miEquipo.mygame.MyGdxGame;

/**
 * Pantalla principal del juego donde se desarrolla la acción.
 * Gestiona la lógica de renderizado, actualización y la interacción con el usuario durante la partida.
 */
public class GameScreen implements Screen {
    private final MyGdxGame game;
    private PartidaFacade motorJuego;
    private SpriteBatch batch;
    private BitmapFont font;

    /**
     * Constructor de GameScreen.
     * @param game La instancia principal de MyGdxGame.
     */
    public GameScreen(MyGdxGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();
        motorJuego = new PartidaFacade(game.getScoreManager());
    }

    /**
     * Se llama cuando esta pantalla se convierte en la pantalla actual de Game.
     */
    @Override
    public void show() {
        // No se requiere implementación específica al mostrar la pantalla.
    }

    /**
     * Lógica de renderizado y actualización de la pantalla de juego.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        motorJuego.renderizarMundo(batch, font);
        batch.end();

        motorJuego.actualizarYEscucharEntradas(delta);

        if (motorJuego.isGameOver()) {
            Gdx.input.getTextInput(new Input.TextInputListener() {
                @Override
                public void input(String text) {
                    String playerName = text.isEmpty() ? "Anonimo" : text;
                    game.getScoreManager().addScore(playerName, motorJuego.getScoreActual());
                    game.setScreen(new RankScreen(game));
                    dispose();
                }

                @Override
                public void canceled() {
                    game.getScoreManager().addScore("Anonimo", motorJuego.getScoreActual());
                    game.setScreen(new RankScreen(game));
                    dispose();
                }
            }, "GAME OVER! Ingresa tu nombre:", "", "Tu nombre");
        }
    }

    /**
     * Se llama cuando la ventana del juego cambia de tamaño.
     * @param width Nuevo ancho de la ventana.
     * @param height Nueva altura de la ventana.
     */
    @Override
    public void resize(int width, int height) {
    }

    /**
     * Se llama cuando el juego es pausado.
     */
    @Override
    public void pause() {
    }

    /**
     * Se llama cuando el juego es reanudado.
     */
    @Override
    public void resume() {
    }

    /**
     * Se llama cuando esta pantalla deja de ser la pantalla actual de Game.
     */
    @Override
    public void hide() {
    }

    /**
     * Libera los recursos utilizados por esta pantalla.
     */
    @Override
    public void dispose() {
        motorJuego.dispose();
    }
}
