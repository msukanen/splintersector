package net.msukanen.splintersector

import kotlin.random.Random

fun ClosedFloatingPointRange<Float>.random(): Float =
    Random.nextDouble(start.toDouble(), endInclusive.toDouble()).toFloat()
