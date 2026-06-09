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
    public SpriteBatch batch;
    public BitmapFont font;
    public ScoreManager scoreManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Inicializa la fuente por defecto
        scoreManager = new ScoreManager();
        this.setScreen(new MenuScreen(this)); // Establece la pantalla inicial como el menú
    }

    @Override
    public void dispose() {
        super.dispose(); // Llama al dispose de la pantalla actual
        batch.dispose();
        font.dispose();
    }
}
