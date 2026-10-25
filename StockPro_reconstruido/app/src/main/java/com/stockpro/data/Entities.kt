package com.stockpro.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories", indices = [Index(value = ["name"], unique = true)])
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products", indices = [Index(value = ["sku"]), Index(value = ["barcode"])])
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sku: String = "",
    val barcode: String = "",
    val categoryId: Long? = null,
    val unit: String = "unidad",
    val stockQuantity: Int = 0,
    val minimumStock: Int = 0,
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "stock_movements", indices = [Index("productId"), Index("createdAt")])
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val quantityDelta: Int,
    val quantityBefore: Int,
    val quantityAfter: Int,
    val type: String,
    val reason: String,
    val createdAt: Long = System.currentTimeMillis()
)
