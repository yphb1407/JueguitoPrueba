package com.miEquipo.Datos_y_almacen;

import com.miEquipo.Adapter.ScoreRepository;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Gestiona el ranking de puntuaciones del juego, coordinando la lógica de negocio.
 * Utiliza un ScoreRepository para la persistencia de datos.
 */
public class ScoreManager {
    private final ScoreRepository repository;
    private final LinkedList<ScoreEntry> topScores;
    private final int MAX_RANKING_SIZE = 5;

    /**
     * Inicializa el gestor de puntuaciones con un repositorio específico.
     * @param repository Repositorio para cargar y guardar datos.
     */
    public ScoreManager(ScoreRepository repository) {
        this.repository = repository;
        this.topScores = new LinkedList<>(repository.loadScores());
        sortAndTruncate();
    }

    /**
     * Añade una nueva puntuación al ranking, lo ordena y lo guarda si es necesario.
     * @param name Nombre del jugador.
     * @param score Puntuación obtenida.
     */
    public void addScore(String name, int score) {
        topScores.add(new ScoreEntry(name, score));
        sortAndTruncate();
        repository.saveScores(topScores);
    }

    private void sortAndTruncate() {
        Collections.sort(topScores);
        while (topScores.size() > MAX_RANKING_SIZE) {
            topScores.removeLast();
        }
    }

    /**
     * Obtiene una copia de la lista de las mejores puntuaciones.
     * @return List de ScoreEntry con las mejores puntuaciones.
     */
    public List<ScoreEntry> getTopScores() {
        return new LinkedList<>(topScores);
    }

    /**
     * Obtiene la puntuación más alta registrada.
     * @return La puntuación más alta, o 0 si el ranking está vacío.
     */
    public int getHighestScore() {
        if (topScores.isEmpty()) {
            return 0;
        }
        return topScores.getFirst().getScore();
    }
}
