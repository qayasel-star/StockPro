package com.stockpro.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stockpro.data.ProductEntity
import com.stockpro.data.StockProDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockProViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = StockProDatabase.get(application).dao()
    val products = dao.observeProducts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val productCount = dao.observeProductCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val lowStockCount = dao.observeLowStockCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
    val categories = dao.observeCategories().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun saveProduct(id: Long?, name: String, sku: String, barcode: String, stock: Int, minStock: Int, unit: String, notes: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val cleanName = name.trim()
                require(cleanName.isNotEmpty()) { "El nombre es obligatorio." }
                require(stock >= 0) { "El stock inicial no puede ser negativo." }
                require(minStock >= 0) { "El stock mínimo no puede ser negativo." }
                val now = System.currentTimeMillis()
                val normalizedSku = sku.trim().uppercase()
                val normalizedBarcode = barcode.trim()
                if (id == null) {
                    dao.createProductWithInitialStock(ProductEntity(name = cleanName, sku = normalizedSku, barcode = normalizedBarcode, stockQuantity = 0, minimumStock = minStock, unit = unit.trim().ifBlank { "unidad" }, notes = notes.trim(), createdAt = now, updatedAt = now), stock)
                } else {
                    val old = dao.getProduct(id) ?: error("No se encontró el producto.")
                    val result = dao.updateProductAndStock(old.copy(name = cleanName, sku = normalizedSku, barcode = normalizedBarcode, minimumStock = minStock, unit = unit.trim().ifBlank { "unidad" }, notes = notes.trim(), updatedAt = now), stock)
                    result.getOrThrow()
                }
                onDone(null)
            } catch (e: Exception) {
                onDone(e.message ?: "No se pudo guardar el producto.")
            }
        }
    }

    fun adjustStock(id: Long, delta: Int, reason: String, onDone: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = dao.adjustStock(id, delta, if (delta >= 0) "ENTRADA" else "SALIDA", reason)
                onDone(result.exceptionOrNull()?.message)
            } catch (e: Exception) {
                onDone(e.message ?: "No se pudo registrar el movimiento.")
            }
        }
    }
}
