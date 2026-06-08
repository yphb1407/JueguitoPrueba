package com.miEquipo.Entidades;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;

public class TextoFlotante {
    private float x, y;
    private String texto;
    private float tiempoVida;
    private float duracionMax = 1.0f;
    private Color color;

    public TextoFlotante(String texto, float x, float y, Color color) {
        this.texto = texto;
        this.x = x;
        this.y = y;
        this.color = new Color(color);
        this.tiempoVida = 0;
    }

    public void actualizar(float delta) {
        tiempoVida += delta;
        y += 50 * delta; // Sube poco a poco
        color.a = 1 - (tiempoVida / duracionMax); // Se desvanece
    }

    public void dibujar(SpriteBatch batch, BitmapFont font) {
        font.setColor(color);
        font.draw(batch, texto, x, y);
    }

    public boolean isMuerto() {
        return tiempoVida >= duracionMax;
    }
}
