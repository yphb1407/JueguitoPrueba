package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.miEquipo.mygame.MyGdxGame;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Pantalla del menú principal del juego.
 * Permite al usuario navegar entre las diferentes opciones como iniciar el juego o ver el ranking.
 */
public class MenuScreen implements Screen {
    private final MyGdxGame game;
    private Texture background;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    /**
     * Constructor de MenuScreen.
     * @param game La instancia principal de MyGdxGame.
     */
    public MenuScreen(MyGdxGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();
        this.layout = new GlyphLayout();
        try {
            background = new Texture("fondo.png");
        } catch (GdxRuntimeException e) {
            Gdx.app.error("MenuScreen", "Error al cargar fondo.png. Asegúrate de que esté en la carpeta assets.", e);
            background = null;
        }
    }

    /**
     * Se llama cuando esta pantalla se convierte en la pantalla actual de Game.
     */
    @Override
    public void show() {
        // No se requiere implementación específica al mostrar la pantalla.
    }

    /**
     * Lógica de renderizado y actualización de la pantalla del menú.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        if (background != null) {
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            batch.setColor(Color.BLACK);
            batch.draw(game.getFont().getRegion().getTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        }

        font.getData().setScale(2);
        font.setColor(Color.WHITE);
        String titleText = "JUEGUITO PRUEBA";
        layout.setText(font, titleText);
        float titleWidth = layout.width;
        font.draw(batch, titleText, Gdx.graphics.getWidth() / 2f - titleWidth / 2, Gdx.graphics.getHeight() - 100);

        font.getData().setScale(1.5f);
        String jugarText = "JUGAR";
        layout.setText(font, jugarText);
        float jugarWidth = layout.width;
        float jugarHeight = layout.height;
        float jugarX = Gdx.graphics.getWidth() / 2f - jugarWidth / 2;
        float jugarY = Gdx.graphics.getHeight() / 2f + 50;
        font.draw(batch, jugarText, jugarX, jugarY);

        String rankText = "RANK";
        layout.setText(font, rankText);
        float rankWidth = layout.width;
        float rankHeight = layout.height;
        float rankX = Gdx.graphics.getWidth() / 2f - rankWidth / 2;
        float rankY = Gdx.graphics.getHeight() / 2f - 50;
        font.draw(batch, rankText, rankX, rankY);

        String tiledMapText = "PROBAR MAPA TILED";
        layout.setText(font, tiledMapText);
        float tiledMapWidth = layout.width;
        float tiledMapHeight = layout.height;
        float tiledMapX = Gdx.graphics.getWidth() / 2f - tiledMapWidth / 2;
        float tiledMapY = Gdx.graphics.getHeight() / 2f - 150; // Posicionado debajo de RANK
        font.draw(batch, tiledMapText, tiledMapX, tiledMapY);


        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (touchX > jugarX && touchX < jugarX + jugarWidth && touchY > jugarY - jugarHeight && touchY < jugarY) {
                game.setScreen(new GameScreen(game));
                dispose();
            } else if (touchX > rankX && touchX < rankX + rankWidth && touchY > rankY - rankHeight && touchY < rankY) {
                game.setScreen(new RankScreen(game));
                dispose();
            } else if (touchX > tiledMapX && touchX < tiledMapX + tiledMapWidth && touchY > tiledMapY - tiledMapHeight && touchY < tiledMapY) {
                game.setScreen(new TiledMapScreen(game)); // Nueva pantalla para el mapa Tiled
                dispose();
            }
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
        if (background != null) {
            background.dispose();
        }
    }
}
