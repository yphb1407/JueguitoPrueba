package com.miEquipo.Datos_y_almacen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.Collections;
import java.util.LinkedList;

public class ScoreManager {
    private static final String PREFS_NAME = "juego_ranking";
    private static final String KEY_SCORES = "top_scores";
    private Preferences prefs;
    private LinkedList<ScoreEntry> topScores;
    private final int MAX_RANKING_SIZE = 5;

    public ScoreManager() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        topScores = new LinkedList<>();
        loadScores();
    }

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
            // Asegurarse de que no exceda el tamaño máximo al cargar
            while (topScores.size() > MAX_RANKING_SIZE) {
                topScores.removeLast();
            }
        }
    }

    private void saveScores() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < topScores.size(); i++) {
            ScoreEntry entry = topScores.get(i);
            sb.append(entry.name).append(":").append(entry.score);
            if (i < topScores.size() - 1) {
                sb.append(";");
            }
        }
        prefs.putString(KEY_SCORES, sb.toString());
        prefs.flush();
    }

    public void addScore(String name, int score) {
        topScores.add(new ScoreEntry(name, score));
        Collections.sort(topScores); // Ordenar de mayor a menor
        while (topScores.size() > MAX_RANKING_SIZE) {
            topScores.removeLast(); // Eliminar el más bajo si excede el límite
        }
        saveScores();
    }

    public LinkedList<ScoreEntry> getTopScores() {
        return new LinkedList<>(topScores); // Devolver una copia para evitar modificaciones externas
    }

    public int getHighestScore() {
        if (topScores.isEmpty()) {
            return 0;
        }
        return topScores.getFirst().score;
    }
}
