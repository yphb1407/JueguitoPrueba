package com.miEquipo.Datos_y_almacen;

import com.miEquipo.Adapter.ScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreManagerTest {
    private ScoreRepository repository;
    private ScoreManager scoreManager;
    @BeforeEach
    void setUp() {
        repository = mock(ScoreRepository.class);
        when(repository.loadScores())
            .thenReturn(new ArrayList<>());
        scoreManager = new ScoreManager(repository);
    }


    @Test
    void debeInicializarConScoresDelRepositorio() {
        List<ScoreEntry> scores = List.of(
            new ScoreEntry("Juan", 500),
            new ScoreEntry("Pedro", 300)
        );
        when(repository.loadScores())
            .thenReturn(scores);
        ScoreManager manager = new ScoreManager(repository);
        assertEquals(500, manager.getHighestScore());
        assertEquals(2, manager.getTopScores().size());
    }


    @Test
    void debeAgregarUnScoreYGuardarlo() {
        scoreManager.addScore("Carlos", 1000);
        assertEquals(1000, scoreManager.getHighestScore());
        verify(repository)
            .saveScores(anyList());
    }


    @Test
    void debeOrdenarScoresDeMayorAMenor() {
        scoreManager.addScore("Jugador1", 200);
        scoreManager.addScore("Jugador2", 800);
        scoreManager.addScore("Jugador3", 400);
        List<ScoreEntry> scores =
            scoreManager.getTopScores();
        assertEquals(800,
            scores.get(0).getScore());
        assertEquals(400,
            scores.get(1).getScore());
        assertEquals(200,
            scores.get(2).getScore());
    }


    @Test
    void noDebeSuperarLosCincoMejoresScores() {
        for(int i = 1; i <= 10; i++) {
            scoreManager.addScore(
                "Jugador " + i,
                i * 100
            );
        }


        List<ScoreEntry> scores =
            scoreManager.getTopScores();
        assertEquals(5, scores.size());
        assertEquals(1000,
            scores.get(0).getScore());
        assertEquals(600,
            scores.get(4).getScore());
    }


    @Test
    void debeRetornarCeroSiNoHayScores() {
        assertEquals(
            0,
            scoreManager.getHighestScore()
        );
    }


    @Test
    void getTopScoresDebeDevolverUnaCopia() {

        scoreManager.addScore(
            "Jugador",
            500
        );
        List<ScoreEntry> lista =
            scoreManager.getTopScores();
        lista.clear();
        assertEquals(
            1,
            scoreManager.getTopScores().size()
        );
    }


    @Test
    void debeGuardarLaListaActualizadaAlAgregarScore() {

        ArgumentCaptor<List<ScoreEntry>> captor =
            ArgumentCaptor.forClass(List.class);
        scoreManager.addScore(
            "Mario",
            900
        );
        verify(repository)
            .saveScores(captor.capture());
        List<ScoreEntry> guardados =
            captor.getValue();
        assertEquals(
            900,
            guardados.get(0).getScore()
        );
    }
}
