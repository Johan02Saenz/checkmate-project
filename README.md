# Checkmate Lab

Checkmate Lab es una aplicación móvil nativa para el ecosistema Android diseñada como un entorno analítico y de entrenamiento autónomo de ajedrez. El sistema opera bajo un enfoque estrictamente local y desconectado (100% offline), permitiendo a los usuarios practicar sus tácticas contra oponentes virtuales (Bots de IA local) y almacenar de manera persistente sus estadísticas de juego directamente en el dispositivo móvil sin depender de servicios en la nube.

---

## Caracteristicas del MVP (Producto Minimo Viable)

* Entrenamiento Autonomo Offline: Juega partidas completas contra la IA sin consumo de datos ni conexión a internet.
* Gestión de Oponentes IA (HU-01): Selección de 5 niveles de dificultad de bots locales indexados por nivel de fuerza (ELO de 400 a 2000).
* Tablero de Juego Interactivo (HU-02): Renderizado matricial dinámico de 8x8 con validación estricta de movimientos legales (reglas oficiales de la FIDE).
* Persistencia Local Completa: Registro automático de historiales, estados de tablero en formato FEN, métricas de tiempo (cronómetros) y estadísticas generales de partidas jugadas.
* Diseño Moderno y Accesible: Interfaz gráfica optimizada bajo los estándares de Material Design 3 con soporte prioritario para modo oscuro (Dark Theme) que reduce la fatiga visual.

---

## Enfoque Metodologico e Investigacion

### Tipo de Investigacion
Este proyecto se define metodológicamente como un Estudio de Caso con alcance Descriptivo. Se centra de manera exclusiva en el análisis, modelado e implementación de la solución móvil particular Checkmate Lab, describiendo minuciosamente sus componentes arquitectónicos, las relaciones de datos y los criterios de usabilidad que viabilizan el desarrollo de software nativo bajo restricciones específicas de hardware.

### Justificacion Tecnica y Practica
* Teorica/Tecnica: Valida la implementación del patrón arquitectónico MVVM y la abstracción de bases de datos embebidas con Room, aislando los procesos pesados del hilo principal de la interfaz (Main Thread).
* Practica: Satisface la necesidad de aficionados y estudiantes de contar con un laboratorio de ajedrez táctico inmediato, con latencia cero en el renderizado de la matriz y un consumo mínimo de batería.

---

## Limitaciones del Proyecto

* Tiempo: El alcance del desarrollo está estrictamente acotado al cronograma académico del ciclo universitario vigente, limitando la ejecución de pruebas beta masivas.
* Recursos: Proyecto autofinanciado ejecutado con recursos locales de hardware del estudiante y herramientas open-source (Android Studio, Figma, SQLite/Room), prescindiendo de infraestructura de servidores de pago o APIs externas.
* Espacio y Sincronizacion: Los datos se almacenan exclusivamente en el directorio privado del dispositivo (/data/data/). Al no contar con arquitectura Cloud, si la aplicación es desinstalada, el historial y las estadísticas se perderán permanentemente. No existe portabilidad multidireccional ni emparejamiento multijugador online.

---

## Arquitectura del Software

El sistema implementa una arquitectura limpia y desacoplada basada en el patrón de diseño MVVM (Model-View-ViewModel) de Android Jetpack:


## Arquitectura de Datos

**Checkmate** implementa la arquitectura limpia estándar de Android utilizando el patrón **ViewModel → DAO → Room** para todas las operaciones de persistencia en el dispositivo.

* **Asincronía:** Las consultas a la base de datos se ejecutan estrictamente en hilos secundarios mediante **Corrutinas de Kotlin** (`Dispatchers.IO`), asegurando que la interfaz de usuario se mantenga fluida y libre de bloqueos.
* **Reactividad:** La capa de datos expone los resultados a la UI a través de **LiveData**, permitiendo una actualización en tiempo real cuando ocurren cambios.
* **Almacenamiento Local:** El sistema está diseñado bajo un enfoque *offline-first* absoluto. No existe capa de red ni repositorio remoto; el 100% de los datos residen de forma segura en el directorio privado del dispositivo.

---

## Cómo probar el CRUD

Sigue estos pasos dentro de la aplicación para verificar el ciclo completo de creación, lectura, actualización y eliminación de datos:

1. **Crear (Create):** Inicia una partida contra cualquier nivel de bot y complétala. Al finalizar, el sistema registrará la partida automáticamente en el historial local.

2. **Leer (Read):** Navega a la pantalla de **Historial** para verificar que el registro aparece correctamente detallado con su resultado, duración y el nivel del oponente contra el que jugaste.

3. **Actualizar (Update):** Toca cualquier partida del historial para acceder a su pantalla de detalle. Una vez ahí, edita la **nota de análisis**, guarda los cambios y regresa al historial para verificar que la modificación persiste.

4. **Eliminar (Delete):** Mantén presionada una partida de la lista y selecciona la opción de eliminar. Confirma la acción en el `AlertDialog`. Si lo necesitas, puedes presionar el botón de deshacer en el `Snackbar` emergente para revertir la eliminación inmediatamente.
