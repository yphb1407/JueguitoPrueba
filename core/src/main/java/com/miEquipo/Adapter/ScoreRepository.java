package com.miEquipo.Adapter;

import com.miEquipo.Datos_y_almacen.ScoreEntry;
import java.util.List;

public interface ScoreRepository {
    void saveScores(List<ScoreEntry> scores);
    List<ScoreEntry> loadScores();
}
