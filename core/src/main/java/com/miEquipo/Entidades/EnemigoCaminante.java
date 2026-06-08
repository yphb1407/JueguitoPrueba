package com.miEquipo.Entidades;

public class EnemigoCaminante extends Enemigo {

    public EnemigoCaminante(float x, float y, float velX) {
        super("Caminante", "enemigo.png");
        this.x = x;
        this.y = y;
        this.velocidadX = velX;
    }

    public EnemigoCaminante() {
        super("Caminante", "enemigo.png");
        this.velocidadX = -100f;
    }

    @Override
    public void actualizar(float delta) {
        this.x += velocidadX * delta;
    }
}
