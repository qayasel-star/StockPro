# Auditoría de estado — StockPro

Última revisión: 2026-10-24. Esta auditoría cubre el contenido del ZIP recibido y la base independiente reconstruida desde los requisitos documentados.

## Hallazgos de la auditoría

1. El ZIP original solo contenía el contexto, un README y tres imágenes; no contenía código Kotlin, archivos Gradle ni Gradle Wrapper. La base actual se reconstruyó desde los requisitos textuales y no debe confundirse con una modificación de fuentes originales inexistentes en el ZIP.
2. Se detectó un riesgo de integridad en la edición del producto: un método transaccional podía devolver `Result.failure` después de haber escrito datos. Un retorno normal no provoca rollback de Room. Se cambió la operación para validar antes de escribir y registrar producto + movimiento en la misma transacción.
3. Se retiró `fallbackToDestructiveMigration()`: una futura actualización de esquema no debe borrar silenciosamente la base de datos. Cada cambio de esquema deberá incluir una migración explícita.
4. Se centralizaron las reglas aritméticas del stock en `StockRules` y se añadieron pruebas unitarias para entradas, salidas, bloqueo de stock negativo, desbordamiento y diferencias de inventario.
5. Se normalizan las referencias SKU a mayúsculas en la capa de presentación para reducir duplicados por diferencias de capitalización. La unicidad aún debe protegerse también a nivel de base de datos en una revisión de esquema con migración.
6. El acceso rápido «Añadir producto» del panel ahora debe abrir el formulario de alta, en vez de limitarse a navegar al inventario.
7. Se actualizó la decisión monetaria a CUP (pesos cubanos). Aún no existe módulo de precios/ventas, por lo que la moneda está documentada pero todavía no se usa en operaciones comerciales.

## Estado funcional actual

### Implementado en código (sin afirmar verificación de compilación)
- Proyecto Android nativo Kotlin, Jetpack Compose y Material 3.
- Tema oscuro con acento verde lima y bienvenida animada básica.
- Navegación inferior y panel de inicio con indicadores derivados de Room.
- Persistencia local Room para productos, categorías y movimientos de stock.
- Alta y edición de productos con nombre, SKU, código de barras manual, unidad, stock mínimo y notas.
- Búsqueda por nombre/SKU/código escrito y filtro de stock bajo.
- Movimientos manuales de entrada/salida; bloqueo de stock negativo por defecto.
- Validación aritmética aislada en `StockRules` con pruebas unitarias añadidas.
- CUP registrado como moneda predeterminada del producto, pendiente de aplicación al módulo comercial.

### Pendiente
- Ejecutar pruebas unitarias en un entorno Android/Gradle real; los tests están escritos, no ejecutados aquí.
- Compilar y ejecutar en emulador/dispositivo; no se dispone de Gradle, wrapper ni Android SDK en este entorno.
- Añadir índice único de SKU/código de barras con una migración compatible con datos existentes.
- Añadir operaciones de categorías desde la interfaz.
- Añadir precios, costes, descuentos y cálculos monetarios CUP sin errores de redondeo.
- TPV, carrito, ventas, pagos, devoluciones e historial con transacción atómica y protección contra doble confirmación.
- Compras/recepción, proveedores, caja, reportes y alertas basadas en operaciones reales.
- Impuestos configurables.
- Exportación/restauración manual de copias y recordatorios.
- Escáner de códigos de barras con cámara (CameraX/ML Kit).
- Migraciones Room versionadas, pruebas de recuperación, accesibilidad, privacidad y rendimiento.
- Ajustar la fidelidad visual y las animaciones cuando el usuario envíe las próximas referencias.
- APK firmado, CI y publicación en el repositorio independiente `StockPro`.
- Impresión física de tickets, reservada para versión 2.

## Entorno y verificación

En el entorno de trabajo no se encontró Gradle, Android SDK ni Gradle Wrapper. Por tanto, no se ejecutó una compilación real ni las pruebas JUnit; no se afirma que el proyecto compile o que exista un APK funcional. Para verificarlo, abrir el proyecto en Android Studio con JDK 17, Android SDK Platform 35 y sincronización Gradle disponible; ejecutar pruebas, `assembleDebug` y pruebas en dispositivo antes de usar datos comerciales reales.

## Regla permanente para futuras entregas

Antes de cada actualización: auditar el estado existente, requisitos nuevos, integridad de datos, seguridad, cálculos, compatibilidad/migraciones y pruebas. Después implementar, volver a auditar y documentar con claridad lo implementado, probado y pendiente. No declarar completada una función sin evidencia verificable.
