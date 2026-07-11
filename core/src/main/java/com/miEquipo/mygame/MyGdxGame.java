package com.miEquipo.mygame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.miEquipo.Datos_y_almacen.ScoreManager;
import com.miEquipo.screens.MenuScreen;

/**
 * Clase principal del juego. Extiende Game para gestionar las diferentes pantallas (menu, juego, ranking).
 * Es responsable de inicializar y disponer de recursos globales como SpriteBatch y BitmapFont.
 */
public class MyGdxGame extends Game {
    private SpriteBatch batch;
    private BitmapFont font;
    private ScoreManager scoreManager;

    /**
     * Se llama cuando la aplicación es creada. Inicializa recursos globales y establece la pantalla inicial.
     */
    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        scoreManager = new ScoreManager();
        this.setScreen(new MenuScreen(this));
    }

    /**
     * Obtiene el SpriteBatch global del juego.
     * @return El SpriteBatch.
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Obtiene la BitmapFont global del juego.
     * @return La BitmapFont.
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     * Obtiene el ScoreManager global del juego.
     * @return El ScoreManager.
     */
    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Se llama cuando la aplicación se cierra. Dispone de los recursos globales.
     */
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
    }
}
