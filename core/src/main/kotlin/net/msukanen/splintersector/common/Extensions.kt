package net.msukanen.splintersector.common

import kotlin.random.Random

fun ClosedFloatingPointRange<Float>.random(): Float =
    Random.nextDouble(start.toDouble(), endInclusive.toDouble()).toFloat()
