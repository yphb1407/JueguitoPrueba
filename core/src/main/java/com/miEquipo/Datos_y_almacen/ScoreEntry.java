package com.miEquipo.Datos_y_almacen;

import java.util.Objects;

/**
 * Representa una entrada de puntuación en el ranking, con un nombre de jugador y su score.
 */
public class ScoreEntry implements Comparable<ScoreEntry> {
    private final String name;
    private final int score;

    /**
     * Crea una nueva entrada de puntuación.
     * @param name Nombre del jugador.
     * @param score Puntuación obtenida.
     */
    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Obtiene el nombre del jugador.
     * @return Nombre del jugador.
     */
    public String getName() {
        return name;
    }

    /**
     * Obtiene la puntuación del jugador.
     * @return Puntuación del jugador.
     */
    public int getScore() {
        return score;
    }

    /**
     * Compara esta entrada con otra para ordenar por score de forma descendente.
     * @param other Otra entrada de puntuación.
     * @return Un valor negativo si esta entrada es mayor, positivo si es menor, o cero si son iguales.
     */
    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }

    /**
     * Compara si dos objetos ScoreEntry son iguales.
     * @param o Objeto a comparar.
     * @return true si son iguales, false en caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreEntry that = (ScoreEntry) o;
        return score == that.score && Objects.equals(name, that.name);
    }

    /**
     * Genera el código hash para el objeto ScoreEntry.
     * @return Código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }

    /**
     * Proporciona una representación en cadena del objeto ScoreEntry.
     * @return Representación en cadena.
     */
    @Override
    public String toString() {
        return "ScoreEntry{" +
               "name='" + name + '\'' +
               ", score=" + score +
               '}';
    }
}
