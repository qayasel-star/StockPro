package com.stockpro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class StockProDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name COLLATE NOCASE")
    abstract fun observeProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    abstract suspend fun getProduct(id: Long): ProductEntity?

    @Query("SELECT id FROM products WHERE :sku != '' AND sku = :sku AND id != :excludeId LIMIT 1")
    abstract suspend fun findSkuConflict(sku: String, excludeId: Long): Long?

    @Query("SELECT id FROM products WHERE :barcode != '' AND barcode = :barcode AND id != :excludeId LIMIT 1")
    abstract suspend fun findBarcodeConflict(barcode: String, excludeId: Long): Long?

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    abstract fun observeProduct(id: Long): Flow<ProductEntity?>

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    abstract fun observeProductCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1 AND stockQuantity <= minimumStock")
    abstract fun observeLowStockCount(): Flow<Int>

    @Query("SELECT * FROM categories ORDER BY name COLLATE NOCASE")
    abstract fun observeCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertProduct(product: ProductEntity): Long

    @Update
    abstract suspend fun updateProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertCategory(category: CategoryEntity): Long

    @Transaction
    open suspend fun createProductWithInitialStock(product: ProductEntity, initialStock: Int): Long {
        require(initialStock >= 0) { "El stock inicial no puede ser negativo." }
        require(findSkuConflict(product.sku, 0) == null) { "Ya existe un producto con esa referencia." }
        require(findBarcodeConflict(product.barcode, 0) == null) { "Ya existe un producto con ese código de barras." }
        val id = insertProduct(product.copy(stockQuantity = 0))
        if (initialStock > 0) adjustStock(id, initialStock, "INICIAL", "Stock inicial").getOrThrow()
        return id
    }

    @Transaction
    open suspend fun updateProductAndStock(updated: ProductEntity, requestedStock: Int): Result<Unit> {
        if (requestedStock < 0) return Result.failure(IllegalArgumentException("El stock no puede ser negativo."))
        val old = getProduct(updated.id) ?: return Result.failure(IllegalArgumentException("No se encontró el producto."))
        if (findSkuConflict(updated.sku, updated.id) != null) return Result.failure(IllegalArgumentException("Ya existe un producto con esa referencia."))
        if (findBarcodeConflict(updated.barcode, updated.id) != null) return Result.failure(IllegalArgumentException("Ya existe un producto con ese código de barras."))

        // Validate everything before the first write. A failed Result does not roll back a Room transaction;
        // only an exception does. Persist the product and its stock movement as one atomic operation.
        val delta = try {
            StockRules.calculateDelta(old.stockQuantity, requestedStock)
        } catch (e: IllegalArgumentException) {
            return Result.failure(e)
        }
        updateProduct(updated.copy(stockQuantity = requestedStock, createdAt = old.createdAt, updatedAt = System.currentTimeMillis()))
        if (delta != 0) {
            insertMovement(
                StockMovementEntity(
                    productId = updated.id,
                    quantityDelta = delta,
                    quantityBefore = old.stockQuantity,
                    quantityAfter = requestedStock,
                    type = "AJUSTE",
                    reason = "Ajuste desde ficha de producto"
                )
            )
        }
        return Result.success(Unit)
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertMovement(movement: StockMovementEntity): Long

    @Query("SELECT * FROM stock_movements WHERE productId = :productId ORDER BY createdAt DESC")
    abstract fun observeMovements(productId: Long): Flow<List<StockMovementEntity>>

    @Transaction
    open suspend fun adjustStock(productId: Long, delta: Int, type: String, reason: String, allowNegative: Boolean = false): Result<Unit> {
        if (delta == 0) return Result.failure(IllegalArgumentException("El movimiento debe cambiar la cantidad de stock."))
        if (reason.isBlank()) return Result.failure(IllegalArgumentException("El motivo del movimiento es obligatorio."))
        val product = getProduct(productId) ?: return Result.failure(IllegalArgumentException("El producto ya no existe."))
        val after = try {
            StockRules.calculateAfter(product.stockQuantity, delta, allowNegative)
        } catch (e: IllegalArgumentException) {
            return Result.failure(e)
        } catch (e: IllegalStateException) {
            return Result.failure(e)
        }
        updateProduct(product.copy(stockQuantity = after, updatedAt = System.currentTimeMillis()))
        insertMovement(StockMovementEntity(productId = productId, quantityDelta = delta, quantityBefore = product.stockQuantity, quantityAfter = after, type = type, reason = reason.trim()))
        return Result.success(Unit)
    }
}
