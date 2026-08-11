# WonderExample (WEX)

Expansion de ejemplo para WonderEvents. Sirve como plantilla/tour de como
esta armada una expansion (config, messages, database YML, comandos y
listeners), calcada de la distribucion de source code de Extremo3Vanilla
pero reducida a lo minimo.

## Como compilar

1. Copia tu `WonderEvents.jar` (el build del core) a la raiz de este
   proyecto, junto al `pom.xml` (mismo mecanismo que usa E3V: dependencia
   `system` apuntando a `${project.basedir}/WonderEvents.jar`).
2. `mvn clean package`
3. El jar queda en `target/WE-Expansion-WonderExample-1.0.0_WO.jar`
   (la ofuscacion con yguard esta comentada en el `pom.xml`; si la queres,
   descomenta ese plugin, igual que en E3V, y el jar final quedara sin el
   sufijo `_WO`).
4. Soltalo en `plugins/WonderEvents/expansions/`.

No requiere ninguna dependencia de plugin externo (ni PlaceholderAPI ni
nada): corre en Paper o Spigot puro, en cualquier version soportada por
WonderEvents, gracias a que Adventure/MiniMessage se resuelve por
reflexion (ver `UniversalFormatter` y `PaperPlatformAdapter`) en vez de
como dependencia dura en tiempo de compilacion.

## Que hace

- Al cargar, loguea un saludo en consola avisando que es la expansion de
  ejemplo y que se puede borrar sin problema.
- Da la bienvenida (y registra en `data/wex_db.yml`) a cualquier jugador
  que WEX todavia no conocia.
- Cuando el servidor salta la noche porque todos estan durmiendo
  (`TimeSkipEvent` con `SkipReason.NIGHT_SKIP`), le manda a todos los
  jugadores online un mensaje de "buenos dias".
- Agrega el comando `/wex`, que responde con un mensaje fijo.

Los tres mensajes de gameplay/comando son 100% configurables desde
`config.yml` (raiz `Messages`).
