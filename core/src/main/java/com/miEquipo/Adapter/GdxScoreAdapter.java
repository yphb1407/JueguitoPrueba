package com.miEquipo.Adapter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
//import com.miEquipo.Datos_y_almacen.ScoreRepository;

public class GdxScoreAdapter implements ScoreRepository {
    private static final String PREFS_NAME = "juego_scores";
    private Preferences prefs;

    public GdxScoreAdapter() {
        this.prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    @Override
    public void guardarRecord(String nombre, int score) {
        prefs.putInteger("high_score", score);
        prefs.putString("player_name", nombre);
        prefs.flush(); // Guarda físicamente en disco
        System.out.println("💾 Récord guardado mediante Gdx Preferences: " + score);
    }

    @Override
    public int cargarRecord() {
        return prefs.getInteger("high_score", 0);
    }
}
