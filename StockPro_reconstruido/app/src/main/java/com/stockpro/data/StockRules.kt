package com.stockpro.data

/** Reglas puras de inventario para mantener validaciones deterministas y fáciles de probar. */
object StockRules {
    fun calculateAfter(current: Int, delta: Int, allowNegative: Boolean = false): Int {
        val result = current.toLong() + delta.toLong()
        require(result in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
            "La cantidad resultante está fuera del rango permitido."
        }
        check(allowNegative || result >= 0L) {
            "El movimiento dejaría el stock en negativo."
        }
        return result.toInt()
    }

    fun calculateDelta(current: Int, requested: Int): Int {
        require(current >= 0) { "El stock actual no puede ser negativo." }
        require(requested >= 0) { "El stock no puede ser negativo." }
        val delta = requested.toLong() - current.toLong()
        require(delta in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) {
            "El ajuste de stock está fuera del rango permitido."
        }
        return delta.toInt()
    }
}
