package com.miEquipo.managers;

import com.miEquipo.Entidades.Enemigo;
import com.miEquipo.Factory.EnemigoFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SpawnManagerTest {

    private EntityManager entityManager;
    private EnemigoFactory enemigoFactory;
    private SpawnManager spawnManager;
    private Enemigo enemigo;

    @BeforeEach
    void setUp() {

        entityManager = mock(EntityManager.class);
        enemigoFactory = mock(EnemigoFactory.class);
        enemigo = mock(Enemigo.class);

        when(enemigoFactory.crearEnemigo(
            anyString(),
            anyFloat(),
            anyFloat(),
            anyFloat()
        )).thenReturn(enemigo);

        spawnManager = new SpawnManager(
            entityManager,
            enemigoFactory,
            1000f,
            600f
        );
    }

    @Test
    void noDebeGenerarEnemigosAntesDelTiempoDeSpawn() {

        spawnManager.update(1f, 0);

        verify(entityManager, never())
            .addEnemigo(any());
    }

    @Test
    void debeGenerarEnemigoCuandoSeCumpleElTiempo() {

        spawnManager.update(2.1f, 0);

        verify(enemigoFactory)
            .crearEnemigo(
                eq("goomba"),
                anyFloat(),
                anyFloat(),
                anyFloat()
            );

        verify(entityManager)
            .addEnemigo(enemigo);
    }

    @Test
    void debeGenerarEnemigoConMayorDificultad() {

        spawnManager.update(0.3f, 4000);

        verify(entityManager)
            .addEnemigo(any());
    }

    @Test
    void nuncaDebeReducirElTiempoPorDebajoDelMinimo() {

        spawnManager.update(0.21f, 100000);

        verify(entityManager)
            .addEnemigo(any());
    }

    @Test
    void noDebeGenerarItemAntesDelTiempo() {

        spawnManager.update(1f, 0);

        verify(entityManager, never())
            .addItem(any());
    }

    @Test
    void debeGenerarItemDespuesDeVariasActualizaciones() {

        for (int i = 0; i < 50; i++) {
            spawnManager.update(1f, 0);
        }

        verify(entityManager, atLeastOnce())
            .addItem(any());
    }

    @Test
    void debePoderGenerarEnemigosEItemsEnLaMismaActualizacion() {

        for (int i = 0; i < 50; i++) {
            spawnManager.update(1f, 5000);
        }

        verify(entityManager, atLeastOnce())
            .addEnemigo(any());

        verify(entityManager, atLeastOnce())
            .addItem(any());
    }
}
