# StockPro — Paquete de continuidad del proyecto

Este archivo resume las decisiones y el estado del proyecto para continuar desde otro dispositivo o cuenta. No es una exportación literal de todo el historial del chat; incluye los requisitos y acuerdos técnicos importantes.

## Identidad y objetivo
- Nombre: **StockPro**.
- Aplicación Android nativa para una tienda o comercio general.
- Dispositivo inicial: un teléfono Android.
- Prioridad: **offline-first**, usando almacenamiento local como fuente de verdad.
- Repositorio GitHub independiente: `StockPro`.
- Interfaz en español.
- Mantener StockPro separado de todos los demás proyectos.

## Preferencias confirmadas
- Moneda predeterminada: CUP (pesos cubanos); los importes comerciales deben usar unidades monetarias menores enteras cuando se implementen.
- Impuestos: configurables por negocio.
- Copias de seguridad: exportación manual y recordatorios.
- Códigos de barras: lectura con cámara (pendiente de implementar).
- Impresión de tickets: segunda versión.
- El usuario quiere recibir el **código completo**, no solo fragmentos o cambios parciales.
- El usuario autorizó continuar el desarrollo sin esperar a las próximas imágenes; cuando las envíe, se hará una auditoría visual conjunta y se ajustará la interfaz. No es necesario detener el desarrollo mientras tanto.

## Referencias visuales incluidas
1. `Referencias_visuales/referencia_bienvenida.png`: splash/bienvenida con fondo verde oscuro, aros y brillos verdes, logotipo de cubo/caja con flecha ascendente, texto StockPro (Stock blanco, Pro verde lima), eslogan «Tu negocio en buenas manos», iconos de módulos, barra de carga y frase «Más que un inventario... ¡es tu aliado de negocio!». Debe ser animada profesionalmente.
2. `Referencias_visuales/referencia_panel_general.png`: presentación de múltiples pantallas: acceso, panel principal, inventario, TPV/ventas, compras, proveedores, reportes, alertas, configuración, seguridad y licencia. Estilo oscuro con verde lima, tarjetas redondeadas.
3. `Referencias_visuales/referencia_inventario_productos.jpg`: pantalla Inventario con encabezado, búsqueda, botón `+`, filtros «Todos», «Categorías», «Stock bajo», lista con icono/nombre/stock/flecha y navegación inferior Inicio, Inventario, Ventas, Compras y Más. Los productos y cantidades de la imagen son ejemplos ilustrativos, no datos reales.

## Módulos previstos
1. Bienvenida animada.
2. Inicio/configuración inicial local.
3. Panel principal.
4. Inventario y catálogo.
5. Ficha de producto.
6. Movimientos de inventario: entradas, salidas, ajustes, devoluciones y motivo.
7. TPV/carrito.
8. Cobro y ticket digital.
9. Historial de ventas y devoluciones.
10. Compras y recepción.
11. Proveedores.
12. Caja: apertura, movimientos y cierre/arqueo.
13. Análisis y reportes.
14. Alertas de stock mínimo/agotar; vencimientos si se implementan lotes y fechas.
15. Exportación/restauración de copias.
16. Configuración, moneda, impuestos y seguridad.

## Reglas de negocio importantes
- Confirmar una venta en una transacción: cabecera, líneas, pagos y movimientos de stock; no dejar operaciones parciales.
- Guardar dinero como unidades monetarias menores enteras una vez elegida la moneda.
- Guardar instantáneas de precio y coste en las líneas de venta.
- Corregir inventario con movimientos compensatorios; no borrar historial.
- Identificadores/números de ticket únicos y protección contra doble confirmación.
- Bloquear stock negativo por defecto.
- Reportes calculados a partir de operaciones registradas.
- Mantener el stock actual consistente con el historial de movimientos.

## Tecnología y esqueleto generado
La carpeta `StockPro/` contiene el código inicial:
- Kotlin, Jetpack Compose, Material 3.
- Navigation Compose.
- Room/SQLite.
- Tema oscuro verde/negro con verde lima.
- Splash animada básica.
- Navegación inferior Inicio, Inventario, Ventas, Compras y Más.
- Entidades Room iniciales: productos, categorías y movimientos.
- DAO y base de datos local.
- README y documentación inicial.
- Workflow de GitHub Actions que requiere generar el Gradle Wrapper.

Configuración prevista: paquete `com.stockpro`, Android Gradle Plugin 8.7.3, Kotlin 2.0.21, KSP 2.0.21-1.0.28, JDK 17, compileSdk 35, minSdk 26, Room 2.6.1, Navigation Compose 2.8.5.

## Estado real: no afirmar más de lo implementado
**Incluido:** esqueleto Android, tema, splash, navegación de muestra, pantallas placeholder, entidades/DAO/base Room inicial y documentación.

**Pendiente:** CRUD completo, lógica de inventario, TPV/ventas, compras, proveedores, caja, reportes reales, alertas basadas en datos, configuración de moneda e impuestos, exportación/restauración, recordatorios, lectura de códigos con cámara, pruebas automatizadas, migraciones, accesibilidad, pruebas en dispositivo y APK firmado. Impresión de tickets queda para versión 2.

**No se ha verificado una compilación real.** El ZIP inicial no incluye `gradlew`, `gradlew.bat` ni `gradle/wrapper/gradle-wrapper.jar`; generar el wrapper desde Android Studio o con Gradle instalado antes de usar CI. No afirmar que ya hay un APK funcional.

## Próximos pasos acordados
1. Continuar el desarrollo por módulos sin esperar a las próximas fotos.
2. Antes de cada actualización, auditar código, requisitos, integridad de datos, cálculos, seguridad y pruebas.
3. Completar inventario y movimientos con pruebas de integridad.
4. Implementar después TPV, ventas, compras, proveedores, caja y reportes.
5. Añadir copias de seguridad y escáner con cámara.
6. Cuando lleguen más imágenes, analizarlas en conjunto y ajustar el diseño con fidelidad.
7. Generar wrapper, compilar y probar en un dispositivo antes de dar el APK por listo.
8. Publicar en el repositorio GitHub independiente `StockPro`.

## Instrucción para iniciar el nuevo chat
«Continúa StockPro como proyecto independiente. Antes de cada cambio, audita lo existente y lo nuevo. Entrega el código completo y distingue lo implementado, probado y pendiente. Las imágenes adicionales se incorporarán cuando estén disponibles. No afirmes que el APK compila hasta verificarlo realmente.»
