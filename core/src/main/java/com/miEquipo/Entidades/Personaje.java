package com.miEquipo.Entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.miEquipo.patron_states.PersonajeState;
import com.miEquipo.patron_states.EstadoQuieto;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Personaje implements ComponentePersonaje {
    private float x, y;
    private float velocidadX, velocidadY;
    private PersonajeState estadoActual;

    // Animaciones
    private Animation<TextureRegion> animReposo;
    private Animation<TextureRegion> animAtaque;
    private float tiempoAnimacion = 0;

    private boolean atacando = false;
    private boolean mirandoDerecha = true;
    private float ancho = 64;
    private float alto = 74;

    // Listener para cuando termine el ataque
    public interface OnAtaqueFinalizado {
        void alFinalizar();
    }
    private OnAtaqueFinalizado listenerAtaque;

    public Personaje(float x, float y) {
        this.x = x;
        this.y = y;
        this.estadoActual = new EstadoQuieto();

        cargarAnimaciones();
    }

    private void cargarAnimaciones() {
        // Cargar Reposo
        animReposo = crearAnimacionDesdeCarpeta("personaje_reposo", 0.05f, Animation.PlayMode.LOOP);
        // Cargar Ataque
        animAtaque = crearAnimacionDesdeCarpeta("personaje_ataque", 0.05f, Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> crearAnimacionDesdeCarpeta(String carpeta, float frameDuration, Animation.PlayMode mode) {
        Array<TextureRegion> frames = new Array<>();
        int i = 1;
        while (true) {
            String suffix = (i < 10 && carpeta.equals("personaje_ataque")) ? "0" + i : "" + i;
            String ruta = carpeta + "/" + suffix + ".png";
            if (!Gdx.files.internal(ruta).exists()) {
                // Intentar sin el cero inicial si falló (para reposo que usa 1, 2...)
                ruta = carpeta + "/" + i + ".png";
                if (!Gdx.files.internal(ruta).exists()) break;
            }
            frames.add(new TextureRegion(new Texture(ruta)));
            i++;
        }
        Animation<TextureRegion> anim = new Animation<>(frameDuration, frames);
        anim.setPlayMode(mode);
        return anim;
    }

    @Override
    public void actualizar(float delta) {
        tiempoAnimacion += delta;

        if (!atacando) {
            estadoActual.manejarEntrada(this);
            estadoActual.actualizar(this, delta);
        }

        if (velocidadX > 0) mirandoDerecha = true;
        else if (velocidadX < 0) mirandoDerecha = false;

        this.x += velocidadX * delta;
        this.y += velocidadY * delta;

        // Verificar si la animación de ataque terminó
        if (atacando && animAtaque.isAnimationFinished(tiempoAnimacion)) {
            atacando = false;
            tiempoAnimacion = 0;
            if (listenerAtaque != null) {
                listenerAtaque.alFinalizar();
            }
        }
    }

    public void iniciarAtaque(OnAtaqueFinalizado listener) {
        if (!atacando) {
            this.atacando = true;
            this.tiempoAnimacion = 0;
            this.listenerAtaque = listener;
            this.velocidadX = 0; // Se detiene al atacar
        }
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        TextureRegion frameActual;
        if (atacando) {
            frameActual = animAtaque.getKeyFrame(tiempoAnimacion);
        } else {
            frameActual = animReposo.getKeyFrame(tiempoAnimacion);
        }

        if (frameActual != null) {
            // Dibujar con flip si mira a la izquierda
            if (!mirandoDerecha && !frameActual.isFlipX()) frameActual.flip(true, false);
            if (mirandoDerecha && frameActual.isFlipX()) frameActual.flip(true, false);

            batch.draw(frameActual, x, y, ancho, alto);
        }
    }

    @Override public boolean isMirandoDerecha() { return mirandoDerecha; }
    @Override public void setEstado(PersonajeState nuevoEstado) { if (!atacando) this.estadoActual = nuevoEstado; }
    @Override public float getX() { return x; }
    @Override public float getY() { return y; }
    @Override public void setY(float y) { this.y = y; }
    @Override public float getVelocidadY() { return velocidadY; }
    @Override public void setVelocidadY(float vy) { this.velocidadY = vy; }
    @Override public void setVelocidadX(float vx) { this.velocidadX = vx; }
}
