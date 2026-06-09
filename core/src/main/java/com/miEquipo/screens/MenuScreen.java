package com.miEquipo.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // Importar GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.miEquipo.mygame.MyGdxGame;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class MenuScreen implements Screen {
    private final MyGdxGame game;
    private Texture background;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout; // Declarar GlyphLayout

    public MenuScreen(MyGdxGame game) {
        this.game = game;
        this.batch = game.batch;
        this.font = game.font;
        this.layout = new GlyphLayout(); // Inicializar GlyphLayout
        try {
            background = new Texture("fondo.png");
        } catch (GdxRuntimeException e) {
            Gdx.app.error("MenuScreen", "Error al cargar fondo.png. Asegúrate de que esté en la carpeta assets.", e);
            background = null;
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        if (background != null) {
            batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else {
            batch.setColor(Color.BLACK);
            batch.draw(game.font.getRegion().getTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setColor(Color.WHITE);
        }

        font.getData().setScale(2);
        font.setColor(Color.WHITE);
        String titleText = "JUEGUITO PRUEBA";
        layout.setText(font, titleText); // Usar GlyphLayout para obtener el ancho
        float titleWidth = layout.width;
        font.draw(batch, titleText, Gdx.graphics.getWidth() / 2f - titleWidth / 2, Gdx.graphics.getHeight() - 100);

        font.getData().setScale(1.5f);
        // Botón Jugar
        String jugarText = "JUGAR";
        layout.setText(font, jugarText); // Usar GlyphLayout para obtener el ancho
        float jugarWidth = layout.width;
        float jugarHeight = layout.height; // También obtenemos la altura para la detección de toque
        float jugarX = Gdx.graphics.getWidth() / 2f - jugarWidth / 2;
        float jugarY = Gdx.graphics.getHeight() / 2f + 50;
        font.draw(batch, jugarText, jugarX, jugarY);

        // Botón Rank
        String rankText = "RANK";
        layout.setText(font, rankText); // Usar GlyphLayout para obtener el ancho
        float rankWidth = layout.width;
        float rankHeight = layout.height; // También obtenemos la altura
        float rankX = Gdx.graphics.getWidth() / 2f - rankWidth / 2;
        float rankY = Gdx.graphics.getHeight() / 2f - 50;
        font.draw(batch, rankText, rankX, rankY);

        batch.end();

        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            // Detectar toque en "JUGAR"
            if (touchX > jugarX && touchX < jugarX + jugarWidth && touchY > jugarY - jugarHeight && touchY < jugarY) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
            // Detectar toque en "RANK"
            else if (touchX > rankX && touchX < rankX + rankWidth && touchY > rankY - rankHeight && touchY < rankY) {
                game.setScreen(new RankScreen(game));
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
        if (background != null) {
            background.dispose();
        }
        // No es necesario disponer de layout aquí, ya que no gestiona recursos pesados como Textures.
    }
}
