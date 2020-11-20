@file:Suppress("unused")

package xyz.dean.androiddemos.utils

import java.io.Closeable

inline fun <P1 : Closeable?, P2 : Closeable?, R> use(
    c1: P1, c2: P2,
    block: (P1, P2) -> R
): R {
    return c1.use { c2.use { block(c1, c2) } }
}

inline fun <P1 : Closeable?, P2 : Closeable?, P3 : Closeable?, R> use(
    c1: P1, c2: P2, c3: P3,
    block: (P1, P2, P3) -> R
): R {
    return c1.use { c2.use { c3.use { block(c1, c2, c3) } } }
}