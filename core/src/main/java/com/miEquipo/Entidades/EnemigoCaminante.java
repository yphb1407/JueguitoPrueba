package com.miEquipo.Entidades;

/**
 * Representa un tipo de enemigo que se mueve horizontalmente, persigue al jugador y rebota en los bordes.
 * Extiende la clase abstracta Enemigo.
 */
public class EnemigoCaminante extends Enemigo {

    // --- Constants ---
    private static final String ENEMY_TYPE = "Caminante";
    private static final String TEXTURE_PATH = "enemigo.png"; // Asegúrate de que esta textura exista
    private static final float BASE_CHASE_SPEED = 100f; // Velocidad base para perseguir al jugador

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
        this(0, 0, BASE_CHASE_SPEED); // Llama al constructor principal con valores por defecto
    }

    /**
     * Actualiza la posición y el comportamiento del enemigo en cada frame.
     * Implementa lógica de persecución horizontal y rebote en los bordes del mapa.
     * @param delta El tiempo transcurrido desde el último frame.
     * @param playerX La coordenada X del jugador.
     * @param playerY La coordenada Y del jugador.
     * @param mapWidthInPixels El ancho del mapa en píxeles.
     * @param mapHeightInPixels El alto del mapa en píxeles.
     */
    @Override
    public void actualizar(float delta, float playerX, float playerY, float mapWidthInPixels, float mapHeightInPixels) {
        // --- Lógica de Persecución Horizontal ---
        // Si el enemigo está a la izquierda del jugador, se mueve a la derecha.
        // Si el enemigo está a la derecha del jugador, se mueve a la izquierda.
        if (playerX < this.x) {
            this.velocidadX = -BASE_CHASE_SPEED;
        } else if (playerX > this.x) {
            this.velocidadX = BASE_CHASE_SPEED;
        } else {
            this.velocidadX = 0; // Si está en la misma X, se detiene horizontalmente
        }

        // --- Movimiento ---
        this.x += velocidadX * delta;
        // No hay movimiento vertical por ahora, pero se podría añadir gravedad o movimiento vertical aquí.
        // this.y += velocidadY * delta;

        // --- Rebote en los bordes del mapa ---
        if (this.x < 0) {
            this.x = 0; // Ajusta la posición para que no se salga
            revertirDireccionX(); // Invierte la dirección
        } else if (this.x + this.ancho > mapWidthInPixels) {
            this.x = mapWidthInPixels - this.ancho; // Ajusta la posición
            revertirDireccionX(); // Invierte la dirección
        }
        // No se implementa rebote vertical por ahora, ya que los enemigos "caminantes" suelen estar en el suelo.
    }
}
