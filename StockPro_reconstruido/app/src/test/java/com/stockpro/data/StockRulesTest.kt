package com.stockpro.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class StockRulesTest {
    @Test fun incomingMovementAddsStock() {
        assertEquals(12, StockRules.calculateAfter(7, 5))
    }

    @Test fun outgoingMovementSubtractsStock() {
        assertEquals(3, StockRules.calculateAfter(7, -4))
    }

    @Test fun negativeStockIsRejectedByDefault() {
        assertThrows(IllegalStateException::class.java) { StockRules.calculateAfter(2, -3) }
    }

    @Test fun negativeStockCanOnlyBeAllowedExplicitly() {
        assertEquals(-1, StockRules.calculateAfter(2, -3, allowNegative = true))
    }

    @Test fun integerOverflowIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { StockRules.calculateAfter(Int.MAX_VALUE, 1) }
    }

    @Test fun requestedStockCreatesCorrectDelta() {
        assertEquals(-6, StockRules.calculateDelta(10, 4))
        assertEquals(0, StockRules.calculateDelta(4, 4))
        assertEquals(6, StockRules.calculateDelta(4, 10))
    }

    @Test fun negativeRequestedStockIsRejected() {
        assertThrows(IllegalArgumentException::class.java) { StockRules.calculateDelta(3, -1) }
    }
}
