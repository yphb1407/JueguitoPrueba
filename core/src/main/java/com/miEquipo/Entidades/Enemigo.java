package com.miEquipo.Entidades;

import com.badlogic.gdx.Gdx; // Importar Gdx para logging (aunque ya no se usará para logs de posición)
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable; // Import Disposable

/**
 * Clase abstracta base para todos los enemigos en el juego.
 * Define propiedades y comportamientos comunes, como posición, velocidad, textura y dibujo.
 * Implementa Cloneable para permitir la creación de copias de enemigos.
 * Implementa Disposable para gestionar la liberación de recursos (texturas).
 */
public abstract class Enemigo implements Cloneable, Disposable {
    // --- Constants ---
    protected static final float DEFAULT_SPEED_X = -60f;
    protected static final float DEFAULT_WIDTH = 150;
    protected static final float DEFAULT_HEIGHT = 150;
    protected static final float GRAVITY = -1000f; // Gravedad aplicada a los enemigos
    protected static final float MAX_FALL_SPEED = -500f; // Velocidad máxima de caída

    protected float x, y;
    public float velocidadX;
    protected float velocidadY; // Velocidad en el eje Y para la gravedad
    protected Texture textura;
    protected String tipo;
    protected float ancho;
    protected float alto;

    // Referencias al mapa para colisiones
    protected TiledMap map;
    protected TiledMapTileLayer collisionLayer;
    protected int tileWidth;
    protected int tileHeight;

