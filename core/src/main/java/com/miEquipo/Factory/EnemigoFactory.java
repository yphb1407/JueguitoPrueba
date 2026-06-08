package com.miEquipo.Factory;

import com.miEquipo.Entidades.Enemigo;
import com.miEquipo.Entidades.EnemigoCaminante;
import java.util.HashMap;
import java.util.Map;

public class EnemigoFactory {
    public static Enemigo crearEnemigo(String tipo, float x, float y, float velX) {
        if (tipo.equals("goomba")) {
            return new EnemigoCaminante(x, y, velX);
        }
        return null;
    }

    public static Enemigo crearEnemigo(String tipo, float x, float y) {
        return crearEnemigo(tipo, x, y, -100f);
    }
}
