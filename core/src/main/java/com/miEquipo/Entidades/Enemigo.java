package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Enemigo implements Cloneable {
    protected float x, y;
    protected float velocidadX;
    protected Texture textura;
    protected String tipo;
    protected float ancho = 64;
    protected float alto = 64;

    public Enemigo(String tipo, String rutaTextura) {
        this.tipo = tipo;
        this.textura = new Texture(rutaTextura);
        this.velocidadX = -60f;
    }

    public abstract void actualizar(float delta);

    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, ancho, alto);
    }

    @Override
    public Enemigo clone() {
        try {
            return (Enemigo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public float getX() { return x; }
    public float getY() { return y; }
}