    /**
     * Constructor para la clase abstracta Enemigo.
     * Inicializa el tipo de enemigo, carga su textura y configura las referencias del mapa.
     * @param tipo El tipo o nombre del enemigo.
     * @param rutaTextura La ruta del archivo de textura del enemigo.
     * @param map El TiledMap del juego.
     * @param collisionLayer La capa de colisión del TiledMap.
     * @param tileWidth El ancho de los tiles del mapa.
     * @param tileHeight El alto de los tiles del mapa.
     */
    public Enemigo(String tipo, String rutaTextura, TiledMap map, TiledMapTileLayer collisionLayer, int tileWidth, int tileHeight) {
        this.tipo = tipo;
        this.textura = new Texture(rutaTextura);
        this.velocidadX = DEFAULT_SPEED_X;
        this.velocidadY = 0; // Inicialmente sin velocidad vertical
        this.ancho = DEFAULT_WIDTH;
        this.alto = DEFAULT_HEIGHT;

        this.map = map;
        this.collisionLayer = collisionLayer;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    /**
     * Actualiza el estado del enemigo en cada frame del juego.
     * Aplica gravedad y maneja colisiones con el mapa.
     * @param delta El tiempo transcurrido desde el último frame.
     */
    public void actualizar(float delta) {
        // --- Aplicar gravedad ---
        velocidadY += GRAVITY * delta;
        if (velocidadY < MAX_FALL_SPEED) {
            velocidadY = MAX_FALL_SPEED;
        }

        // --- Guardar posición antigua ---
        float oldX = x;
        float oldY = y;

        // --- Movimiento predicho ---
        float newX = x + velocidadX * delta;
        float newY = y + velocidadY * delta;

        // Log de estado inicial del frame - ELIMINADO
        // Gdx.app.log("Enemigo", String.format("Tipo: %s, Pos: (%.2f, %.2f), Vel: (%.2f, %.2f) - Inicio", tipo, x, y, velocidadX, velocidadY));


        // --- Manejar colisión vertical ---
        y = newY;
        Rectangle bounds = new Rectangle(x, y, ancho, alto);
        if (collidesWithMap(bounds)) {
            // Si colisiona, revertir a la posición Y anterior y ajustar
            y = oldY;
            if (velocidadY < 0) { // Cayendo
                // Ajustar la posición Y para que esté justo encima del tile sólido
                int bottomTileRow = (int) (oldY / tileHeight);
                y = (bottomTileRow + 1) * tileHeight;
                // Gdx.app.log("Enemigo", String.format("Tipo: %s, Colisión vertical (cayendo). Ajuste Y a %.2f", tipo, y)); // ELIMINADO
            } else if (velocidadY > 0) { // Subiendo (chocando con el techo)
                // Ajustar la posición Y para que esté justo debajo del tile sólido
                int topTileRow = (int) ((oldY + alto) / tileHeight);
                y = topTileRow * tileHeight - alto;
                // Gdx.app.log("Enemigo", String.format("Tipo: %s, Colisión vertical (subiendo). Ajuste Y a %.2f", tipo, y)); // ELIMINADO
            }
            velocidadY = 0; // Detener movimiento vertical
        }

        // --- Manejar colisión horizontal ---
        x = newX;
        bounds.setPosition(x, y); // Actualizar bounds para la verificación horizontal
        if (collidesWithMap(bounds)) {
            x = oldX; // Revertir a la posición X anterior
            velocidadX = -velocidadX; // Rebotar o cambiar de dirección
            // Gdx.app.log("Enemigo", String.format("Tipo: %s, Colisión horizontal. Revertir X a %.2f, nueva VelX %.2f", tipo, x, velocidadX)); // ELIMINADO
        }

        // Log de estado final del frame - ELIMINADO
        // Gdx.app.log("Enemigo", String.format("Tipo: %s, Pos: (%.2f, %.2f), Vel: (%.2f, %.2f) - Fin", tipo, x, y, velocidadX, velocidadY));
    }

    /**
     * Dibuja el enemigo en la pantalla.
     * @param batch El SpriteBatch utilizado para dibujar texturas.
     */
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, x, y, ancho, alto);
    }

    /**
     * Crea y devuelve una copia de este objeto.
     * @return Una copia superficial de este objeto Enemigo.
     */
    @Override
    public Enemigo clone() {
        try {
            Enemigo newEnemy = (Enemigo) super.clone();
            // Asegurarse de que las referencias del mapa se copien
            newEnemy.map = this.map;
            newEnemy.collisionLayer = this.collisionLayer;
            newEnemy.tileWidth = this.tileWidth;
            newEnemy.tileHeight = this.tileHeight;
            // La textura se comparte entre clones, lo cual es eficiente.
            return newEnemy;
        } catch (CloneNotSupportedException e) {
            // Esto no debería ocurrir ya que implementamos Cloneable
            throw new InternalError(e);
        }
    }

    /**
     * Establece la posición del enemigo en el mundo del juego.
     * @param x La nueva coordenada X del enemigo.
     * @param y La nueva coordenada Y del enemigo.
     */
    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Obtiene la coordenada X actual del enemigo.
     * @return La coordenada X del enemigo.
     */
    public float getX() { return x; }

    /**
     * Obtiene la coordenada Y actual del enemigo.
     * @return La coordenada Y del enemigo.
     */
    public float getY() { return y; }

    /**
     * Obtiene el ancho del enemigo.
     * @return El ancho del enemigo.
     */
    public float getAncho() { return ancho; }

    /**
     * Obtiene el alto del enemigo.
     * @return El alto del enemigo.
     */
    public float getAlto() { return alto; }

    /**
     * Libera los recursos utilizados por el enemigo.
     * NOTA: No liberamos la textura aquí porque, al usar el patrón Prototype,
     * la textura es compartida entre todos los clones. Si un clon la libera,
     * todos los demás enemigos (y el prototipo) se quedan sin ella.
     */
    @Override
    public void dispose() {
        // No disponemos la textura aquí para evitar que los clones la destruyan prematuramente.
    }

    /**
     * Libera específicamente la textura del enemigo.
     * Este método debe ser llamado solo cuando estemos seguros de que la textura
     * ya no será necesaria por ningún clon (por ejemplo, al cerrar el juego).
     */
    public void disposeTexture() {
        if (textura != null) {
            textura.dispose();
            textura = null;
        }
    }

    // --- Métodos de colisión con el mapa Tiled (movidos de PartidaFacade) ---

    // Helper method to check if a tile ID is considered solid/collidable
    protected boolean isTileSolid(int tileId) {
        // GID 1 y 2 son los tiles de colisión según tu descripción
        return tileId == 1 || tileId == 2;
    }

    // Helper method to check if a rectangle collides with any solid tile in the map
    protected boolean collidesWithMap(Rectangle rect) {
        if (collisionLayer == null) return false;

        // Check the four corners of the rectangle for collision
        // También se pueden revisar los puntos medios para mayor precisión
        if (checkPointCollision(rect.x, rect.y) ||
            checkPointCollision(rect.x + rect.width, rect.y) ||
            checkPointCollision(rect.x, rect.y + rect.height) ||
            checkPointCollision(rect.x + rect.width, rect.y + rect.height) ||
            checkPointCollision(rect.x + rect.width / 2, rect.y) || // Punto medio inferior
            checkPointCollision(rect.x + rect.width / 2, rect.y + rect.height) // Punto medio superior
        ) {
            return true;
        }
        return false;
    }

    // Helper method to check if a specific point collides with a solid tile
    protected boolean checkPointCollision(float x, float y) {
        int col = (int) (x / tileWidth);
        int row = (int) (y / tileHeight);

        // Ensure the point is within map bounds
        if (col < 0 || row < 0 || col >= map.getProperties().get("width", Integer.class) || row >= map.getProperties().get("height", Integer.class)) {
            return false;
        }

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(col, row);
        return cell != null && isTileSolid(cell.getTile().getId());
    }
}
