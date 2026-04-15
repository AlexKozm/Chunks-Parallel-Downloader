package org.example.storage

class UnionOfRangesIsNotContinuous(
    val space: LongRange
) : IllegalArgumentException("Range is not continues. It has a space: $space")