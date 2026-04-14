package org.example.utils

import kotlin.math.absoluteValue
import kotlin.math.sign

infix fun Int.ceilDiv(other: Int): Int = floorDiv(other) + rem(other).sign.absoluteValue