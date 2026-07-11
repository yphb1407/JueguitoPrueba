package com.miEquipo.Datos_y_almacen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Gestiona el ranking de puntuaciones del juego, cargando, guardando y añadiendo nuevas entradas.
 */
public class ScoreManager {
    private static final String PREFS_NAME = "juego_ranking";
    private static final String KEY_SCORES = "top_scores";
    private Preferences prefs;
    private LinkedList<ScoreEntry> topScores;
    private final int MAX_RANKING_SIZE = 5;

    /**
     * Inicializa el gestor de puntuaciones, cargando las puntuaciones guardadas.
     */
    public ScoreManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        topScores = new LinkedList<>();
        loadScores();
    }

    /**
     * Carga las puntuaciones del ranking desde las preferencias del juego.
     */
    private void loadScores() {
        String scoresString = prefs.getString(KEY_SCORES, "");
        if (!scoresString.isEmpty()) {
            String[] entries = scoresString.split(";");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    try {
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        topScores.add(new ScoreEntry(name, score));
                    } catch (NumberFormatException e) {
                        Gdx.app.error("ScoreManager", "Error parsing score entry: " + entry, e);
                    }
                }
            }
            Collections.sort(topScores);
            while (topScores.size() > MAX_RANKING_SIZE) {
                topScores.removeLast();
            }
        }
    }

    /**
     * Guarda las puntuaciones actuales del ranking en las preferencias del juego.
     */
    private void saveScores() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < topScores.size(); i++) {
            ScoreEntry entry = topScores.get(i);
            sb.append(entry.getName()).append(":").append(entry.getScore());
            if (i < topScores.size() - 1) {
                sb.append(";");
            }
        }
        prefs.putString(KEY_SCORES, sb.toString());
        prefs.flush();
    }

    /**
     * Añade una nueva puntuación al ranking, lo ordena y lo trunca si excede el tamaño máximo.
     * @param name Nombre del jugador.
     * @param score Puntuación obtenida.
     */
    public void addScore(String name, int score) {
        topScores.add(new ScoreEntry(name, score));
        Collections.sort(topScores);
        while (topScores.size() > MAX_RANKING_SIZE) {
            topScores.removeLast();
        }
        saveScores();
    }

    /**
     * Obtiene una copia de la lista de las mejores puntuaciones.
     * @return LinkedList de ScoreEntry con las mejores puntuaciones.
     */
    public LinkedList<ScoreEntry> getTopScores() {
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
