package com.gunt.presentation.ui.util

import kotlin.math.roundToInt

fun discountRatio(originalPrice:Int, discountedPrice:Int): Int{
    return (((originalPrice - discountedPrice).toDouble() / originalPrice)*100f).roundToInt()
}