package com.gunt.presentation.ui.util

import kotlin.math.roundToInt

fun getDiscountRatio(originalPrice: Int, discountedPrice: Int): Int {
    return (((originalPrice - discountedPrice).toDouble() / originalPrice) * 100f).roundToInt()
}