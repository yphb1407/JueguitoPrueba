package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ItemRegeneracion {
    private float x, y;
    private Texture textura;
    private float velocidadCaida = 150f;
    private boolean activo = true;
    private final float ANCHO = 48;
    private final float ALTO = 48;

    public ItemRegeneracion(float x, float y) {
        this.x = x;
        this.y = y;
        this.textura = new Texture("regeneracion.png");
    }

    public void actualizar(float delta, float sueloY) {
        if (y > sueloY) {
            y -= velocidadCaida * delta;
        } else {
            y = sueloY;
        }
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, ANCHO, ALTO);
    }

    public boolean colisiona(float px, float py, float pAncho, float pAlto) {
        return x < px + pAncho && x + ANCHO > px && y < py + pAlto && y + ALTO > py;
    }

    public boolean isActivo() { return activo; }
    public void desactivar() { activo = false; }
    public float getX() { return x; }
    public float getY() { return y; }
}
