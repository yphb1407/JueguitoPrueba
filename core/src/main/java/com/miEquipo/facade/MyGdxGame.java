package com.miEquipo.facade;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class MyGdxGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private PartidaFacade motorJuego; // <--- Nuestra fachada central

    @Override
    public void create() {
        batch = new SpriteBatch();
        // Inicializamos la fachada que se encarga de crear todo por dentro
        motorJuego = new PartidaFacade();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1. Delegar el control de la lógica a la fachada
        motorJuego.actualizarYEscucharEntradas(delta);

        // 2. Limpiar pantalla y renderizar
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        batch.begin();
        // Delegar el dibujo de las entidades a la fachada
        motorJuego.renderizarMundo(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
