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
internal infix fun Long.ceilDiv(other: Int): Int {
    val longRes = floorDiv(other) + rem(other).sign.absoluteValue
    return longRes.toIntOrThrow()
}

internal fun Long.toIntOrThrow(): Int {
    if (this !in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) throw LongNotInIntRangeException(this)
    return toInt()
}

class LongNotInIntRangeException(
    val long: Long
) : IllegalArgumentException(
    "$long cannot be converted to an Int as it is not in ${Int.MIN_VALUE}<=..<=${Int.MAX_VALUE} range"
)