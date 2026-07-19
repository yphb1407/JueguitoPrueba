# 🎮 Juego Prueba - LibGDX

Un videojuego 2D desarrollado con **LibGDX** que implementa patrones de diseño para crear una experiencia de juego completa, modular y mantenible.

---

## 📋 Descripción del Proyecto

"Juego Prueba" es un videojuego de acción 2D donde el jugador controla a un personaje que debe sobrevivir a oleadas de enemigos mientras recolecta items de regeneración. El objetivo es acumular la mayor puntuación posible derrotando enemigos y manteniendo la vida del personaje.

El proyecto ha sido desarrollado como parte de un trabajo académico para demostrar la aplicación de patrones de diseño en el desarrollo de videojuegos. Hemos utilizado el framework LibGDX por su robustez y su amplia comunidad de desarrolladores.

### Características Principales
- 🏃 **Movimiento completo**: caminar, saltar y control en el aire
- ⚔️ **Sistema de combate**: ataque con proyectiles
- ❤️ **Sistema de vida**: regeneración mediante items
- 👾 **Enemigos**: oleadas con dificultad progresiva
- 🏆 **Ranking**: Top 5 de mejores puntuaciones
- 💾 **Persistencia**: guardado automático de récords
- 🎨 **Efectos visuales**: textos flotantes y decoradores visuales
- 📱 **Interfaz intuitiva**: menús simples y navegación clara

---

## 🏗️ Arquitectura y Patrones de Diseño

El proyecto implementa cinco patrones de diseño fundamentales que garantizan escalabilidad, mantenibilidad y separación de responsabilidades.

### 1. Adapter Pattern
- **Interfaz**: `ScoreRepository`
- **Implementación**: `GdxScoreAdapter`
- **Propósito**: Abstrae la persistencia de puntuaciones usando Preferences de LibGDX
- **Ventaja**: Permite cambiar fácilmente el sistema de almacenamiento sin afectar al resto del juego. Por ejemplo, en el futuro podríamos implementar un adaptador para SQLite o almacenamiento en la nube sin modificar la lógica del juego.

### 2. Decorator Pattern
- **Base**: `PersonajeDecorator` (abstracto)
- **Decoradores concretos**:
  - `FuegoDecorator` - Efecto visual rojo al personaje
  - `VidaRegeneracionDecorator` - Regeneración de vida con efecto verde parpadeante
- **Propósito**: Añadir funcionalidades dinámicamente sin modificar la clase original del personaje
- **Ventaja**: Podemos combinar múltiples decoradores en tiempo de ejecución (ej: personaje en llamas con regeneración)

### 3. State Pattern
- **Interfaz**: `PersonajeState`
- **Estados**:
  - `EstadoQuieto` - Reposo sin movimiento, transición a caminar o saltar
  - `EstadoCaminando` - Movimiento horizontal, transición a saltar o quieto
  - `EstadoSaltando` - En el aire con gravedad, control horizontal limitado
- **Propósito**: Gestionar el comportamiento del personaje según su estado actual
- **Ventaja**: Cada estado encapsula su propia lógica, facilitando la adición de nuevos estados

### 4. Factory Pattern
- **Clase**: `EnemigoFactory`
- **Propósito**: Centralizar la creación de diferentes tipos de enemigos
- **Ventaja**: Extensible para nuevos tipos sin modificar el código existente. Basta con agregar un nuevo caso en el método de fábrica.

### 5. Facade Pattern
- **Clase**: `PartidaFacade`
- **Propósito**: Orquestar toda la lógica del juego y simplificar la interacción entre componentes
- **Ventaja**: Oculta la complejidad interna del juego, proporcionando una interfaz simple para la pantalla de juego

---

## 🎮 Mecánicas del Juego

### Movimiento y Física
El sistema de movimiento está diseñado para ser responsivo y natural, con físicas que simulan gravedad y control en el aire.

| Acción | Tecla | Velocidad | Descripción |
|--------|-------|-----------|-------------|
| Caminar | ← → | 200 px/s | Movimiento horizontal básico |
| Saltar | SPACE | 450 px/s (inicial) | Impulso vertical desde el suelo |
| Gravedad | - | -1500 px/s² | Aceleración constante hacia abajo |
| Control en aire | ← → | 200 px/s | Movimiento horizontal limitado durante el salto |
| Detenerse | Soltar tecla | 0 px/s | Transición automática a EstadoQuieto |

### Combate
El sistema de combate es sencillo pero efectivo, con un ciclo de ataque que incluye animación y disparo de proyectiles.

- **Ataque**: Tecla ENTER
- **Animación**: Duración aproximada de 0.5 segundos
- **Proyectil**: Velocidad de 400 px/s en dirección del personaje
- **Daño a enemigos**: 100 puntos por eliminación
- **Daño al jugador**: -10 HP por contacto con enemigo
- **Texto flotante**: "+100" en amarillo, "-10 HP" en rojo

### Sistema de Vida
El sistema de vida incluye regeneración mediante items recolectables en el mapa.

- **Vida inicial**: 20 HP
- **Vida máxima**: 500 HP
- **Regeneración**: +3 HP/segundo durante 15 segundos
- **Items de regeneración**: Aparecen cada 20-40 segundos
- **Efecto visual**: Tinte verde parpadeante durante la regeneración
- **Indicadores**: Textos flotantes "+3 HP" en verde

### Enemigos
Los enemigos son el principal desafío del juego, con un sistema de spawn que aumenta la dificultad progresivamente.

- **Tipo**: Caminante (se mueve horizontalmente)
- **Textura**: "enemigo.png" (64x64 píxeles)
- **Velocidad**: 100-200 px/s (aleatoria por spawn)
- **Spawn inicial**: Cada 2 segundos
- **Spawn mínimo**: 0.7 segundos (dificultad máxima)
- **Direcciones**: Desde ambos lados de la pantalla
- **Eliminación**: Al salir de la pantalla o al ser alcanzado por un proyectil

---

## 🖥️ Pantallas y Flujo

### 1. MenuScreen
La pantalla de menú principal, con un diseño limpio y navegación intuitiva.

- **Fondo**: Textura "fondo.png" con fallback a color negro
- **Título**: "JUEGUITO PRUEBA" centrado con escala 2x
- **Botones**:
  - "JUGAR" (escala 1.5x) → Inicia GameScreen
  - "RANK" (escala 1.5x) → Muestra RankScreen
- **Interacción**: Detección de toques con bounding boxes precisas
- **Recursos**: Utiliza GlyphLayout para centrado perfecto de texto

### 2. GameScreen
La pantalla principal del juego, donde ocurre toda la acción.

- **Renderizado**: Fondo oscuro con textura de terreno
- **UI**:
  - VIDA: número actual (blanco)
  - SCORE: puntuación actual (blanco)
  - MAX: récord histórico (dorado)
- **Entidades**: Personaje, enemigos, proyectiles, items, textos flotantes
- **Game Over**: 
  - Mensaje rojo "GAME OVER"
  - Diálogo para ingresar nombre
  - Guardado automático en ScoreManager
  - Transición a RankScreen

### 3. RankScreen
La pantalla de ranking que muestra los mejores jugadores.

- **Fondo**: Oscuro (0.1, 0.1, 0.1)
- **Título**: "TOP 5 RANKING" en dorado (escala 2x)
- **Lista**: Numeración del 1 al 5
- **Formato**: "1. Nombre - Puntuación"
- **Relleno**: "---" para posiciones vacías
- **Botón**: "Volver al Menú" en cian
- **Interacción**: Toque en el botón para regresar

### Flujo de Navegación
El flujo de navegación es sencillo e intuitivo para el usuario.
