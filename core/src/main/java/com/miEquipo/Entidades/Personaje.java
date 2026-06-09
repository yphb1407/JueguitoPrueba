package com.miEquipo.Entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException; // Importar GdxRuntimeException
import com.miEquipo.patron_states.PersonajeState;
import com.miEquipo.patron_states.EstadoQuieto;

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
    private float alto = 64;

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
        animReposo = crearAnimacionDesdeCarpeta("personaje_reposo", 0.05f, Animation.PlayMode.LOOP);
        animAtaque = crearAnimacionDesdeCarpeta("personaje_ataque", 0.05f, Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> crearAnimacionDesdeCarpeta(String carpeta, float frameDuration, Animation.PlayMode mode) {
        Array<TextureRegion> frames = new Array<>();
        int i = 1;
        String expectedFormat;

        // Determinar el formato esperado del nombre de archivo basado en la carpeta
        // Asumimos que ataque usa 01, 02... y reposo usa 1, 2...
        if (carpeta.equals("personaje_ataque")) {
            expectedFormat = "%02d.png"; // Ej: 01.png, 02.png, ..., 10.png
        } else {
            expectedFormat = "%d.png"; // Ej: 1.png, 2.png, ..., 29.png
        }

        while (true) {
            String fileName = String.format(expectedFormat, i);
            String ruta = carpeta + "/" + fileName;

            if (Gdx.files.internal(ruta).exists()) {
                try {
                    frames.add(new TextureRegion(new Texture(ruta)));
                    Gdx.app.log("AnimationLoader", "Cargado frame: " + ruta);
                } catch (GdxRuntimeException e) {
                    // Captura errores como "Image not of any known type, or corrupt"
                    Gdx.app.error("AnimationLoader", "Error al cargar textura para " + ruta + ": " + e.getMessage());
                    // Si una textura está corrupta o es inválida, detenemos la carga de esta animación.
                    break;
                }
            } else {
                // Si no encuentra el archivo con el formato esperado, intenta con el otro formato
                // Esto es útil si hay inconsistencias en los nombres (ej. 1.png en carpeta de ataque)
                String alternateFormat = (expectedFormat.equals("%02d.png")) ? "%d.png" : "%02d.png";
                String alternateFileName = String.format(alternateFormat, i);
                String alternateRuta = carpeta + "/" + alternateFileName;

                if (Gdx.files.internal(alternateRuta).exists()) {
                    try {
                        frames.add(new TextureRegion(new Texture(alternateRuta)));
                        Gdx.app.log("AnimationLoader", "Cargado frame (formato alternativo): " + alternateRuta);
                    } catch (GdxRuntimeException e) {
                        Gdx.app.error("AnimationLoader", "Error al cargar textura para " + alternateRuta + ": " + e.getMessage());
                        break;
                    }
                } else {
                    Gdx.app.log("AnimationLoader", "Archivo no encontrado: " + ruta + " ni " + alternateRuta + ". Deteniendo carga para carpeta " + carpeta);
                    break; // Ningún formato encontrado, detener
                }
            }
            i++;
        }

        if (frames.size == 0) {
            Gdx.app.error("AnimationLoader", "CRÍTICO: No se cargaron fotogramas de animación para la carpeta: " + carpeta + ". ¡Verifica tus assets! Esto causará un error.");
            throw new GdxRuntimeException("No se encontraron fotogramas de animación para la carpeta: " + carpeta + ". Asegúrate de que los archivos existan y sean PNGs válidos.");
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

    public void iniciarAtaque(OnAtaqueFinalizado listener) {
        if (!atacando) {
            this.atacando = true;
            this.tiempoAnimacion = 0;
            this.listenerAtaque = listener;
            this.velocidadX = 0;
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
