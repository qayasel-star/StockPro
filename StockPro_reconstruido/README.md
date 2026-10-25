# StockPro — base reconstruida independiente

Aplicación Android nativa para tienda general. Kotlin, Jetpack Compose, Material 3 y Room/SQLite. El proyecto se reconstruyó desde los requisitos documentados porque el ZIP de transferencia recibido no contenía los fuentes Kotlin ni la configuración Gradle del esqueleto original.

## Implementado en esta base
- Proyecto Android con configuración Gradle Kotlin DSL, paquete `com.stockpro`, `minSdk 26`, `compileSdk 35`, JDK 17.
- Bienvenida animada básica (pulso, puntos y transición al panel).
- Tema oscuro verde/negro con acento lima.
- Navegación inferior Inicio, Inventario, Ventas, Compras y Más.
- Persistencia local Room para productos, categorías y movimientos de stock.
- Alta y edición de productos (nombre, referencia, código de barras como campo, unidad, stock, mínimo y notas).
- Búsqueda por nombre/referencia/código guardado y filtro de stock bajo.
- Movimientos manuales de entrada/salida registrados junto al stock dentro de una transacción Room; bloquea stock negativo.
- Indicadores básicos del panel calculados desde la base local.

## No implementado todavía
- TPV, carrito, ventas, pagos, devoluciones y ticket digital.
- Compras y recepción, proveedores, caja y reportes.
- Moneda predeterminada CUP (pesos cubanos); los precios comerciales y cálculos monetarios aún no están implementados y deberán usar unidades monetarias menores enteras.
- Impuestos configurables.
- Escaneo de códigos de barras con cámara (por ahora el campo puede escribirse manualmente).
- Exportación/restauración de copias y recordatorios.
- Categorías administrables desde la UI.
- Seguridad/roles, bloqueo de sesión, auditoría reforzada.
- Migraciones Room de producción; el fallback destructivo es temporal para desarrollo y debe eliminarse antes de uso real.
- Pruebas automatizadas, pruebas en dispositivo, accesibilidad completa, APK firmado y CI.
- Impresión física de tickets, reservada para versión 2.

## Limitaciones y precauciones
- **No usar aún para operaciones comerciales reales.** Es una base de desarrollo, no un TPV completo.
- La creación y edición de producto con su ajuste de stock se han agrupado en métodos transaccionales; deben verificarse con pruebas automatizadas antes de uso comercial.
- Las referencias y códigos de barras no vacíos se validan contra duplicados en la capa DAO; deben añadirse pruebas para confirmar todos los casos límite.
- No se ha verificado la compilación. En el entorno de trabajo no se encontró Gradle instalado, SDK Android ni Gradle Wrapper, por lo que no se afirma que compile.

## Requisitos para compilar
- Android Studio compatible con AGP 8.7.3.
- JDK 17.
- Android SDK Platform 35.
- Primera sincronización con conexión para descargar dependencias.

Abre la carpeta `StockPro_reconstruido` en Android Studio y deja que Gradle sincronice. No incluye `gradlew` ni `gradle-wrapper.jar` porque no se pudo generar un wrapper verificado en este entorno.

## Decisiones preservadas
- Aplicación independiente: no depende de otros proyectos.
- Offline-first: SQLite local como fuente de verdad.
- Interfaz en español.
- Moneda predeterminada: CUP (pesos cubanos). Los módulos de precios y ventas aún están pendientes.
- Impuestos configurables pendientes.
- Copias: exportación manual y recordatorios pendientes.
- Código de barras: cámara pendiente.
- Impresión de tickets: versión 2.
- La fidelidad visual se ajustará cuando se reciban las próximas imágenes.
