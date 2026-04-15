package org.example.utils

import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.ranges.contains

/*
TODO:
 As it is expected to use this only for division of bodySize: Long on
 numOfParallelRequests: Int and get ChunkSize: Int,
 probably better to use value classes for bodySize, numOfParallelRequests, ChunkSize
*/
infix fun Long.ceilDiv(other: Int): Int {
    val longRes = floorDiv(other) + rem(other).sign.absoluteValue
    return longRes.toIntOrThrow()
}

fun Long.toIntOrThrow(): Int {
    if (this !in Int.MIN_VALUE..Int.MAX_VALUE) TODO("throw: too big chunk")
    return toInt()
}