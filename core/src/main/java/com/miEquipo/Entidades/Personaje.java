package com.miEquipo.Entidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Disposable; // Import Disposable
import com.miEquipo.patron_states.PersonajeState;
import com.miEquipo.patron_states.EstadoQuieto;

public class Personaje implements ComponentePersonaje, Disposable { // Implementa Disposable
    // --- Constants ---
    private static final float ANIMATION_FRAME_DURATION = 0.05f;
    private static final float CHARACTER_WIDTH = 150;
    private static final float CHARACTER_HEIGHT = 150;
    private static final String ANIM_FOLDER_REPOSO = "personaje_reposo";
    private static final String ANIM_FOLDER_ATAQUE = "personaje_ataque";
    private static final String LOG_TAG_ANIM_LOADER = "AnimationLoader";
    private static final String ERROR_MSG_ANIM_LOAD_FAILED = "Error al cargar textura para %s: %s";
    private static final String ERROR_MSG_NO_FRAMES = "CRÍTICO: No se cargaron fotogramas de animación para la carpeta: %s. ¡Verifica tus assets! Esto causará un error.";

    // Cooldown para el disparo de proyectiles
    private static final float COOLDOWN_DISPARO = 0.3f; // 0.3 segundos entre disparos
    private float tiempoUltimoDisparo = 0;

    private float x, y;
    private float velocidadX, velocidadY;
    private PersonajeState estadoActual;

    private Animation<TextureRegion> animReposo;
    private Animation<TextureRegion> animAtaque;
    private float tiempoAnimacion = 0;

    private boolean atacando = false;
    private boolean mirandoDerecha = true;

    // Lista para almacenar todas las texturas cargadas para poder liberarlas
    private Array<Texture> loadedTextures;

    public interface OnAtaqueFinalizado {
        void alFinalizar();
    }
    private OnAtaqueFinalizado listenerAtaque;

    public Personaje(float x, float y) {
        this.x = x;
        this.y = y;
        this.estadoActual = new EstadoQuieto();
        this.loadedTextures = new Array<>(); // Inicializar la lista de texturas

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
                    Texture texture = new Texture(ruta); // Cargar la textura
                    frames.add(new TextureRegion(texture));
                    loadedTextures.add(texture); // Añadir a la lista para liberar
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
                        Texture texture = new Texture(alternateRuta); // Cargar la textura
                        frames.add(new TextureRegion(texture));
                        loadedTextures.add(texture); // Añadir a la lista para liberar
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
        tiempoUltimoDisparo += delta; // Actualizar el tiempo del cooldown

        if (!atacando) {
            estadoActual.manejarEntrada(this);
            estadoActual.actualizar(this, delta);
        }

        if (velocidadX > 0) mirandoDerecha = true;
        else if (velocidadX < 0) mirandoDerecha = false;

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

    /**
     * Intenta disparar un proyectil.
     * @return Una instancia de Proyectil si se pudo disparar, o null si el cooldown no ha terminado.
     */
    public Proyectil disparar() {
        if (tiempoUltimoDisparo >= COOLDOWN_DISPARO) {
            // Calcular la posición inicial del proyectil para que salga del centro del personaje
            float proyectilX = mirandoDerecha ? x + CHARACTER_WIDTH : x - Proyectil.WIDTH;
            float proyectilY = y + CHARACTER_HEIGHT / 2 - Proyectil.HEIGHT / 2;

            Proyectil nuevoProyectil = new Proyectil(proyectilX, proyectilY, mirandoDerecha);
            tiempoUltimoDisparo = 0; // Reiniciar el cooldown
            return nuevoProyectil;
        }
        return null;
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
    @Override public void setX(float x) { this.x = x; }
    @Override public float getY() { return y; }
    @Override public void setY(float y) { this.y = y; }
    @Override public float getVelocidadY() { return velocidadY; }
    @Override public void setVelocidadY(float vy) { this.velocidadY = vy; }
    @Override public float getVelocidadX() { return velocidadX; }
    @Override public void setVelocidadX(float vx) { this.velocidadX = vx; }
    @Override public float getWidth() { return CHARACTER_WIDTH; }
    @Override public float getHeight() { return CHARACTER_HEIGHT; }

    @Override
    public void dispose() {
        // Liberar todas las texturas cargadas
        for (Texture texture : loadedTextures) {
            texture.dispose();
        }
        loadedTextures.clear();
    }
}
