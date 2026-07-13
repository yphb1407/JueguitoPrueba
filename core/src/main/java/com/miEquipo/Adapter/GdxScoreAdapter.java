package com.miEquipo.Adapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.miEquipo.Datos_y_almacen.ScoreEntry;

import java.util.ArrayList;
import java.util.List;

public class GdxScoreAdapter implements ScoreRepository {
    private static final String PREFS_NAME = "juego_ranking";
    private static final String KEY_SCORES = "top_scores";
    private Preferences prefs;

    public GdxScoreAdapter() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    @Override
    public void saveScores(List<ScoreEntry> scores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            ScoreEntry entry = scores.get(i);
            sb.append(entry.getName()).append(":").append(entry.getScore());
            if (i < scores.size() - 1) {
                sb.append(";");
            }
        }
        prefs.putString(KEY_SCORES, sb.toString());
        prefs.flush();
    }

    @Override
    public List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        String scoresString = prefs.getString(KEY_SCORES, "");
        if (!scoresString.isEmpty()) {
            String[] entries = scoresString.split(";");
            for (String entry : entries) {
                String[] parts = entry.split(":");
                if (parts.length == 2) {
                    try {
                        String name = parts[0];
                        int score = Integer.parseInt(parts[1]);
                        scores.add(new ScoreEntry(name, score));
                    } catch (NumberFormatException e) {
                        Gdx.app.error("GdxScoreAdapter", "Error parsing score entry: " + entry, e);
                    }
                }
            }
        }
        return scores;
    }
}
