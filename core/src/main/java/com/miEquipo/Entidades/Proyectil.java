package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Proyectil {
    private float x, y;
    private float velocidadX;
    private Texture textura;
    private boolean activo;
    private final float ANCHO = 32;
    private final float ALTO = 32;

    public Proyectil(float x, float y, boolean haciaDerecha) {
        this.x = x;
        this.y = y;
        this.textura = new Texture("proyectil.png");
        this.velocidadX = haciaDerecha ? 400f : -400f;
        this.activo = true;
    }

    public void actualizar(float delta) {
        x += velocidadX * delta;
        // Desactivar si sale de la pantalla (suponiendo 800 de ancho)
        if (x < -50 || x > 850) {
            activo = false;
        }
    }

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, ANCHO, ALTO);
    }

    public boolean isActivo() {
        return activo;
    }

    public float getX() { return x; }
    public float getY() { return y; }
}
