package com.miEquipo.Entidades;

/**
 * Representa un tipo de enemigo que se mueve horizontalmente.
 * Extiende la clase abstracta Enemigo.
 */
public class EnemigoCaminante extends Enemigo {

    // --- Constants ---
    private static final String ENEMY_TYPE = "Caminante";
    private static final String TEXTURE_PATH = "enemigo.png";
    private static final float DEFAULT_WALKING_SPEED_X = -100f; // Velocidad por defecto si no se especifica

    /**
     * Constructor para crear un EnemigoCaminante con una posición y velocidad específicas.
     * @param x La coordenada X inicial del enemigo.
     * @param y La coordenada Y inicial del enemigo.
     * @param velX La velocidad inicial en el eje X del enemigo.
     */
    public EnemigoCaminante(float x, float y, float velX) {
        super(ENEMY_TYPE, TEXTURE_PATH);
        this.x = x;
        this.y = y;
        this.velocidadX = velX;
    }

    /**
     * Constructor por defecto para crear un EnemigoCaminante.
     * Utiliza una velocidad X predefinida.
     */
    public EnemigoCaminante() {
        this(0, 0, DEFAULT_WALKING_SPEED_X); // Llama al constructor principal con valores por defecto
    }

    /**
     * Actualiza la posición del enemigo en cada frame.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    @Override
    public void actualizar(float delta) {
        this.x += velocidadX * delta;
    }
}
