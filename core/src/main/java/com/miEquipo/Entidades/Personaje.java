package com.miEquipo.Entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.miEquipo.patron_states.PersonajeState;
import com.miEquipo.patron_states.EstadoQuieto;

public class Personaje implements ComponentePersonaje {
    // --- Constants ---
    private static final float ANIMATION_FRAME_DURATION = 0.05f;
    private static final float JUMP_VELOCITY = 650;
    private static final float CHARACTER_WIDTH = 64;
    private static final float CHARACTER_HEIGHT = 64;
    private static final String ANIM_FOLDER_REPOSO = "personaje_reposo";
    private static final String ANIM_FOLDER_ATAQUE = "personaje_ataque";
    private static final String LOG_TAG_ANIM_LOADER = "AnimationLoader";
    private static final String ERROR_MSG_ANIM_LOAD_FAILED = "Error al cargar textura para %s: %s";
    private static final String ERROR_MSG_NO_FRAMES = "CRÍTICO: No se cargaron fotogramas de animación para la carpeta: %s. ¡Verifica tus assets! Esto causará un error.";

    private float x, y;
    private float velocidadX, velocidadY;
    private PersonajeState estadoActual;

    private Animation<TextureRegion> animReposo;
    private Animation<TextureRegion> animAtaque;
    private float tiempoAnimacion = 0;

    private boolean atacando = false;
    private boolean mirandoDerecha = true;

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
        animReposo = crearAnimacionDesdeCarpeta(ANIM_FOLDER_REPOSO, ANIMATION_FRAME_DURATION, Animation.PlayMode.LOOP);
        animAtaque = crearAnimacionDesdeCarpeta(ANIM_FOLDER_ATAQUE, ANIMATION_FRAME_DURATION, Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> crearAnimacionDesdeCarpeta(String carpeta, float frameDuration, Animation.PlayMode mode) {
        Array<TextureRegion> frames = new Array<>();
        int i = 1;
        String expectedFormat;

        if (carpeta.equals(ANIM_FOLDER_ATAQUE)) {
            expectedFormat = "%02d.png";
        } else {
            expectedFormat = "%d.png";
        }

        while (true) {
            String fileName = String.format(expectedFormat, i);
            String ruta = carpeta + "/" + fileName;

            if (Gdx.files.internal(ruta).exists()) {
                try {
                    frames.add(new TextureRegion(new Texture(ruta)));
                    Gdx.app.log(LOG_TAG_ANIM_LOADER, "Cargado frame: " + ruta);
                } catch (GdxRuntimeException e) {
                    Gdx.app.error(LOG_TAG_ANIM_LOADER, String.format(ERROR_MSG_ANIM_LOAD_FAILED, ruta, e.getMessage()));
                    break;
                }
            } else {
                String alternateFormat = (expectedFormat.equals("%02d.png")) ? "%d.png" : "%02d.png";
                String alternateFileName = String.format(alternateFormat, i);
                String alternateRuta = carpeta + "/" + alternateFileName;

                if (Gdx.files.internal(alternateRuta).exists()) {
                    try {
                        frames.add(new TextureRegion(new Texture(alternateRuta)));
                        Gdx.app.log(LOG_TAG_ANIM_LOADER, "Cargado frame (formato alternativo): " + alternateRuta);
                    } catch (GdxRuntimeException e) {
                        Gdx.app.error(LOG_TAG_ANIM_LOADER, String.format(ERROR_MSG_ANIM_LOAD_FAILED, alternateRuta, e.getMessage()));
                        break;
                    }
                } else {
                    Gdx.app.log(LOG_TAG_ANIM_LOADER, "Archivo no encontrado: " + ruta + " ni " + alternateRuta + ". Deteniendo carga para carpeta " + carpeta);
                    break;
                }
            }
            i++;
        }

        if (frames.size == 0) {
            Gdx.app.error(LOG_TAG_ANIM_LOADER, String.format(ERROR_MSG_NO_FRAMES, carpeta));
            throw new GdxRuntimeException(String.format(ERROR_MSG_NO_FRAMES, carpeta));
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

        if (atacando && animAtaque.isAnimationFinished(tiempoAnimacion)) {
            atacando = false;
            tiempoAnimacion = 0;
            if (listenerAtaque != null) {
                listenerAtaque.alFinalizar();
            }
        }
    }

    /**
     * Inicia la animación de ataque del personaje.
     * @param listener Un callback que se ejecuta cuando la animación de ataque finaliza.
     */
    public void iniciarAtaque(OnAtaqueFinalizado listener) {
        if (!atacando) {
            this.atacando = true;
            this.tiempoAnimacion = 0;
            this.listenerAtaque = listener;
            this.velocidadX = 0; // Detener movimiento horizontal durante el ataque
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
            if (!mirandoDerecha && !frameActual.isFlipX()) frameActual.flip(true, false);
            if (mirandoDerecha && frameActual.isFlipX()) frameActual.flip(true, false);

            batch.draw(frameActual, x, y, CHARACTER_WIDTH, CHARACTER_HEIGHT);
        }
    }

    @Override public boolean isMirandoDerecha() { return mirandoDerecha; }
    @Override public void setEstado(PersonajeState nuevoEstado) { if (!atacando) this.estadoActual = nuevoEstado; }
    @Override public float getX() { return x; }
    @Override public float getY() { return y; }
    @Override public void setY(float y) { this.y = y; }
    @Override public float getVelocidadY() { return velocidadY; }
    @Override public void setVelocidadY(float vy) { this.velocidadY = vy; }
    @Override public float getVelocidadX() { return velocidadX; } // Added getter
    @Override public void setVelocidadX(float vx) { this.velocidadX = vx; }
}
